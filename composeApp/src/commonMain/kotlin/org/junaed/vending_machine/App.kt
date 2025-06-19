package org.junaed.vending_machine

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.junaed.vending_machine.ui.screens.MainMenuScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(MainMenuScreen())
    }
}