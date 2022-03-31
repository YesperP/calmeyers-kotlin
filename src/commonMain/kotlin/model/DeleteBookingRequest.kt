package model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteBookingRequest(
    val bookingId: String
)