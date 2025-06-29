package org.junaed.vending_machine.simulator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * OverallControlScreen - Main control panel for the VMCS simulator
 *
 * This is the first screen QA opens to control the simulation.
 */
class OverallControlScreen : Screen {

    // Helper function to format doubles to 2 decimal places in a KMP-compatible way
    private fun formatTwoDecimalPlaces(value: Double): String {
        val intPart = value.toInt()
        val decimalPart = ((value - intPart) * 100).toInt()
        return "$intPart.${decimalPart.toString().padStart(2, '0')}"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Create and remember the view model instance
        val viewModel = remember { SimRuntimeViewModel() }

        // State for opened panels
        var customerPanelOpen by remember { mutableStateOf(false) }
        var maintainerPanelOpen by remember { mutableStateOf(false) }
        var machineryPanelOpen by remember { mutableStateOf(false) }

        // State for button debouncing
        var beginClickable by remember { mutableStateOf(true) }
        var endClickable by remember { mutableStateOf(true) }

        // Format current time
        val currentTime = remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            while (true) {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                // Use KMP-compatible string formatting
                val hour = now.hour.toString().padStart(2, '0')
                val minute = now.minute.toString().padStart(2, '0')
                val second = now.second.toString().padStart(2, '0')
                currentTime.value = "$hour:$minute:$second"
                delay(1000) // Update every second
            }
        }

        // Scroll state for the log
        val logScrollState = rememberLazyListState()

        // Auto-scroll to bottom when new log entry is added
        LaunchedEffect(viewModel.eventLog.size) {
            if (viewModel.eventLog.isNotEmpty()) {
                logScrollState.scrollToItem(viewModel.eventLog.size - 1)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VendingMachineColors.MachineBackground)
                .padding(16.dp)
        ) {
            // Header bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = VendingMachineColors.MachinePanelColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "VMCS Simulator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )

                    // LED indicator with text label for accessibility
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    if (viewModel.isRunning) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                                    CircleShape
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        )
                        Text(
                            if (viewModel.isRunning) "ON" else "OFF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (viewModel.isRunning) Color(0xFF2ECC71) else Color(0xFFE74C3C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Control buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // BEGIN SIMULATION button
                ControlButton(
                    text = "BEGIN SIMULATION PRESS",
                    enabled = !viewModel.isRunning && beginClickable,
                    onClick = {
                        if (beginClickable) {
                            beginClickable = false
                            viewModel.startSimulation()
                        }
                    }
                )

                // Add debouncing effect for BEGIN button
                if (!beginClickable) {
                    LaunchedEffect(beginClickable) {
                        delay(300) // Adjusted to 300ms as specified
                        beginClickable = true
                    }
                }

                // END SIMULATION button
                ControlButton(
                    text = "END SIMULATION PRESS",
                    enabled = viewModel.isRunning && endClickable,
                    onClick = {
                        if (endClickable) {
                            endClickable = false
                            viewModel.reset()
                        }
                    }
                )

                // Add debouncing effect for END button
                if (!endClickable) {
                    LaunchedEffect(endClickable) {
                        delay(300) // Adjusted to 300ms as specified
                        endClickable = true
                    }
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )

                // ACTIVATE CUSTOMER PANEL button
                ControlButton(
                    text = "ACTIVATED CUSTOMER PANEL PRESS",
                    enabled = viewModel.isRunning,
                    onClick = { customerPanelOpen = true }
                )

                // ACTIVATE MAINTAINER PANEL button
                ControlButton(
                    text = "ACTIVATED MAINTAINER PANEL PRESS",
                    enabled = viewModel.isRunning,
                    onClick = { maintainerPanelOpen = true }
                )

                // ACTIVATE MACHINERY SIMULATOR PANEL button
                ControlButton(
                    text = "ACTIVATED MACHINERY SIMULATOR PANEL PRESS",
                    enabled = viewModel.isRunning,
                    onClick = { machineryPanelOpen = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status strip
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        currentTime.value,
                        fontSize = 12.sp,
                        color = VendingMachineColors.DisplayColor
                    )
                    Text(
                        "Door: ${if (viewModel.doorLocked) "Locked" else "Unlocked"}",
                        fontSize = 12.sp,
                        color = VendingMachineColors.DisplayColor
                    )
                    Text(
                        "Cash: RM${formatTwoDecimalPlaces(viewModel.getTotalCoinValue())}",
                        fontSize = 12.sp,
                        color = VendingMachineColors.DisplayColor
                    )
                    Text(
                        "Cans: ${viewModel.getTotalCans()}",
                        fontSize = 12.sp,
                        color = VendingMachineColors.DisplayColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Event log window
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        "Event Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = VendingMachineColors.DisplayColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        state = logScrollState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (viewModel.eventLog.isEmpty()) {
                            item {
                                Text(
                                    "No events logged yet. Press BEGIN to start simulation.",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        } else {
                            items(viewModel.eventLog) { logEntry ->
                                Text(
                                    logEntry,
                                    color = VendingMachineColors.DisplayColor,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Open customer panel if requested
            if (customerPanelOpen) {
                Dialog(onDismissRequest = { customerPanelOpen = false }) {
                    // Using dummy screen instead of actual CustomerPanelScreen
                    DummyPanelScreen(
                        title = "Customer Panel",
                        message = "This is a dummy customer panel for testing purposes.",
                        viewModel = viewModel,
                        onClose = { customerPanelOpen = false } // Close dialog when X is clicked
                    )
                }
            }

            // Open maintainer panel if requested
            if (maintainerPanelOpen) {
                Dialog(onDismissRequest = { maintainerPanelOpen = false }) {
                    // Using dummy screen instead of actual MaintainerPanelScreen
                    DummyPanelScreen(
                        title = "Maintainer Panel",
                        message = "This is a dummy maintainer panel for testing purposes.",
                        viewModel = viewModel,
                        onClose = { maintainerPanelOpen = false } // Close dialog when X is clicked
                    )
                }
            }

            // Open machinery panel if requested
            if (machineryPanelOpen) {
                Dialog(onDismissRequest = { machineryPanelOpen = false }) {
                    // Using MachinerySimulationScreen as it doesn't affect the database
                    MachinerySimulationScreen(
                        viewModel = viewModel,
                        onClose = { machineryPanelOpen = false } // Close dialog when X is clicked
                    )
                }
            }
        }
    }

    @Composable
    private fun ControlButton(
        text: String,
        enabled: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text,
                fontWeight = FontWeight.Bold,
                color = if (enabled) Color.White else Color.Gray
            )
        }
    }
}
