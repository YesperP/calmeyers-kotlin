package handlers

import com.amazonaws.services.lambda.runtime.Context
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.*
import tables.BookingTable
import tables.UserTable
import validations.DeleteBookingValidation

private suspend fun deleteBooking(bookingId: String, user: User): BookingResponse {
    val bookingTable = BookingTable(UserTable())
    val booking = bookingTable.getBooking(bookingId)
        ?: throw Exception("Booking not existing")

    DeleteBookingValidation.validate(booking.toResponse(), user)

    val deletedBooking = booking.copy(
        deleter = BookUser(user, Clock.System.now())
    )
    bookingTable.setBooking(deletedBooking)
    return deletedBooking.toResponse()
}

class DeleteBookingHandler : AwsApiHandlerAuth() {
    override suspend fun handleAuthRequest(input: ApiRequest, context: Context, user: User): String {
        val bookingId = input.pathParameters?.get("bookingId")
            ?: throw BadRequestException("No bookingId specified")
        return encodeBody(deleteBooking(bookingId, user))
    }
}