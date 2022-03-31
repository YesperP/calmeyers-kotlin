package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val startTime: Instant,
    val endTime: Instant
)
