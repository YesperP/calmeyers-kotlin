package api

import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class Loader {
    companion object {
        private val scope = MainScope()
    }

    private val errorHandlers = mutableMapOf<HttpStatusCode, ((e: ResponseException) -> Unit)>()
    private var onLoading: ((Boolean) -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null

    fun onApiError(statusCode: HttpStatusCode, block: (e: ResponseException) -> Unit): Loader {
        errorHandlers[statusCode] = block
        return this
    }

    fun onLoading(block: (loading: Boolean) -> Unit): Loader {
        onLoading = block
        return this
    }

    fun handleError(block: (e: Exception) -> Unit): Loader {
        onError = block
        return this
    }

    fun load(op: suspend CoroutineScope.() -> Unit) {
        scope.launch {
            onLoading?.invoke(true)
            try {
                this.op()
                onLoading?.invoke(false)
            } catch (e: Exception) {
                onLoading?.invoke(false)
                (e as? ResponseException)?.let {
                    errorHandlers[e.response.status]?.invoke(e)
                } ?: run {
                    onError?.invoke(e) ?: throw e
                }
            }
        }
    }
}