import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RComponent

fun eventHandler(block: (evt: Event) -> Unit) = { evt: Event ->
    block(evt).let { }
}

fun inputHandler(block: (evt: HTMLInputElement) -> Unit) = { evt: Event ->
    block(evt.target as HTMLInputElement).let { }
}

fun formSubmitHandler(block: () -> Unit) = { evt: Event ->
    evt.preventDefault()
    block().let { }
}

sealed class ResponseState<out T> {
    object None : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
    data class Success<T>(val data: T) : ResponseState<T>()
    data class Error(val msg: String) : ResponseState<Nothing>()
}

inline fun <reified T> ResponseState<T>.none() = this as? ResponseState.None
inline fun <reified T> ResponseState<T>.loading() = this as? ResponseState.Loading
inline fun <reified T> ResponseState<T>.success() = (this as? ResponseState.Success<T>)?.data
inline fun <reified T> ResponseState<T>.error() = (this as? ResponseState.Error)

inline fun <reified T> ResponseState<T>.isNone() = this is ResponseState.None
inline fun <reified T> ResponseState<T>.isLoading() = this is ResponseState.Loading
inline fun <reified T> ResponseState<T>.isSuccess() = this is ResponseState.Success
inline fun <reified T> ResponseState<T>.isError() = this is ResponseState.Error


/**
 * React wrappers don't support getDerivedStateFromProps. This can be used as a fix.
 * Should be called from S.init()
 */
inline fun <P, S, reified T : RComponent<P, S>> T.setGetDerivedStateFromProps(
    noinline block: (p: P, s: S) -> Unit
) {
    T::class.js.asDynamic().getDerivedStateFromProps = block
}
