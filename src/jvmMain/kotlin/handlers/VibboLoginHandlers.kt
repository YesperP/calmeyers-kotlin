package handlers

import auth.MyCookieStorage
import auth.Token
import com.amazonaws.services.lambda.runtime.Context
import getUserInfo
import io.ktor.client.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import model.*
import tables.UserTable


private fun Parameters.getValue(name: String): String {
    return get(name)?.decodeURLQueryComponent() ?: throw Exception("$name not in parameters.")
}

private suspend fun initAuth(client: HttpClient, cookieStorage: MyCookieStorage): AuthParameters {
    val initResponse: HttpResponse = client.get("https://vibbo.no/auth/login")
    val authParams = initResponse.request.url.parameters
    println("Intercepted auth parameters: $authParams")

    return AuthParameters(
        authParams.getValue("client"),
        authParams.getValue("response_type"),
        authParams.getValue("redirect_uri"),
        authParams.getValue("scope"),
        authParams.getValue("audience"),
        authParams.getValue("state"),
        cookieStorage.cookies.map { it.second }.find { it.name == "_csrf" }?.value
            ?: throw Exception("Could not find _csrf in cookies")
    )
}

suspend fun triggerSms(client: HttpClient, authParameters: AuthParameters, phoneNumber: String) {
    client.post<Unit>("https://innlogging.obos.no/passwordless/start") {
        json {
            put("client_id", authParameters.clientId)
            put("connection", "sms")
            put("send", "code")
            put("phone_number", phoneNumber)
            putJsonObject("authParams") {
                put("response_type", authParameters.responseType)
                put("redirect_uri", authParameters.redirectUri)
                put("scope", authParameters.scope)
                put("audience", authParameters.audience)
                put("state", authParameters.state)
                put("_csrf", authParameters.csrf)
            }
        }
    }
}

suspend fun tokenStart(loginStartRequest: LoginStartRequest): LoginStartResponse {
    val cookieStorage = MyCookieStorage()
    val client = HttpClient {
        install(HttpCookies) { storage = cookieStorage }
        install(JsonFeature) { serializer = KotlinxSerializer() }
    }
    val initResult = initAuth(client, cookieStorage)
    triggerSms(client, initResult, loginStartRequest.phoneNumber)
    return LoginStartResponse(
        loginStartRequest.phoneNumber,
        initResult,
        cookieStorage.cookies.map { it.toSerial() }
    )
}

suspend fun verifyCode(client: HttpClient, request: LoginEndRequest) {
    client.post<Unit>("https://innlogging.obos.no/passwordless/verify") {
        json {
            put("connection", "sms")
            put("phone_number", request.startResponse.phoneNumber)
            put("verification_code", request.verificationCode)
        }
    }
}

suspend fun authenticate(client: HttpClient, request: LoginEndRequest) {
    client.get<HttpResponse>("https://innlogging.obos.no/passwordless/verify_redirect") {
        parameter("client_id", request.startResponse.authParams.clientId)
        parameter("response_type", request.startResponse.authParams.responseType)
        parameter("redirect_uri", request.startResponse.authParams.redirectUri)
        parameter("scope", request.startResponse.authParams.scope)
        parameter("audience", request.startResponse.authParams.audience)
        parameter("state", request.startResponse.authParams.state)
        parameter("_csrf", request.startResponse.authParams.csrf)
        parameter("protocol", "oauth2")
        parameter("connection", "sms")
        parameter("phone_number", request.startResponse.phoneNumber)
        parameter("verification_code", request.verificationCode)
    }
}

suspend fun tokenEnd(request: LoginEndRequest): TokenResponse {
    val cookieStorage = MyCookieStorage()
    val client = HttpClient {
        install(HttpCookies) { storage = cookieStorage }
        install(JsonFeature) { serializer = KotlinxSerializer() }
    }
    request.startResponse.cookies.map { it.toKtor() }.forEach {
        cookieStorage.addCookie(it.first, it.second)
    }
    verifyCode(client, request)
    authenticate(client, request)
    val user = getUserInfo(client)
    println("User: $user")
    return Token.createTokenResponse(user, UserTable())
}

class VibboStartHandler : AwsApiHandler() {
    override suspend fun handleRequest(input: ApiRequest, context: Context) =
        encodeBody(tokenStart(decodeBody(input)))
}

class VibboEndHandler : AwsApiHandler() {
    override suspend fun handleRequest(input: ApiRequest, context: Context) =
        encodeBody(tokenEnd(decodeBody(input)))
}

//Test:
suspend fun main() {
    val result = tokenStart(LoginStartRequest("+47XXXXXXXX"))
    println(result)
    print("Code: ")
    val code = readLine()!!
    val response = tokenEnd(LoginEndRequest(code, result))
    println(response)
    println(Token.verify(response.token))
}