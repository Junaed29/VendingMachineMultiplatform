package org.junaed.vending_machine.ui.utils

/**
 * Opens a URL in a new browser tab/window.
 * This is a multiplatform function that has platform-specific implementations.
 * On non-web platforms, this will be a no-op.
 */
expect fun openUrlInBrowser(url: String)
