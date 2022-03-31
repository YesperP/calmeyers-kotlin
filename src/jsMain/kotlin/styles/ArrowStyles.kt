package styles

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object ArrowStyles : StyleSheet("ArrowStyles") {
    val arrow by css {
        fontSize = 2.5.rem
        lineHeight = LineHeight("1.0")
        color = GlobalStyles.mainColorSemiDark
        cursor = Cursor.pointer
        userSelect = UserSelect.none
        transition(duration = 0.2.s)
        hover {
            color = GlobalStyles.mainColorDark
        }
    }
    val arrowVertical by css {
        fontSize = 3.rem
        transform {
            scale(1, 0.6)
        }
    }
}