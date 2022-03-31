@file:Suppress("NON_EXPORTABLE_TYPE")

package views.booking

import kotlinx.css.LinearDimension
import kotlinx.css.width
import kotlinx.html.TH
import kotlinx.html.TR
import kotlinx.html.js.onClickFunction
import model.BookingResponse
import model.User
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledTable
import utils.toStringDate
import utils.toStringDateTime
import utils.toStringTime
import validations.DeleteBookingValidation

external interface BookingTableProps : RProps {
    var showDeleted: Boolean
    var showDate: Boolean
    var showPhone: Boolean
    var bookings: List<BookingResponse>
    var user: User
    var onDeleteBooking: (bookingId: String) -> Unit
}


@JsExport
class BookingTable : RComponent<BookingTableProps, RState>() {
    private fun RDOMBuilder<TR>.th(colSpan: Int, block: RDOMBuilder<TH>.() -> Unit) {
        th {
            block()
            attrs { this.colSpan = colSpan.toString() }
        }
    }

    override fun RBuilder.render() {
        styledTable {
            css {
                width = LinearDimension("100%")
            }
            thead {
                tr {
                    th(if (props.showDate) 3 else 2) { +"Time" }
                    th(if (props.showPhone) 3 else 2) { +"Created" }
                    if (props.showDeleted) th(2) { +"Deleted" }
                    th(1) { }
                }
                tr {
                    if (props.showDate) th { +"Date" }
                    th { +"Start" }
                    th { +"End" }
                    th { +"Name" }
                    if (props.showPhone) th { +"Phone" }
                    th { +"Time" }
                    if (props.showDeleted) {
                        th { +"Name" }
                        th { +"Time" }
                    }
                    th { }
                }
            }
            tbody {
                val bookings = props.bookings.filter {
                    it.deleter == null || props.showDeleted
                }

                if (bookings.isEmpty()) {
                    tr {
                        td {
                            attrs { colSpan = "8" }
                            +"No bookings"
                        }
                    }
                }
                bookings.sortedBy {
                    it.startTime
                }.forEach {
                    tr {
                        if (props.showDate) {
                            td { +it.startTime.toStringDate() }
                        }
                        td { +it.startTime.toStringTime() }
                        td { +it.endTime.toStringTime() }
                        td { +"${it.creator.user.firstName} ${it.creator.user.lastName}" }
                        if (props.showPhone) td { +it.creator.user.phoneNumber }
                        td { +it.creator.time.toStringDateTime() }
                        if (props.showDeleted) {
                            td { +(it.deleter?.let { "${it.user.firstName} ${it.user.lastName}" } ?: "") }
                            td { +(it.deleter?.time?.toStringDateTime() ?: "") }
                        }

                        if (DeleteBookingValidation.isValid(it, props.user)) {
                            td {
                                button {
                                    +"Delete"
                                    attrs {
                                        onClickFunction = { _ ->
                                            props.onDeleteBooking(it.bookingId)
                                        }
                                    }
                                }
                            }
                        } else {
                            td { }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.bookingTable(
    showDeleted: Boolean,
    showDate: Boolean,
    showPhone: Boolean,
    bookings: List<BookingResponse>,
    user: User,
    onDeleteBooking: (bookingId: String) -> Unit
) = child(BookingTable::class) {
    attrs {
        this.showDeleted = showDeleted
        this.showDate = showDate
        this.showPhone = showPhone
        this.bookings = bookings
        this.user = user
        this.onDeleteBooking = onDeleteBooking
    }
}