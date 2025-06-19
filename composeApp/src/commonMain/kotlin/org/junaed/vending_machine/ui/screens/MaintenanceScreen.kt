package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

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
        // State variables for UI components
        var password by remember { mutableStateOf("") }
        var isAuthenticated by remember { mutableStateOf(false) }
        var stockLevel by remember { mutableStateOf("") }

        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Maintenance Mode", fontWeight = FontWeight.Bold) },
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isAuthenticated) {
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
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // TODO: Implement authentication logic
                            // Check if password is correct and update isAuthenticated
                            isAuthenticated = true // For demo purposes
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Login")
                    }
                } else {
                    // SECTION: Maintenance Controls
                    Text(
                        "MAINTENANCE CONTROLS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // SUBSECTION: Inventory Management
                    Text(
                        "Inventory Management",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stockLevel,
                        onValueChange = { stockLevel = it },
                        label = { Text("Sparkle Pop Stock Level") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // TODO: Implement stock update logic
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Stock")
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // SUBSECTION: Sales Data
                    Text(
                        "Sales Data",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // TODO: Implement a sales data display component
                    Text(
                        "Total Sales: $120.50",
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        "Total Items Sold: 45",
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Divider(modifier = Modifier.padding(vertical = 16.dp))

                    // SUBSECTION: System Maintenance
                    Text(
                        "System Maintenance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // TODO: Implement error clearing logic
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Error Logs")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // TODO: Implement system reset logic
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset System")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // TODO: Implement logout logic
                            isAuthenticated = false
                            password = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exit Maintenance Mode")
                    }
                }
            }
        }
    }
}
