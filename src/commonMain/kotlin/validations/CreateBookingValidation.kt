package validations

import Settings
import Settings.maxBookingsAhead
import Settings.maxDuration
import Settings.maxTime
import Settings.minDuration
import Settings.minTime
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import model.BookingResponse
import model.CreateBookingRequest
import model.User
import utils.toStringTime

object CreateBookingValidation {
    private fun validateTime(booking: CreateBookingRequest) {
        if (booking.startTime < Clock.System.now())
            throw Exception("Booking in the past")
        if (booking.startTime > Clock.System.now() + Settings.maxDurationAhead)
            throw Exception("Booking too far ahead")
        if (booking.startTime > booking.endTime)
            throw Exception("Start time after end time")
        if (minTime > booking.startTime)
            throw Exception("Start time too early (Min time: ${minTime}).")
        if (maxTime < booking.endTime)
            throw Exception("End time too late (Max time: ${maxTime}).")
        if (booking.endTime - booking.startTime < minDuration)
            throw Exception("Booking too short (min: $minDuration)")
        if (booking.endTime - booking.startTime > maxDuration)
            throw Exception("Booking too long (max: $maxDuration)")
    }

    private fun validateDateAllowed(booking: CreateBookingRequest) {
        val bookingDate = booking.startTime.toLocalDateTime(Settings.timeZone).date
        Settings.invalidDates.forEach {
            if (bookingDate.dayOfMonth == it.dayOfMonth && bookingDate.monthNumber == it.monthNumber)
                throw Exception("Booking on invalid date")
        }
    }

    private fun validateAvailable(booking: CreateBookingRequest, otherBookings: Iterable<BookingResponse>) {
        for (other in otherBookings) {
            if (other.deleter != null) continue
            if (booking.endTime <= other.startTime) continue
            if (booking.startTime >= other.endTime) continue
            throw Exception(
                "Overlapping with booking: ${other.startTime.toStringTime()} - ${other.endTime.toStringTime()}"
            )
        }
    }

    private fun validateNewBookingForApartment(creator: User, bookings: Iterable<BookingResponse>) {
        val now = Clock.System.now()
        val bookingsInFutureForApartment = bookings.filter {
            it.creator.user.apartmentNumber == creator.apartmentNumber &&
                    it.deleter == null &&
                    it.startTime > now
        }
        if (bookingsInFutureForApartment.size >= maxBookingsAhead)
            throw Exception("Maximum bookings reached (${bookingsInFutureForApartment.size})")

    }

    fun validateAll(
        creator: User,
        newBooking: CreateBookingRequest,
        otherBookings: Iterable<BookingResponse>
    ) {
        validateDateAllowed(newBooking)
        validateNewBookingForApartment(creator, otherBookings)
        validateTime(newBooking)
        validateAvailable(newBooking, otherBookings)
    }
}
