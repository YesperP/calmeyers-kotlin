package handlers

import auth.Token
import com.amazonaws.services.lambda.runtime.Context
import kotlinx.serialization.encodeToString
import model.LoginAltRequest
import model.TokenResponse
import model.User
import tables.UserTable
import validations.LoginAltValidation

private suspend fun loginAlt(request: LoginAltRequest): TokenResponse {
    LoginAltValidation.validate(request)
    val user = User(
        userId = request.phoneNumber,
        firstName = request.firstName,
        lastName = request.lastName,
        phoneNumber = request.phoneNumber,
        email = null,
        apartmentNumber = request.apartmentNumber.toInt(),
        admin = false
    )
    return Token.createTokenResponse(user, UserTable())
}

class LoginAltHandler : AwsApiHandler() {
    override suspend fun handleRequest(input: ApiRequest, context: Context) =
        encodeBody(loginAlt(decodeBody(input)))
}