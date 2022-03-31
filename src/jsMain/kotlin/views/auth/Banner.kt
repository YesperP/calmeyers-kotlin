@file:Suppress("NON_EXPORTABLE_TYPE")

package views.auth

import eventHandler
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import model.User
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.attrs
import react.dom.span
import styled.css
import styled.styledButton
import styled.styledDiv
import styled.styledSpan
import styles.GlobalStyles
import styles.InputStyles

external interface BannerProps : RProps {
    var user: User?
    var onLogout: () -> Unit
}

@JsExport
class Banner : RComponent<BannerProps, LoginState>() {
    private val logout = eventHandler { props.onLogout() }

    override fun RBuilder.render() {
        styledDiv {
            css {
                padding(vertical = 10.px, horizontal = 20.px)
                color = GlobalStyles.white
                backgroundColor = GlobalStyles.mainColor
                display = Display.flex
                justifyContent = JustifyContent.spaceBetween
                alignItems = Align.center
            }
            span { +"Calmeyers Booking" }
            props.user?.let {
                styledDiv {
                    styledSpan {
                        css { margin(right = 10.px) }
                        +"${it.firstName} ${it.lastName}"
                    }
                    if (it.admin) {
                        styledButton {
                            +"Admin"
                            css {
                                +InputStyles.buttonInvert
                                margin(right = 10.px)
                            }
                            attrs { disabled = true }
                        }
                    }
                    styledButton {
                        +"Logout"
                        css { +InputStyles.buttonInvert }
                        attrs { onClickFunction = logout }
                    }
                }
            }
        }
    }
}

fun RBuilder.banner(user: User?, onLogout: () -> Unit) =
    child(Banner::class) {
        attrs {
            this.user = user
            this.onLogout = onLogout
        }
    }