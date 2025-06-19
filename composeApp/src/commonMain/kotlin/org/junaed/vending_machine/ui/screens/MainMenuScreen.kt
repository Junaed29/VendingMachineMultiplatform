package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

/**
 * Main Menu Screen for the Vending Machine app
 * This is the entry point of the application that provides navigation to other screens
 */
class MainMenuScreen: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Vending Machine App", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors()
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Get the navigator instance to handle navigation between screens
            val navigator = LocalNavigator.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Button to navigate to the Vending Machine screen
                Button(onClick = {
                    // TODO: Add any preparation logic before navigation if needed
                    navigator?.push(VendingMachineScreen())
                }) {
                    Text("Vending Machine")
                }

                // Button to navigate to the Maintenance screen
                Button(onClick = {
                    // TODO: Implement navigation to Maintenance screen
                    navigator?.push(MaintenanceScreen())
                }) {
                    Text("Maintenance")
                }

                // Button to navigate to the Simulator screen
                Button(onClick = {
                    // TODO: Implement navigation to Simulator screen
                    navigator?.push(SimulatorScreen())
                }) {
                    Text("Simulator")
                }

                // TODO: Add any additional UI elements or information as needed
                // For example: app version, copyright information, etc.
            }
        }
    }
}
