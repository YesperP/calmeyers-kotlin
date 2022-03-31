package utils

import kotlinx.datetime.*

fun padTime(value: Int) = value.toString().padStart(2, '0')

fun LocalDateTime.toStringTime() = "${padTime(hour)}:${padTime(minute)}"
fun Instant.toStringTime() = toLocalDateTime(TimeZone.currentSystemDefault()).toStringTime()
fun LocalDateTime.toStringDate() = date.toString()
fun Instant.toStringDate() = toLocalDateTime(TimeZone.currentSystemDefault()).toStringDate()
fun LocalDateTime.toStringDateTime() = "${toStringDate()} ${toStringTime()}"
fun Instant.toStringDateTime() = toLocalDateTime(TimeZone.currentSystemDefault()).toStringDateTime()

fun Clock.System.nowDateTime(timeZone: TimeZone) = now().toLocalDateTime(timeZone)
fun Clock.System.nowDate(timeZone: TimeZone) = now().toLocalDateTime(timeZone).date

fun Instant.hour(zone: TimeZone) = toLocalDateTime(zone).hour
fun Instant.minute(zone: TimeZone) = toLocalDateTime(zone).minute

fun Instant.withDate(date: LocalDate, zone: TimeZone) =
    toLocalDateTime(zone).let {
        LocalDateTime(date.year, date.monthNumber, date.dayOfMonth,  it.hour, it.minute, it.second, it.nanosecond)
            .toInstant(zone)
    }