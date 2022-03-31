@file:Suppress("NON_EXPORTABLE_TYPE")

package views.booking

import Information
import ResponseState
import api.Api
import api.Loader
import error
import eventHandler
import inputHandler
import io.ktor.http.*
import kotlinx.css.*
import kotlinx.datetime.Clock
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import loading
import model.BookingResponse
import model.CreateBookingRequest
import model.User
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledP
import styles.AppStyles
import styles.SpinnerStyle
import success
import kotlin.time.Duration.Companion.days

external interface BookingProps : RProps {
    var api: Api
    var user: User
    var onForbiddenRequest: () -> Unit
}

external interface BookingState : RState {
    var showDeleted: Boolean
    var bookingData: ResponseState<List<BookingResponse>>
}

@JsExport
class BookingView(props: BookingProps) : RComponent<BookingProps, BookingState>(props) {
    override fun BookingState.init(props: BookingProps) {
        showDeleted = false
        bookingData = ResponseState.Loading
    }

    private val bookingLoader = Loader()
        .onLoading {
            if (it) setState { bookingData = ResponseState.Loading }
        }.handleError {
            setState { bookingData = ResponseState.Error(it.message ?: "Unknown error") }
        }.onApiError(HttpStatusCode.Unauthorized) {
            props.onForbiddenRequest()
        }

    override fun componentDidMount() = bookingLoader.load { getBookings() }


    private suspend fun getBookings() {
        props.api.getBookings().let {
            setState { bookingData = ResponseState.Success(it.bookings) }
        }
    }

    private val createBooking = { request: CreateBookingRequest ->
        bookingLoader.load {
            props.api.createBooking(request)
            getBookings()
        }
    }

    private val deleteBooking = { bookingId: String ->
        bookingLoader.load {
            props.api.deleteBooking(bookingId)
            getBookings()
        }
    }

    private val onRefresh = eventHandler {
        bookingLoader.load { getBookings() }
    }

    private val onShowDeletedChange = inputHandler {
        setState { showDeleted = it.checked }
    }

    private fun RBuilder.spinner() =
        styledDiv { css { +SpinnerStyle.spinner } }

    override fun RBuilder.render() {
        styledDiv {
            css { +AppStyles.section }
            state.bookingData.error()?.let {
                styledP {
                    css { +AppStyles.error }
                    +it.msg
                }
                button {
                    +"Refresh Bookings"
                    attrs { onClickFunction = onRefresh }
                }
            }
            styledDiv {
                css {
                    display = Display.flex
                    flexWrap = FlexWrap.wrap
                    justifyContent = JustifyContent.spaceEvenly
                    children { padding(10.px) }
                }
                div {
                    h4 { +"Information" }
                    ul { Information.info.forEach { li { +it } } }
                }
                div {
                    h4 { +"View" }
                    label { +"Deleted bookings: " }
                    input {
                        attrs["checked"] = state.showDeleted  // Workaround for react warning about controlled comp.
                        attrs {
                            type = InputType.checkBox
                            onChangeFunction = onShowDeletedChange
                        }
                    }
                }
            }
        }
        styledDiv {
            css { +AppStyles.section }
            h2 { +"Apartment ${props.user.apartmentNumber}" }

            state.bookingData.loading()?.let { spinner() }
            state.bookingData.success()?.let {
                bookingTable(
                    showDate = true,
                    showPhone = false,
                    showDeleted = state.showDeleted,
                    bookings = it.filter { booking ->
                        booking.startTime > Clock.System.now() - days(1) &&
                                booking.creator.user.apartmentNumber == props.user.apartmentNumber
                    },
                    user = props.user,
                    onDeleteBooking = deleteBooking
                )
            }
        }
        styledDiv {
            css { +AppStyles.section }
            h2 { +"Book" }
            bookingCreate(
                user = props.user,
                bookings = state.bookingData,
                showDeleted = state.showDeleted,
                onCreateBooking = createBooking,
                onDeleteBooking = deleteBooking
            )
        }
    }
}

fun RBuilder.bookingView(
    api: Api,
    user: User,
    onForbiddenRequest: () -> Unit
) = child(BookingView::class) {
    attrs {
        this.api = api
        this.user = user
        this.onForbiddenRequest = onForbiddenRequest
    }
}