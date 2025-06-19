package org.junaed.vending_machine

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VendingMachineMultiplatform",
    ) {
        App()
    }
}