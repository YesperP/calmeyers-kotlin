package styles

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet
import styled.animation


object SpinnerStyle : StyleSheet("SpinnerStyle") {
    val spinner by css {
        pointerEvents = PointerEvents.none
        width = 2.5.em
        height = 2.5.em
        margin(all = 2.5.em)
        border(0.4.em, BorderStyle.solid, Color("#eee"), borderRadius = 50.pct)
        borderTopColor = GlobalStyles.mainColor
        margin(horizontal = LinearDimension.auto)
        animation(
            duration = 1.s,
            timing = Timing.linear,
            iterationCount = IterationCount.infinite
        ) {
            100 {
                transform { rotate(360.deg) }
            }
        }
    }
}