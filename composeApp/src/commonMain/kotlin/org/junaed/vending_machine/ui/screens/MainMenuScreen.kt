package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.junaed.vending_machine.ui.screens.simulator.screens.OverallControlScreen
import org.junaed.vending_machine.ui.theme.VendingMachineColors

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
                    title = {
                        Text(
                            "VIMTO Vending Machine",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VendingMachineColors.MachinePanelColor
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Get the navigator instance to handle navigation between screens
            val navigator = LocalNavigator.current

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                VendingMachineColors.MachineBackground,
                                VendingMachineColors.MachineBackground.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App logo or title
                    Text(
                        "VIMTO",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        color = VendingMachineColors.AccentColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Soft Drinks Dispenser",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Vending Machine screen
                    MainMenuButton(
                        text = "Vending Machine",
                        onClick = { navigator?.push(VendingMachineScreen()) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Maintenance screen
                    MainMenuButton(
                        text = "Maintenance",
                        onClick = { navigator?.push(MaintenanceScreen()) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Simulator screen
                    MainMenuButton(
                        text = "Simulator",
                        onClick = { navigator?.push(OverallControlScreen()) }
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Copyright information
                    Text(
                        "© 2025 VIMTO Soft Drinks Ltd",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun MainMenuButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor
            )
        ) {
            Text(
                text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
