package model

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginStartRequest(
    val phoneNumber: String
)

@Serializable
data class LoginStartResponse(
    val phoneNumber: String,
    val authParams: AuthParameters,
    val cookies: List<SerialCookie>
)

@Serializable
data class AuthParameters(
    val clientId: String,
    val responseType: String,
    val redirectUri: String,
    val scope: String,
    val audience: String,
    val state: String,
    val csrf: String
)

@Serializable
data class SerialCookie(
    val requestUrl: String,
    val name: String,
    val value: String,
    val encoding: CookieEncoding,
    val maxAge: Int,
    val domain: String?,
    val path: String?,
    val secure: Boolean = false,
    val httpOnly: Boolean = false
)

fun SerialCookie.toKtor() = requestUrl to Cookie(
    name = name,
    value = value,
    encoding = encoding,
    maxAge = maxAge,
    domain = domain,
    path = path,
    secure = secure,
    httpOnly = httpOnly
)

fun Pair<String, Cookie>.toSerial() = SerialCookie(
    requestUrl = first,
    name = second.name,
    value = second.value,
    encoding = second.encoding,
    maxAge = second.maxAge,
    domain = second.domain,
    path = second.path,
    secure = second.secure,
    httpOnly = second.httpOnly
)

@Serializable
data class LoginEndRequest(
    val verificationCode: String,
    val startResponse: LoginStartResponse
)
