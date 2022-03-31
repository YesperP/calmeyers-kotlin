import kotlinx.browser.window
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.User

@Serializable
data class TokenPayload(
    val iss: String,
    val exp: Int,
    val user: String
)

object TokenStore {
    private val json = Json { ignoreUnknownKeys = true }
    private const val KEY = "token"

    fun getToken() = window.localStorage.getItem(KEY)
    fun setToken(token: String) = window.localStorage.setItem(KEY, token)
    fun clearToken() = window.localStorage.removeItem(KEY)

    fun toUser(token: String): User {
        val payloadBase64 = token
            .split(".")[1]
            .replace('_', '/')
            .replace('-', '+')
        val payloadRaw = window.atob(payloadBase64)
        val payload = json.decodeFromString<TokenPayload>(payloadRaw)
        return json.decodeFromString(payload.user)
    }
}