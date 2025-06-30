package org.junaed.vending_machine.ui.utils

import kotlinx.browser.window

/**
 * Opens a URL in a new browser tab/window.
 * This implementation is specific to the Web platform.
 */
actual fun openUrlInBrowser(url: String) {
    // Use the kotlinx.browser API which works with Kotlin/Wasm
    window.open(url, "_blank")
}
