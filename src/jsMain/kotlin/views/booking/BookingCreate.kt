@file:Suppress("NON_EXPORTABLE_TYPE")

package views.booking

import ResponseState
import Settings
import formSubmitHandler
import isSuccess
import kotlinx.css.*
import kotlinx.datetime.*
import kotlinx.html.js.onSubmitFunction
import loading
import model.BookingResponse
import model.CreateBookingRequest
import model.User
import model.from
import react.*
import react.dom.attrs
import styled.*
import styles.AppStyles
import styles.InputStyles
import success
import utils.withDate
import validations.CreateBookingValidation
import views.spinner
import kotlin.time.Duration.Companion.minutes

external interface BookingCreateProps : RProps {
    var bookings: ResponseState<List<BookingResponse>>
    var user: User
    var showDeleted: Boolean
    var onCreateBooking: (CreateBookingRequest) -> Unit
    var onDeleteBooking: (bookingId: String) -> Unit
}

external interface BookingCreateState : RState {
    var date: LocalDate
    var startTime: Instant
    var endTime: Instant
}

@JsExport
class BookingCreate(props: BookingCreateProps) : RComponent<BookingCreateProps, BookingCreateState>(props) {
    companion object {
        private val zone = TimeZone.currentSystemDefault()

        private fun roundUpMinute(time: Instant, zone: TimeZone, minuteStep: Int): Instant {
            val minute = time.toLocalDateTime(zone).minute
            val roundMinute = (0..60 step minuteStep)  // E.g. 0, 30, 60
                .map { it - minute }
                .filter { it >= 0 }
                .minOrNull()!!
            return time.plus(minutes(roundMinute))
        }

        private fun initTime(): Pair<Instant, Instant> {
            val nowTime = Clock.System.now()
            val zone = TimeZone.currentSystemDefault()
            val nowTimeStart = roundUpMinute(nowTime, zone, Settings.minuteStep)
            val nowTimeEnd = nowTimeStart + Settings.minDuration
            val startTime = when {
                Settings.minTime > nowTimeStart -> {
                    Instant.from(nowTime.toLocalDateTime(zone).date, Settings.minTime)
                }
                Settings.maxTime < nowTimeEnd -> {
                    Instant.from(nowTime.toLocalDateTime(zone).date, Settings.minTime) // Next day?
                }
                else -> nowTimeStart
            }
            return startTime to startTime + Settings.minDuration
        }
    }

    override fun BookingCreateState.init(props: BookingCreateProps) {
        println("BookingCreateState init")
        val initTime = initTime()
        startTime = initTime.first
        endTime = initTime.second
        date = initTime.first.toLocalDateTime(zone).date
    }

    private val onDateChanged = { date: LocalDate ->
        setState {
            this.date = date
            startTime = startTime.withDate(date, zone)
            endTime = endTime.withDate(date, zone)
        }
    }

    private val onTimeChanged = { startTime: Instant, endTime: Instant ->
        setState {
            this.startTime = startTime
            this.endTime = endTime
        }
    }


    private fun bookingValidation(newBooking: CreateBookingRequest, bookings: List<BookingResponse>) =
        try {
            CreateBookingValidation.validateAll(props.user, newBooking, bookings).let { null }
        } catch (e: Exception) {
            e.message
        }

    override fun RBuilder.render() {
        println("BookingCreate render: ${state.startTime} - ${state.endTime}")
        val newBooking = CreateBookingRequest(state.startTime, state.endTime)

        styledForm {
            attrs {
                onSubmitFunction = formSubmitHandler {
                    props.onCreateBooking(newBooking)
                }
            }

            styledDiv {
                css {
                    display = Display.flex
                    flexWrap = FlexWrap.wrap
                    justifyContent = JustifyContent.center
                    alignItems = Align.stretch
                    children { margin(bottom = 10.px, right = 5.px, left = 5.px) }
                }
                styledDiv {
                    css {
                        +AppStyles.subSection
                    }
                    datePicker(date = state.date, onDateChanged = onDateChanged)
                }
                styledDiv {
                    css {
                        +AppStyles.subSection
                        +AppStyles.center
                    }
                    timePicker(
                        date = state.date,
                        startTime = state.startTime,
                        endTime = state.endTime,
                        onTimeChanged = onTimeChanged
                    )
                }
            }
            val message = props.bookings.success()?.let { bookingValidation(newBooking, it) }

            styledButton {
                +"Book"
                css {
                    +InputStyles.buttonAccent
                    fontSize = 1.25.rem
                    margin(left = 20.px)
                }
                attrs { disabled = !(props.bookings.isSuccess() && message == null) }
            }

            styledP {
                css {
                    +AppStyles.error
                    visibility = if (message == null) Visibility.hidden else Visibility.visible
                }
                +(message ?: "No error")
            }
        }
        props.bookings.loading()?.let { spinner() }
        props.bookings.success()?.let { bookings ->
            bookingTable(
                showDate = false,
                showPhone = true,
                showDeleted = props.showDeleted,
                bookings = bookings.filter {
                    it.startTime.toLocalDateTime(zone).date == state.date
                },
                user = props.user,
                onDeleteBooking = props.onDeleteBooking
            )
        }
    }
}

fun RBuilder.bookingCreate(
    user: User,
    bookings: ResponseState<List<BookingResponse>>,
    showDeleted: Boolean,
    onCreateBooking: (CreateBookingRequest) -> Unit,
    onDeleteBooking: (bookingId: String) -> Unit
) = child(BookingCreate::class) {
    attrs {
        this.user = user
        this.bookings = bookings
        this.showDeleted = showDeleted
        this.onCreateBooking = onCreateBooking
        this.onDeleteBooking = onDeleteBooking
    }
}
