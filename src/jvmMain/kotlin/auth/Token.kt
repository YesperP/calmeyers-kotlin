package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.TokenResponse
import model.User
import tables.UserTable
import java.util.*
import kotlin.time.Duration.Companion.days

object Token {
    private const val issuer = "calmeyers"
    private val expirationTime = days(7)
    private val algorithm = Algorithm.HMAC256("So secret key for calmeyers")

    private fun Instant.toJavaDate() = Date.from(this.toJavaInstant())

    suspend fun createTokenResponse(user: User, userTable: UserTable): TokenResponse {
        userTable.putUser(user)
        val token = sign(user)
        return TokenResponse(token)
    }

    private fun sign(user: User): String =
        JWT.create()
            .withIssuer(issuer)
            .withClaim("user", Json.encodeToString(user))
            .withExpiresAt(Clock.System.now().plus(expirationTime).toJavaDate())
            .sign(algorithm)

    fun verify(token: String): User =
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
            .verify(token)
            .getClaim("user")
            .let {
                Json.decodeFromString(it.asString())
            }
}