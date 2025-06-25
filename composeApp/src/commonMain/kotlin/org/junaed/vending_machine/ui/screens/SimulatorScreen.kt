package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * Simulator Screen
 * This screen allows users to simulate various vending machine scenarios:
 * - Test different purchase flows
 * - Simulate errors and edge cases
 * - Demonstrate machine behavior
 */
class SimulatorScreen : Screen {
    /**
     * Helper function to format float values to 1 decimal place
     * Based on the implementation from VendingMachineService
     */
    private fun formatToOneDecimalPlace(value: Float): String {
        val roundedValue = kotlin.math.round(value * 10) / 10
        return buildString {
            append(roundedValue.toInt())
            append('.')
            val fraction = ((roundedValue - roundedValue.toInt()) * 10).toInt()
            append(fraction)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        // State variables for the simulator
        var temperatureValue by remember { mutableFloatStateOf(4.0f) }
        var coinJamEnabled by remember { mutableStateOf(false) }
        var dispenserJamEnabled by remember { mutableStateOf(false) }
        var simulationLog by remember { mutableStateOf("Simulation log will appear here...") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "VIMTO Simulator",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VendingMachineColors.MachinePanelColor
                    )
                )
            }
        ) { innerPadding ->
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
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        "SIMULATION CONTROLS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // SECTION: Environment Controls
                    SimulatorCard(title = "Environment Settings") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Temperature (°C)",
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Slider(
                                    value = temperatureValue,
                                    onValueChange = { temperatureValue = it },
                                    valueRange = -5f..40f,
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = VendingMachineColors.ButtonColor,
                                        activeTrackColor = VendingMachineColors.AccentColor
                                    )
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    formatToOneDecimalPlace(temperatureValue) + "°C",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* Apply temperature change */ },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VendingMachineColors.ButtonColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Apply", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Fault Simulator
                    SimulatorCard(title = "Fault Simulator") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Coin Jam",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )

                                Switch(
                                    checked = coinJamEnabled,
                                    onCheckedChange = { coinJamEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = VendingMachineColors.ButtonColor,
                                        checkedTrackColor = VendingMachineColors.AccentColor,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Dispenser Jam",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )

                                Switch(
                                    checked = dispenserJamEnabled,
                                    onCheckedChange = { dispenserJamEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = VendingMachineColors.ButtonColor,
                                        checkedTrackColor = VendingMachineColors.AccentColor,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION: Simulation Log
                    SimulatorCard(title = "Simulation Log") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = simulationLog,
                                onValueChange = { simulationLog = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                readOnly = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                    unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                    focusedTextColor = VendingMachineColors.DisplayColor,
                                    unfocusedTextColor = VendingMachineColors.DisplayColor
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { /* Run simulation */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VendingMachineColors.ButtonColor
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Run Simulation")
                                }

                                Button(
                                    onClick = { simulationLog = "Simulation log cleared." },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VendingMachineColors.AccentColor
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Clear Log")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SimulatorCard(
        title: String,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachineBackground.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                content()
            }
        }
    }
}
