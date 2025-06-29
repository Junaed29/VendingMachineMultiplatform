package org.junaed.vending_machine.simulator.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel.Brand
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel.Denom
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * MachinerySimulationScreen - Controls for simulating the physical vending machine
 *
 * This screen allows setting inventory levels and controlling the door lock.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinerySimulationScreen(
    viewModel: SimRuntimeViewModel,
    onClose: () -> Unit = {} // Add onClose callback parameter with default value
) {

    // State for showing toast messages
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    // Setup the toast timeout
    if (showToast) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showToast = false
        }
    }

    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachineBackground
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    TopAppBar(
                        title = {
                            Text(
                                "Machinery Simulation",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        actions = {
                            // Close button
                            IconButton(onClick = { onClose() }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = VendingMachineColors.MachinePanelColor
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Coin Float Section
                    Text(
                        "Coin Float (0-20 each)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Coin input fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Create a numeric field for each denomination
                        NumericField(
                            label = "10c",
                            initialValue = viewModel.coinCounts[Denom.CENT_10] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCoinCount(Denom.CENT_10, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "20c",
                            initialValue = viewModel.coinCounts[Denom.CENT_20] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCoinCount(Denom.CENT_20, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "50c",
                            initialValue = viewModel.coinCounts[Denom.CENT_50] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCoinCount(Denom.CENT_50, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "RM1",
                            initialValue = viewModel.coinCounts[Denom.RM_1] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCoinCount(Denom.RM_1, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Drink Stock Section
                    Text(
                        "Drink Stock (0-20 each)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Drink stock input fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Create a numeric field for each brand
                        NumericField(
                            label = "Coke",
                            initialValue = viewModel.canCounts[Brand.COKE] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCanCount(Brand.COKE, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "Sprite",
                            initialValue = viewModel.canCounts[Brand.SPRITE] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCanCount(Brand.SPRITE, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumericField(
                            label = "Vimto",
                            initialValue = viewModel.canCounts[Brand.VIMTO] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCanCount(Brand.VIMTO, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "Pepsi",
                            initialValue = viewModel.canCounts[Brand.PEPSI] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCanCount(Brand.PEPSI, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )

                        NumericField(
                            label = "Fanta",
                            initialValue = viewModel.canCounts[Brand.FANTA] ?: 0,
                            onValidChange = { newValue ->
                                viewModel.updateCanCount(Brand.FANTA, newValue)
                            },
                            onInvalidInput = {
                                toastMessage = "0-20 only"
                                showToast = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Door Lock Control
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Door Locked",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(end = 16.dp)
                        )

                        Checkbox(
                            checked = viewModel.doorLocked,
                            onCheckedChange = { isChecked ->
                                // Only allow checking the box (locking) when door is unlocked
                                if (isChecked && !viewModel.doorLocked) {
                                    viewModel.lockDoor()
                                } else if (!isChecked && viewModel.doorLocked) {
                                    viewModel.unlockDoor()
                                }
                            },
                            enabled = !viewModel.doorLocked, // Only enabled when door is unlocked
                            colors = CheckboxDefaults.colors(
                                checkedColor = VendingMachineColors.ButtonColor,
                                uncheckedColor = Color.Gray
                            )
                        )
                    }

                    // Toast message at the bottom
                    if (showToast) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE74C3C)
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    toastMessage,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Overlay to disable the UI when not running
                if (!viewModel.isRunning) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .alpha(0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Reusable numeric input field component for the machinery screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumericField(
    label: String,
    initialValue: Int,
    onValidChange: (Int) -> Unit,
    onInvalidInput: () -> Unit
) {
    var textValue by remember { mutableStateOf(initialValue.toString()) }

    // Helper function to validate input and perform updates
    // Moved here before it's used to resolve the reference error
    fun validateAndUpdateValue(
        newValue: String,
        currentValue: String,
        onValidChange: (Int) -> Unit,
        onInvalidInput: () -> Unit
    ): Boolean {
        // Handle empty input
        if (newValue.isEmpty()) {
            return true // Allow empty field during editing
        }

        // Trim whitespace and check for non-digit characters
        val trimmed = newValue.trim()
        if (trimmed.any { !it.isDigit() }) {
            onInvalidInput()
            return false
        }

        // Parse as integer, handling leading zeros
        val numericValue = trimmed.toIntOrNull() ?: 0

        // Check if valid (within 0-20 range)
        if (numericValue in 0..20) {
            onValidChange(numericValue)
            return true
        } else {
            onInvalidInput()
            return false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                // Use a named lambda function for validation with explicit returns
                val isValid = validateAndUpdateValue(
                    newValue = newValue,
                    currentValue = textValue,
                    onValidChange = onValidChange,
                    onInvalidInput = onInvalidInput
                )

                if (isValid) {
                    textValue = newValue
                }
            },
            modifier = Modifier.width(60.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedTextColor = VendingMachineColors.DisplayColor,
                unfocusedTextColor = VendingMachineColors.DisplayColor,
                cursorColor = VendingMachineColors.DisplayColor,
                focusedIndicatorColor = VendingMachineColors.ButtonColor,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.Black.copy(alpha = 0.5f),
                unfocusedContainerColor = Color.Black.copy(alpha = 0.5f)
            )
        )
    }
}
