package views

import react.RBuilder
import styled.css
import styled.styledDiv
import styles.SpinnerStyle

fun RBuilder.spinner() = styledDiv { css { +SpinnerStyle.spinner } }