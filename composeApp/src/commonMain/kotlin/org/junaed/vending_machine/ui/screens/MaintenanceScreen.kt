package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junaed.vending_machine.viewmodel.MaintenanceViewModel

/**
 * Maintenance Screen
 * This screen provides admin-level access to maintain the vending machine:
 * - Restock products
 * - Adjust prices
 * - View sales data
 * - Clear errors
 */
class MaintenanceScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // Initialize the ViewModel with persistent storage support
        val viewModel = remember { MaintenanceViewModel() }

        // State variables for UI components
        var password by remember { mutableStateOf("") }
        var stockLevel by remember { mutableStateOf("") }
        var selectedDrink by remember { mutableStateOf("BRAND 1") }
        var newPrice by remember { mutableStateOf("") }

        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Maintenance Mode", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.clearAuthentication()
                            navigator?.pop()
                        }) {
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!viewModel.isAuthenticated) {
                    // SECTION: Authentication
                    Text(
                        "MAINTENANCE ACCESS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Enter Maintenance Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (viewModel.authenticate(password)) {
                                // Password correct, now authenticated
                                password = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LOGIN")
                    }

                    // Show error message if authentication failed
                    viewModel.errorMessage?.let { error ->
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                } else {
                    // SECTION: Maintenance Controls (shown after authentication)

                    // Display last maintenance timestamp if available
                    if (viewModel.currentSettings.lastMaintenanceDate > 0) {
                        val timestamp = viewModel.currentSettings.lastMaintenanceDate
                        val instant = Instant.fromEpochMilliseconds(timestamp)
                        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                        Text(
                            "Last Maintenance: ${dateTime.date} ${dateTime.hour}:${dateTime.minute}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Stock Management Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "STOCK MANAGEMENT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Drink selection dropdown
                            ExposedDropdownMenuBox(
                                expanded = false,
                                onExpandedChange = { },
                            ) {
                                OutlinedTextField(
                                    value = selectedDrink,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Select Drink") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = false,
                                    onDismissRequest = { },
                                ) {
                                    listOf("BRAND 1", "BRAND 2", "BRAND 3", "BRAND 4", "BRAND 5").forEach { drink ->
                                        DropdownMenuItem(
                                            text = { Text(drink) },
                                            onClick = {
                                                selectedDrink = drink
                                                // Update stock level display when drink changes
                                                stockLevel = viewModel.currentSettings.drinkStockLevels[drink]?.toString() ?: "0"
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stock level input
                            OutlinedTextField(
                                value = stockLevel,
                                onValueChange = { stockLevel = it },
                                label = { Text("Stock Level") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    stockLevel.toIntOrNull()?.let { level ->
                                        viewModel.updateStockLevel(selectedDrink, level)
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Update Stock")
                            }
                        }
                    }

                    // Price Management Section
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "PRICE MANAGEMENT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Current price display
                            val currentPrice = viewModel.currentSettings.priceSettings[selectedDrink]?.toString() ?: "0.00"
                            Text(
                                "Current Price: RM $currentPrice",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // New price input
                            OutlinedTextField(
                                value = newPrice,
                                onValueChange = { newPrice = it },
                                label = { Text("New Price (RM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    newPrice.toDoubleOrNull()?.let { price ->
                                        viewModel.updateDrinkPrice(selectedDrink, price)
                                        newPrice = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Update Price")
                            }
                        }
                    }

                    // Maintenance Service Record Button
                    Button(
                        onClick = {
                            viewModel.recordMaintenance()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("RECORD MAINTENANCE SERVICE")
                    }
                }
            }
        }
    }
}
