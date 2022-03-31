@file:Suppress("NON_EXPORTABLE_TYPE")

package views.auth

import api.Loader
import api.TokenApi
import eventHandler
import formSubmitHandler
import inputHandler
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.width
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import model.LoginEndRequest
import model.LoginStartRequest
import model.LoginStartResponse
import react.*
import react.dom.a
import react.dom.attrs
import react.dom.h1
import react.dom.span
import styled.*
import styles.AppStyles
import styles.InputStyles
import styles.SpinnerStyle

external interface LoginProps : RProps {
    var onLoginComplete: (token: String) -> Unit
}

external interface LoginState : RState {
    var startResult: LoginStartResponse?
    var phoneNumber: String
    var code: String
    var loading: Boolean
    var error: String?
}

@JsExport
class LoginVibbo : RComponent<LoginProps, LoginState>() {
    override fun LoginState.init() {
        startResult = null
        phoneNumber = ""
        code = ""
        loading = false
        error = null
    }

    private val loader = Loader()
        .onLoading {
            setState {
                loading = it
                error = null
            }
        }
        .handleError {
            setState {
                error = it.message ?: "Unknown Error"
            }
        }

    private val onSubmitPhone = formSubmitHandler {
        loader.load {
            val res = TokenApi.loginVibboStart(LoginStartRequest("+47${state.phoneNumber}"))
            setState {
                startResult = res
            }
        }
    }

    private val onSubmitCode = formSubmitHandler {
        loader.load {
            val res = TokenApi.loginVibboEnd(LoginEndRequest(state.code, state.startResult!!))
            props.onLoginComplete(res.token)
        }
    }

    private val onChangePhone = inputHandler {
        setState { phoneNumber = it.value.substring(0, 8) }
    }
    private val onChangeCode = inputHandler {
        setState { code = it.value.substring(0, 4) }
    }

    private val onResend = eventHandler {
        setState {
            startResult = null
            phoneNumber = ""
            code = ""
            error = null
        }
    }

    override fun RBuilder.render() {
        h1 { +"Login with Vibbo" }
        state.error?.let {
            styledP {
                css { +AppStyles.error }
                +it
            }
        }
        when {
            state.loading -> {
                styledDiv { css { +SpinnerStyle.spinner } }
            }
            state.startResult == null -> {
                styledForm {
                    css { +AppStyles.center }
                    attrs { onSubmitFunction = onSubmitPhone }
                    styledDiv {
                        css {
                            +InputStyles.input
                            margin(right = 10.px)
                        }
                        span { +"+47 " }
                        styledInput {
                            css {
                                width = 8.rem
                                +InputStyles.inputNothing
                            }
                            attrs {
                                autoFocus = true
                                type = InputType.number
                                value = state.phoneNumber
                                onChangeFunction = onChangePhone
                            }
                        }
                    }
                    styledButton {
                        +"Next"
                        attrs { disabled = state.phoneNumber.length != 8 }
                    }
                }
                styledP {
                    css { +AppStyles.tinyText }
                    span { +"Note: You have to be registered at Calmeyers Gate 4 at "}
                    a("https://vibbo.no/calmeyersgt-4/om", "_blank") { +"vibbo.no" }
                    span { +" (OBOS)." }
                }
            }
            else -> {
                styledP {
                    css { +AppStyles.tinyText }
                    +"Obos has sent a code to +47 ${state.phoneNumber}"
                }
                styledForm {
                    css { +AppStyles.center }
                    attrs { onSubmitFunction = onSubmitCode }
                    span { +"Code: " }
                    styledInput {
                        css {
                            width = 8.rem
                            margin(horizontal = 10.px)
                        }
                        attrs {
                            autoFocus = true
                            type = InputType.number
                            value = state.code
                            onChangeFunction = onChangeCode
                        }
                    }
                    styledButton {
                        +"Confirm"
                        attrs { disabled = state.code.length != 4 }
                    }
                }
                styledDiv {
                    css {
                        +AppStyles.tinyText
                        margin(vertical = 10.px)
                    }
                    span { +"Not received code? " }
                    styledButton {
                        +"Resend"
                        css {
                            +AppStyles.tinyText
                            +InputStyles.buttonMinimal
                        }
                        attrs { onClickFunction = onResend }
                    }
                }
            }
        }
    }
}

fun RBuilder.loginVibbo(onLoginComplete: (token: String) -> Unit) =
    child(LoginVibbo::class) {
        attrs {
            this.onLoginComplete = onLoginComplete
        }
    }