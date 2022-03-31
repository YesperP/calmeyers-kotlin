package styles

import kotlinx.css.*
import kotlinx.css.properties.LineHeight

object GlobalStyles {
    val white = Color.white //Color("#f5f5f5")
    val offWhite = Color("#fbfbfb")
    val black = Color("#212529")

    val mainColor = Color("#399bec")
    val mainColorLight = Color.lightSteelBlue
    val mainColorSemiDark = Color("#23679E")
    val mainColorDark = Color("#072C4A") //Color("#0D3B66")
    val accentColor = Color("#FF7477")
    val tableHeaderColor = Color("#3F5870")

    val sheet = CSSBuilder().apply {
        html {
            fontSize = 16.px
            fontWeight = FontWeight.w400
            lineHeight = LineHeight("1.5")
            color = black
            textAlign = TextAlign.left
            backgroundColor = rgb(243, 247, 254)
            margin(all = null)
        }
        body {
            margin(all = 0.px)
        }

        multiTag(h1, h2, h3, h4) {
            color = mainColorDark
            margin(top = 0.px, right = 0.px, left = 0.px, bottom = 1.rem)
        }

        a {
            color = mainColor
            visited {
                color = mainColor
            }
        }

        multiTag(button, input, block = InputStyles.input)
        button(InputStyles.button)

        // TABLE
        table(TableStyles.table)
        thead(TableStyles.thead)
        tbody(TableStyles.tbody)

        ul {
            textAlign = TextAlign.left
        }
    }
}
