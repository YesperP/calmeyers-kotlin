package handlers

import auth.Token
import com.amazonaws.services.lambda.runtime.Context
import model.MissingAuthException
import model.User

abstract class AwsApiHandlerAuth : AwsApiHandler() {
    private fun getAuth(input: ApiRequest): User {
        return input.headers.authorization?.let {
            it.split("Bearer ").getOrNull(1) ?: throw MissingAuthException("Malformed header")
        }?.let {
            try {
                Token.verify(it)
            } catch (e: Exception) {
                e.printStackTrace()
                throw MissingAuthException("Token invalid")
            }
        } ?: throw MissingAuthException("No authorization")
    }

    override suspend fun handleRequest(input: ApiRequest, context: Context): String {
        val user = getAuth(input)
        return handleAuthRequest(input, context, user)
    }

    abstract suspend fun handleAuthRequest(input: ApiRequest, context: Context, user: User): String
}