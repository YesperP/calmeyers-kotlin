@file:Suppress("NON_EXPORTABLE_TYPE")

package views.booking

import kotlinx.css.*
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import styled.*
import styles.AppStyles
import styles.ArrowStyles

external interface DatePickerProps : RProps {
    var onDateChanged: (LocalDate) -> Unit
    var date: LocalDate
}

object DatePickerStyleSheet : StyleSheet("DatePickerStyles") {
    val pickerContainer by css {
        +AppStyles.center
        margin(vertical = 10.px)
    }

    val timeValue by css {
        textAlign = TextAlign.center
        display = Display.inlineBlock
        width = 175.px
        fontSize = 1.2.rem
    }
}

@JsExport
class DatePicker(props: DatePickerProps) : RComponent<DatePickerProps, RState>(props) {

    private fun RBuilder.dateValue(value: String) = styledSpan {
        css { +DatePickerStyleSheet.timeValue }
        +value
    }

    private fun RBuilder.arrow(value: Int, unit: DateTimeUnit.DateBased) {
        styledA {
            css { +ArrowStyles.arrow }
            if (value < 0) +"ᐊ" else +"ᐅ"
            attrs {
                onClickFunction = {
                    val newDate = props.date.plus(value, unit)
                    props.onDateChanged(newDate)
                }
            }
        }
    }

    override fun RBuilder.render() {
        println("DatePicker render")

        styledDiv {
            css { +DatePickerStyleSheet.pickerContainer }
            dateValue(
                props.date.year.toString()
            )
        }
        styledDiv {
            css { +DatePickerStyleSheet.pickerContainer }
            arrow(-1, DateTimeUnit.MONTH)
            dateValue(
                props.date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
            )
            arrow(1, DateTimeUnit.MONTH)
        }
        styledDiv {
            css { +DatePickerStyleSheet.pickerContainer }
            arrow(-1, DateTimeUnit.DAY)
            dateValue("${
                props.date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }
            }, ${props.date.dayOfMonth}")
            arrow(1, DateTimeUnit.DAY)
        }
    }
}

fun RBuilder.datePicker(
    date: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) = child(DatePicker::class) {
    attrs {
        this.date = date
        this.onDateChanged = onDateChanged
    }
}