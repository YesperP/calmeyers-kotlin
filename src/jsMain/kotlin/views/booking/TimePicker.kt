@file:Suppress("NON_EXPORTABLE_TYPE")

package views.booking

import Settings
import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.html.js.onClickFunction
import model.from
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import react.dom.span
import styled.css
import styled.styledA
import styled.styledDiv
import styled.styledSpan
import styles.AppStyles
import styles.ArrowStyles
import utils.hour
import utils.minute
import utils.padTime
import kotlin.time.Duration

external interface TimePickerProps : RProps {
    var startTime: Instant
    var endTime: Instant
    var minTime: Instant
    var maxTime: Instant
    var minDurationMillis: Long
    var maxDurationMillis: Long
    var minuteStep: Int
    var onTimeChanged: (startTime: Instant, endTime: Instant) -> Unit
    var timeZone: TimeZone
}

fun TimePickerProps.minDuration() = Duration.milliseconds(minDurationMillis)
fun TimePickerProps.maxDuration() = Duration.milliseconds(maxDurationMillis)

private fun capTime(
    startChanged: Boolean,
    startTimeInput: Instant,
    endTimeInput: Instant,
    minTime: Instant,
    maxTime: Instant,
    minDuration: Duration,
    maxDuration: Duration
): Pair<Instant, Instant> {
    var startTime = startTimeInput
    var endTime = endTimeInput
    if (startChanged) {
        startTime = minOf(startTime, maxTime - minDuration)
        startTime = maxOf(startTime, minTime)

        endTime = maxOf(endTime, startTime + minDuration)
        endTime = minOf(endTime, startTime + maxDuration)
        endTime = minOf(endTime, maxTime)
    } else {
        endTime = maxOf(endTime, minTime + minDuration)
        endTime = minOf(endTime, maxTime)

        startTime = minOf(startTime, endTime - minDuration)
        startTime = maxOf(startTime, endTime - maxDuration)
        startTime = maxOf(startTime, minTime)
    }
    return startTime to endTime
}

@JsExport
class TimePicker(props: TimePickerProps) : RComponent<TimePickerProps, RState>(props) {

    private fun RBuilder.timeValue(value: Int) = styledSpan {
        css {
            fontSize = 1.5.em
            lineHeight = LineHeight("1.0")
        }
        +padTime(value)
    }

    private fun RBuilder.timeArrow(start: Boolean, duration: Duration) = styledA {
        css {
            +ArrowStyles.arrow
            +ArrowStyles.arrowVertical
        }
        if (duration > Duration.ZERO) +"▲" else +"▼"
        attrs {
            onClickFunction = {
                var startTime = props.startTime
                var endTime = props.endTime

                if (start) startTime += duration else endTime += duration
                val capped = capTime(
                    start,
                    startTime,
                    endTime,
                    props.minTime,
                    props.maxTime,
                    props.minDuration(),
                    props.maxDuration()
                )
                startTime = capped.first
                endTime = capped.second
                props.onTimeChanged(startTime, endTime)
            }
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css { +AppStyles.picker }
            timeArrow(true, Duration.hours(1))
            timeValue(props.startTime.hour(props.timeZone))
            timeArrow(true, Duration.hours(-1))
        }
        span { +":" }
        styledDiv {
            css { +AppStyles.picker }
            timeArrow(true, Duration.minutes(props.minuteStep))
            timeValue(props.startTime.minute(props.timeZone))
            timeArrow(true, Duration.minutes(-props.minuteStep))
        }
        styledSpan {
            css { margin(horizontal = 7.px) }
            +"to"
        }
        styledDiv {
            css { +AppStyles.picker }
            timeArrow(false, Duration.hours(1))
            timeValue(props.endTime.hour(props.timeZone))
            timeArrow(false, Duration.hours(-1))
        }
        span { +":" }
        styledDiv {
            css { +AppStyles.picker }
            timeArrow(false, Duration.minutes(props.minuteStep))
            timeValue(props.endTime.minute(props.timeZone))
            timeArrow(false, Duration.minutes(-props.minuteStep))
        }
    }
}

fun RBuilder.timePicker(
    date: LocalDate,
    startTime: Instant,
    endTime: Instant,
    onTimeChanged: (startTime: Instant, endTime: Instant) -> Unit
) = child(TimePicker::class) {
    attrs {
        this.startTime = startTime
        this.endTime = endTime
        this.minTime = Instant.from(date, Settings.minTime)
        this.maxTime = Instant.from(date, Settings.maxTime)
        this.minDurationMillis = Settings.minDuration.inWholeMilliseconds
        this.maxDurationMillis = Settings.maxDuration.inWholeMilliseconds
        this.minuteStep = Settings.minuteStep
        this.onTimeChanged = onTimeChanged
        this.timeZone = Settings.timeZone
    }
}
