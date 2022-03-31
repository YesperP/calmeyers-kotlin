package model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val bookingId: String,
    val startTime: Instant,
    val endTime: Instant,
    val creator: BookUser,
    val deleter: BookUser?
)

@Serializable
data class BookUser(
    val user: User,
    val time: Instant
)

@Serializable
data class BookingResponse(
    val bookingId: String,
    val startTime: Instant,
    val endTime: Instant,
    val creator: BookUserResponse,
    val deleter: BookUserResponse?
)

@Serializable
data class BookUserResponse(
    val user: UserResponse,
    val time: Instant
)

@Serializable
data class UserResponse(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val apartmentNumber: Int
)

fun Booking.toResponse() = BookingResponse(
    bookingId = bookingId,
    startTime = startTime,
    endTime = endTime,
    creator = creator.toResponse(),
    deleter = deleter?.toResponse()
)

fun BookUser.toResponse() = BookUserResponse(
    user = user.toResponse(),
    time = time
)

fun User.toResponse() = UserResponse(
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    apartmentNumber = apartmentNumber
)
