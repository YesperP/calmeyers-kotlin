package styles

import kotlinx.css.*
import kotlinx.css.properties.LineHeight
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderLeft
import kotlinx.css.properties.borderRight
import styled.StyleSheet

object TableStyles : StyleSheet("TableStyles") {
    private val rounding = 15.px
    private val headSize = 0.9.rem
    private val bodySize = 0.85.rem

    val table by css {
        borderCollapse = BorderCollapse.separate
        borderSpacing = 0.px
    }
    val thead by css {
        fontSize = headSize
        verticalAlign = VerticalAlign.middle
        backgroundColor = GlobalStyles.tableHeaderColor
        color = GlobalStyles.offWhite
        lineHeight = LineHeight("2.0")
        tr {
            th {
                fontWeight = FontWeight.w900
                textAlign = TextAlign.center
            }
            firstChild {
                th {
                    firstChild { borderTopLeftRadius = rounding }
                    lastChild { borderTopRightRadius = rounding }
                }
            }
            lastChild { th { padding(top = 5.px) } }
        }
    }
    val tbody by css {
        fontSize = bodySize
        backgroundColor = GlobalStyles.offWhite
        lineHeight = LineHeight("1.8")
        tr {
            textAlign = TextAlign.center
            td {
                firstChild { borderLeft(1.px, BorderStyle.solid, GlobalStyles.mainColorLight) }
                lastChild { borderRight(1.px, BorderStyle.solid, GlobalStyles.mainColorLight) }

                button {
                    fontSize = 0.85.em
                    +InputStyles.buttonMinimal
                }
            }

            firstChild { td { padding(top = 5.px) } }
            lastChild {
                td {
                    padding(bottom = 5.px)
                    borderBottom(1.px, BorderStyle.solid, GlobalStyles.mainColorLight)
                    firstChild {
                        minWidth = rounding
                        borderBottomLeftRadius = rounding
                    }
                    lastChild {
                        minWidth = rounding
                        borderBottomRightRadius = rounding
                    }
                }
            }
        }
    }
}