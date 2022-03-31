package styles

import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import styled.StyleSheet
import styles.GlobalStyles.accentColor
import styles.GlobalStyles.mainColor
import styles.GlobalStyles.mainColorDark
import styles.GlobalStyles.mainColorLight
import styles.GlobalStyles.white

object InputStyles : StyleSheet("InputStyles") {
    private val disabledAlpha = 0.3

    val input by css {
        border = "none"
        outline = Outline.none
        display = Display.inlineBlock
        boxSizing = BoxSizing.borderBox
        fontSize = 1.rem
        lineHeight = LineHeight("1.5")

        borderRadius = 26.px
        backgroundColor = white
        padding(vertical = 2.px, horizontal = 16.px)
    }

    val inputNothing by css {
        borderRadius = 0.px
        backgroundColor = Color.transparent
        padding(all = 0.px)
    }

    val button by css {
        color = white
        backgroundColor = mainColor
        transition(duration = 0.1.s)

        hover { backgroundColor = mainColorDark }
        disabled { backgroundColor = mainColor.changeAlpha(disabledAlpha) }
    }

    val buttonAccent by css {
        backgroundColor = accentColor
        hover { backgroundColor = accentColor.darken(25) }
        disabled { backgroundColor = accentColor.changeAlpha(disabledAlpha) }
    }


    val buttonMinimal by css {
        color = mainColor
        backgroundColor = Color.transparent
        fontWeight = FontWeight.w500
        borderRadius = 0.px
        borderBottom(1.px, BorderStyle.solid, mainColorLight)
        padding(horizontal = 8.px)
        hover {
            backgroundColor = Color.transparent
            borderBottomColor = mainColorDark
        }
    }

    val buttonInvert by css {
        color = mainColorDark
        backgroundColor = Color("#fff")
        hover { backgroundColor = mainColorLight }
        disabled { backgroundColor = Color("#fff").changeAlpha(disabledAlpha) }
    }
}