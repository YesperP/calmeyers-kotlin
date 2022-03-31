package model

import kotlinx.serialization.Serializable

@Serializable
data class GetBookingsResponse (
    val bookings: List<BookingResponse>
)