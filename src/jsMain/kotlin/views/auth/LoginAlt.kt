@file:Suppress("NON_EXPORTABLE_TYPE")

package views.auth

import api.Loader
import api.TokenApi
import formSubmitHandler
import inputHandler
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.width
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import model.LoginAltRequest
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.attrs
import react.dom.h1
import react.dom.span
import styled.*
import styles.AppStyles
import styles.SpinnerStyle
import validations.LoginAltValidation

external interface LoginAltProps : RProps {
    var onLoginComplete: (token: String) -> Unit
}

external interface LoginAltState : RState {
    var phoneNumber: String
    var firstName: String
    var lastName: String
    var apartmentNumber: String
    var loading: Boolean
    var error: String?
}

@JsExport
class LoginAlt : RComponent<LoginAltProps, LoginAltState>() {
    override fun LoginAltState.init() {
        phoneNumber = ""
        firstName = ""
        lastName = ""
        apartmentNumber = ""
        loading = false
        error = null
    }

    private val loader = Loader().onLoading {
        setState {
            loading = it
            error = null
        }
    }.handleError {
        setState {
            error = it.message ?: "Unknown Error"
        }
    }

    private fun loginInputHandler(buildState: LoginAltState.(HTMLInputElement) -> Unit) = inputHandler {
        setState {
            buildState(it)
            error = null
        }
    }

    private val onSubmit = formSubmitHandler {
        loader.load {
            val request = LoginAltRequest(
                state.phoneNumber.trim(),
                state.firstName.trim(),
                state.lastName.trim(),
                state.apartmentNumber.trim()
            )
            LoginAltValidation.validate(request)
            val res = TokenApi.loginAlt(request)
            props.onLoginComplete(res.token)
        }
    }
    private val onChangePhone = loginInputHandler { phoneNumber = it.value.substring(0, 8) }
    private val onChangeFirstName = loginInputHandler { firstName = it.value }
    private val onChangeLastName = loginInputHandler { lastName = it.value }
    private val onChangeAptNumber = loginInputHandler { apartmentNumber = it.value.substring(0, 3) }

    private fun RBuilder.inputField(
        label: String,
        inputType: InputType,
        value: String,
        onChange: (evt: Event) -> Unit,
        placeholder: String? = null,
        autoFocus: Boolean = false
    ) {
        styledDiv {
            css {
                margin(10.px)
            }
            span { +label }
            styledInput {
                css { width = 15.rem }
                attrs {
                    this.autoFocus = autoFocus
                    type = inputType
                    this.value = value
                    this.placeholder = placeholder ?: ""
                    onChangeFunction = onChange
                }
            }
        }
    }


    override fun RBuilder.render() {
        h1 { +"Login" }
        when {
            state.loading -> {
                styledDiv { css { +SpinnerStyle.spinner } }
            }
            else -> {
                styledForm {
                    attrs { onSubmitFunction = onSubmit }
                    inputField(
                        "Phone (+47): ",
                        InputType.number,
                        state.phoneNumber,
                        onChangePhone,
                        autoFocus = true,
                        placeholder = "45454545"
                    )
                    inputField(
                        "First Name: ",
                        InputType.text,
                        state.firstName,
                        onChangeFirstName,
                        placeholder = "Ola"
                    )
                    inputField(
                        "Last Name: ",
                        InputType.text,
                        state.lastName,
                        onChangeLastName,
                        placeholder = "Nordmann"
                    )
                    inputField(
                        "Apartment: ",
                        InputType.number,
                        state.apartmentNumber,
                        onChangeAptNumber,
                        placeholder = "606"
                    )
                    styledButton {
                        +"Login"
                        attrs {
                            disabled = state.phoneNumber.length != 8 ||
                                    state.firstName.isBlank() ||
                                    state.lastName.isBlank() ||
                                    state.apartmentNumber.length != 3
                        }
                    }
                }
            }

        }
        state.error?.let {
            styledP {
                css { +AppStyles.error }
                +it
            }
        }
    }
}

fun RBuilder.loginAlt(onLoginComplete: (token: String) -> Unit) =
    child(LoginAlt::class) {
        attrs {
            this.onLoginComplete = onLoginComplete
        }
    }