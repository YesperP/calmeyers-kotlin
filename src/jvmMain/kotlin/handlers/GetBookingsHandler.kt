package handlers

import com.amazonaws.services.lambda.runtime.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.GetBookingsResponse
import model.User
import model.toResponse
import tables.BookingTable
import tables.UserTable

private suspend fun getBookings(): GetBookingsResponse {
    val bookingTable = BookingTable(UserTable())
    val bookings = bookingTable.getBookings()
    return GetBookingsResponse(bookings.map { it.toResponse() })
}

class GetBookingsHandler : AwsApiHandlerAuth() {
    override suspend fun handleAuthRequest(input: ApiRequest, context: Context, user: User) =
        encodeBody(getBookings())
}