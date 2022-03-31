package validations

import Settings
import kotlinx.datetime.Clock
import model.BookingResponse
import model.User

object DeleteBookingValidation {
    fun validate(booking: BookingResponse, user: User) {
        if (booking.startTime < Clock.System.now())
            throw Exception("Booking in the past")
        if (booking.deleter != null)
            throw Exception("Booking already deleted.")

        val canDelete = (Settings.adminCanDeleteAllBookings && user.admin) || (user.userId == booking.creator.user.userId)
        if (!canDelete)
            throw Exception("Not allowed to delete this views.booking.")
    }

    fun isValid(booking: BookingResponse, user: User) = try {
        validate(booking, user).let { true }
    } catch (e: Exception) {
        false
    }
}