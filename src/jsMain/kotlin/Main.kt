import kotlinx.browser.document
import react.dom.render
import styles.GlobalStyles
import views.App

/* Enabling all timezones for JS */

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

fun main() {
    println("Kotlin version: ${KotlinVersion.CURRENT}")
    styled.injectGlobal(GlobalStyles.sheet.toString())
    render(document.getElementById("root")) {
        child(App::class) {}
    }
}
