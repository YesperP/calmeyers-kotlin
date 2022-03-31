@file:Suppress("NON_EXPORTABLE_TYPE")

package views

import LoginMethod
import Settings
import TokenStore
import api.Api
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import model.User
import react.*
import react.dom.attrs
import styled.css
import styled.styledButton
import styled.styledDiv
import styles.AppStyles
import styles.InputStyles
import views.auth.banner
import views.auth.loginAlt
import views.auth.loginVibbo
import views.booking.bookingView

external interface AppState : RState {
    var api: Api?
    var user: User?
    var loginMethod: LoginMethod?
}

@JsExport
class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        val token = TokenStore.getToken()
        api = token?.let { Api(it) }
        user = token?.let { TokenStore.toUser(it) }
        loginMethod = Settings.loginMethods.takeIf { it.size == 1 }?.get(0)
    }

    private val onLogout = {
        TokenStore.clearToken()
        setState {
            api = null
            user = null
            loginMethod = null
        }
    }

    private val loginComplete = { token: String ->
        setState {
            api = Api(token)
            user = TokenStore.toUser(token)
            TokenStore.setToken(token)
        }
    }

    private val onForbidden = onLogout

    private fun RBuilder.renderLogin() = styledDiv {
        css { +AppStyles.centerHorizontally }
        when (state.loginMethod) {
            LoginMethod.VIBBO -> loginVibbo(loginComplete)
            LoginMethod.ALT -> loginAlt(loginComplete)
            null -> Settings.loginMethods.forEachIndexed { index, loginMethod ->
                styledButton {
                    +loginMethod.buttonName
                    css {
                        display = Display.block
                        margin(vertical = 15.px, horizontal = LinearDimension.auto)
                        if (index == 0) {
                            fontSize = 1.75.rem
                        } else {
                            +InputStyles.buttonMinimal
                        }
                    }
                    attrs {
                        onClickFunction = { setState { this.loginMethod = loginMethod } }
                    }
                }
            }
        }
    }

    override fun RBuilder.render() {
        banner(state.user, onLogout)
        styledDiv {
            css { +AppStyles.topContainer }
            state.api?.let { api ->
                state.user?.let { user ->
                    bookingView(api, user, onForbidden)
                }
            } ?: renderLogin()
        }
    }
}