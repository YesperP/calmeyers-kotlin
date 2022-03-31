package auth

import io.ktor.client.features.cookies.*
import io.ktor.http.*

class MyCookieStorage : CookiesStorage {
    private val cookieStorage = AcceptAllCookiesStorage()
    private val _cookies = mutableListOf<Pair<String, Cookie>>()
    val cookies: List<Pair<String, Cookie>>
        get() = _cookies

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        _cookies.add(requestUrl.toString() to cookie)
        cookieStorage.addCookie(requestUrl, cookie)
    }

    suspend fun addCookie(requestUrl: String, cookie: Cookie) = cookieStorage.addCookie(requestUrl, cookie)
    override fun close() = cookieStorage.close()
    override suspend fun get(requestUrl: Url) = cookieStorage.get(requestUrl)
}
