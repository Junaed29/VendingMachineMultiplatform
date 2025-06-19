package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

/**
 * Simulator Screen
 * This screen allows users to simulate various vending machine scenarios:
 * - Test different purchase flows
 * - Simulate errors and edge cases
 * - Demonstrate machine behavior
 */
class SimulatorScreen : Screen {
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
                    title = { Text("Vending Machine Simulator", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { innerPadding ->
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
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // SECTION: Environment Controls
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Environment Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Temperature control
                        val formattedTemp = (temperatureValue * 10).toInt() / 10.0
                        Text("Temperature: $formattedTemp°C")
                        Slider(
                            value = temperatureValue,
                            onValueChange = { temperatureValue = it },
                            valueRange = -5f..40f,
                            steps = 45,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Time of day simulation could be added here
                        // TODO: Add time of day simulation controls
                    }
                }

                // SECTION: Fault Simulation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Fault Simulation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Coin jam toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Simulate Coin Jam")
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = coinJamEnabled,
                                onCheckedChange = { coinJamEnabled = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dispenser jam toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Simulate Dispenser Jam")
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = dispenserJamEnabled,
                                onCheckedChange = { dispenserJamEnabled = it }
                            )
                        }

                        // TODO: Add more fault simulation options
                    }
                }

                // SECTION: Simulation Actions
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Simulation Actions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // TODO: Implement simulation logic
                                simulationLog += "\nStarted standard transaction simulation..."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Run Standard Transaction")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                // TODO: Implement stress test logic
                                simulationLog += "\nStarted stress test simulation..."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Run Stress Test (100 Transactions)")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                // TODO: Implement edge case simulation
                                simulationLog += "\nSimulating edge cases..."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Simulate Edge Cases")
                        }
                    }
                }

                // SECTION: Simulation Log
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Simulation Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = simulationLog,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { simulationLog = "Simulation log will appear here..." },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Clear Log")
                        }
                    }
                }
            }
        }
    }
}
