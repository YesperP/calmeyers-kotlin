package model

import kotlinx.datetime.*

data class Time(
    val hour: Int = 0,
    val minute: Int = 0,
    val seconds: Int = 0,
    val nanoSeconds: Int = 0,
    val zone: TimeZone
) {
    operator fun compareTo(instant: Instant) =
        instant.toLocalDateTime(zone).date.atTime(hour, minute).toInstant(zone).compareTo(instant)
}

public operator fun Instant.compareTo(time: Time) =
    -time.compareTo(this)

fun Instant.Companion.from(dateTime: LocalDate, time: Time) = LocalDateTime(
    dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth,
    time.hour, time.minute, time.seconds, time.nanoSeconds,
).toInstant(time.zone)