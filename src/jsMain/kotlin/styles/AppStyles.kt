package styles

import kotlinx.css.*
import kotlinx.css.properties.border
import styled.StyleSheet


object AppStyles : StyleSheet("GlobalStyles") {
    val topContainer by css {
        width = 100.pct
        margin(left = LinearDimension.auto, right = LinearDimension.auto)
        media(query = "(min-width: 576px)") {
            width = 90.pct
        }
    }

    val smallText by css {
        fontSize = 0.9.rem
    }
    val tinyText by css {
        fontSize = 0.8.rem
    }

    val centerHorizontally by css {
        textAlign = TextAlign.center
        padding(all = 15.px)
        margin(vertical = 15.px)
        child("h2") {
            margin(top = 0.px)
        }
    }

    val center by css {
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }

    val section by css {
        +centerHorizontally
        borderRadius = 15.px
        backgroundColor = Color.white

        child("h2") {
            margin(top = 0.px)
        }
    }

    val subSection by css {
        backgroundColor = Color("#fbfbfb")
        border(1.px, BorderStyle.solid, GlobalStyles.mainColorLight, 26.px)
        padding(10.px)
    }

    val picker by css {
        display = Display.inlineFlex
        alignItems = Align.center
        flexDirection = FlexDirection.column
        flexWrap = FlexWrap.wrap
        margin(horizontal = 2.px)
    }

    val error by css {
        color = Color.darkRed
    }
}