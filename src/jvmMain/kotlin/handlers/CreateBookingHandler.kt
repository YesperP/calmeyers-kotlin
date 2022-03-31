package handlers

import com.amazonaws.services.lambda.runtime.Context
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import model.*
import tables.BookingTable
import tables.UserTable
import validations.CreateBookingValidation
import java.util.*

private suspend fun createBooking(createBookingRequest: CreateBookingRequest, user: User): BookingResponse {
    val bookingTable = BookingTable(UserTable())
    val otherBookings = bookingTable.getBookings()

    CreateBookingValidation.validateAll(
        user,
        createBookingRequest,
        otherBookings.map { it.toResponse() }
    )
    val booking = Booking(
        UUID.randomUUID().toString(),
        createBookingRequest.startTime,
        createBookingRequest.endTime,
        creator = BookUser(user, Clock.System.now()),
        deleter = null
    )
    bookingTable.setBooking(booking)
    return booking.toResponse()
}

class CreateBookingHandler : AwsApiHandlerAuth() {
    override suspend fun handleAuthRequest(input: ApiRequest, context: Context, user: User) =
        encodeBody(createBooking(decodeBody(input), user))
}