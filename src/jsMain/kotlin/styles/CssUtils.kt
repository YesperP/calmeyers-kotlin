package styles

import kotlinx.css.CSSBuilder
import kotlinx.css.TagSelector

fun CSSBuilder.multiTag(vararg tagSelectors: TagSelector, block: CSSBuilder.() -> Unit) {
    TagSelector(tagSelectors.joinToString(separator = ", ") { it.tagName })(block)
}
