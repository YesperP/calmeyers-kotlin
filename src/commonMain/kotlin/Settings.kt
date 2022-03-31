import kotlinx.datetime.TimeZone
import model.Time
import kotlin.time.Duration

data class LocalDateYearInvariant(val monthNumber: Int, val dayOfMonth: Int)

enum class LoginMethod(val buttonName: String) {
    VIBBO("Login with Vibbo"),
    ALT("Login manually")
}

object Settings {
    val timeZone = TimeZone.of("Europe/Oslo")
    val minTime = Time(11, 0, zone = timeZone)
    val maxTime = Time(23, 0, zone = timeZone)
    val minDuration = Duration.minutes(30)
    val maxDuration: Duration = Duration.hours(24)  // Effectively disabled
    val maxDurationAhead = Duration.days(31 * 2)
    const val maxBookingsAhead = 2
    const val minuteStep = 15
    const val adminCanDeleteAllBookings = false
    val loginMethods = listOf(LoginMethod.VIBBO, LoginMethod.ALT)  // First is highlighted
    val invalidDates = listOf(
        LocalDateYearInvariant(5, 17)
    )
}