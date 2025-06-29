package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junaed.vending_machine.ui.screens.simulator.viewmodel.SimRuntimeViewModel
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
        val navigator = LocalNavigator.current

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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "VMCS Simulator",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        containerColor = VendingMachineColors.MachinePanelColor,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .background(VendingMachineColors.MachineBackground)
                    .padding(innerPadding)
            ) {
                val screenWidth = maxWidth
                val isWideScreen = screenWidth > 600.dp

                if (isWideScreen) {
                    // Wide layout (tablets, desktop, web) - Controls on left, log on right
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left column - Controls
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatusBox(viewModel)

                            // Control buttons in a grid layout for wide screens
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SimulationControlButtons(viewModel, beginClickable, endClickable) {
                                    beginClickable = it
                                    endClickable = it
                                }

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .padding(vertical = 8.dp),
                                    color = Color.White.copy(alpha = 0.3f)
                                )

                                PanelControlButtons(
                                    viewModel = viewModel,
                                    onCustomerPanelClick = { customerPanelOpen = true },
                                    onMaintainerPanelClick = { maintainerPanelOpen = true },
                                    onMachineryPanelClick = { machineryPanelOpen = true }
                                )
                            }

                            StatusStrip(viewModel, currentTime.value)
                        }

                        // Right column - Event log
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            EventLog(viewModel, logScrollState)
                        }
                    }
                } else {
                    // Narrow layout (phones) - Vertical layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatusBox(viewModel)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SimulationControlButtons(viewModel, beginClickable, endClickable) {
                                beginClickable = it
                                endClickable = it
                            }

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(vertical = 8.dp),
                                color = Color.White.copy(alpha = 0.3f)
                            )

                            PanelControlButtons(
                                viewModel = viewModel,
                                onCustomerPanelClick = { customerPanelOpen = true },
                                onMaintainerPanelClick = { maintainerPanelOpen = true },
                                onMachineryPanelClick = { machineryPanelOpen = true }
                            )
                        }

                        StatusStrip(viewModel, currentTime.value)

                        // Event log takes remaining space
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            EventLog(viewModel, logScrollState)
                        }
                    }
                }

                // Dialog screens remain the same
                if (customerPanelOpen) {
                    Dialog(onDismissRequest = { customerPanelOpen = false }) {
                        CustomerPanelScreen(
                            viewModel = viewModel,
                            onClose = { customerPanelOpen = false }
                        )
                    }
                }

                if (maintainerPanelOpen) {
                    Dialog(onDismissRequest = { maintainerPanelOpen = false }) {
                        MaintainerPanelScreen(
                            viewModel = viewModel,
                            onClose = { maintainerPanelOpen = false }
                        )
                    }
                }

                if (machineryPanelOpen) {
                    Dialog(onDismissRequest = { machineryPanelOpen = false }) {
                        MachinerySimulationScreen(
                            viewModel = viewModel,
                            onClose = { machineryPanelOpen = false }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusBox(viewModel: SimRuntimeViewModel) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.7f)
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
                    "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
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
    }

    @Composable
    private fun SimulationControlButtons(
        viewModel: SimRuntimeViewModel,
        beginClickable: Boolean,
        endClickable: Boolean,
        onClickabilityChange: (Boolean) -> Unit
    ) {
        // BEGIN SIMULATION button
        ControlButton(
            text = "BEGIN SIMULATION PRESS",
            enabled = !viewModel.isRunning && beginClickable,
            onClick = {
                if (beginClickable) {
                    onClickabilityChange(false)
                    viewModel.startSimulation()
                }
            }
        )

        // Handle debounce effect for BEGIN button outside the onClick
        if (!beginClickable) {
            LaunchedEffect(beginClickable) {
                delay(300)
                onClickabilityChange(true)
            }
        }

        // END SIMULATION button
        ControlButton(
            text = "END SIMULATION PRESS",
            enabled = viewModel.isRunning && endClickable,
            onClick = {
                if (endClickable) {
                    onClickabilityChange(false)
                    viewModel.reset()
                }
            }
        )

        // Handle debounce effect for END button outside the onClick
        if (!endClickable) {
            LaunchedEffect(endClickable) {
                delay(300)
                onClickabilityChange(true)
            }
        }
    }

    @Composable
    private fun PanelControlButtons(
        viewModel: SimRuntimeViewModel,
        onCustomerPanelClick: () -> Unit,
        onMaintainerPanelClick: () -> Unit,
        onMachineryPanelClick: () -> Unit
    ) {
        // ACTIVATE CUSTOMER PANEL button
        ControlButton(
            text = "ACTIVATED CUSTOMER PANEL PRESS",
            enabled = viewModel.isRunning,
            onClick = onCustomerPanelClick
        )

        // ACTIVATE MAINTAINER PANEL button
        ControlButton(
            text = "ACTIVATED MAINTAINER PANEL PRESS",
            enabled = viewModel.isRunning,
            onClick = onMaintainerPanelClick
        )

        // ACTIVATE MACHINERY SIMULATOR PANEL button
        ControlButton(
            text = "ACTIVATED MACHINERY SIMULATOR PANEL PRESS",
            enabled = viewModel.isRunning,
            onClick = onMachineryPanelClick
        )
    }

    @Composable
    private fun StatusStrip(viewModel: SimRuntimeViewModel, currentTime: String) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val isNarrow = maxWidth < 400.dp

                if (isNarrow) {
                    // Vertical layout for very narrow screens
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatusText(text = "Time: $currentTime")
                        StatusText(text = "Door: ${if (viewModel.doorLocked) "Locked" else "Unlocked"}")
                        StatusText(text = "Cash: RM${formatTwoDecimalPlaces(viewModel.getTotalCoinValue())}")
                        StatusText(text = "Cans: ${viewModel.getTotalCans()}")
                    }
                } else {
                    // Horizontal layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatusText(text = currentTime)
                        StatusText(text = "Door: ${if (viewModel.doorLocked) "Locked" else "Unlocked"}")
                        StatusText(text = "Cash: RM${formatTwoDecimalPlaces(viewModel.getTotalCoinValue())}")
                        StatusText(text = "Cans: ${viewModel.getTotalCans()}")
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusText(text: String) {
        Text(
            text,
            fontSize = 12.sp,
            color = VendingMachineColors.DisplayColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    @Composable
    private fun EventLog(viewModel: SimRuntimeViewModel, logScrollState: LazyListState) {
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
                .fillMaxWidth()
                .widthIn(max = 400.dp)
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
                color = if (enabled) Color.White else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
