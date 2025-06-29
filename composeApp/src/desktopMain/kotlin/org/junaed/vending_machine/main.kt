package org.junaed.vending_machine

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.res.painterResource
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "VendingMachineMultiplatform",
        icon = painterResource("DrinkBot.png")
    ) {
        App()
    }
}