package org.junaed.vending_machine.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import org.junaed.vending_machine.ui.theme.VendingMachineColors
import org.junaed.vending_machine.ui.utils.WindowSize
import org.junaed.vending_machine.ui.utils.rememberWindowSize
import org.junaed.vending_machine.viewmodel.MaintenanceViewModel
import kotlin.math.round

/**
 * Maintenance Screen
 * This screen provides admin-level access to maintain the vending machine:
 * - Restock products
 * - Adjust prices
 * - Count coins and cans
 * - View and collect cash
 */
class MaintenanceScreen : Screen {

    /**
     * Helper function to format double values to 2 decimal places
     * Based on the implementation from VendingMachineService
     */
    private fun formatToTwoDecimalPlaces(value: Double): String {
        val roundedValue = round(value * 100) / 100
        return buildString {
            append(roundedValue.toInt())
            append('.')
            val fraction = ((roundedValue - roundedValue.toInt()) * 100).toInt()
            if (fraction < 10) {
                append('0')
            }
            append(fraction)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        // Initialize the ViewModel with persistent storage support
        val viewModel = remember { MaintenanceViewModel() }

        // State variables for UI components
        var passwordDigits by remember { mutableStateOf(List(6) { "" }) }
        var isPasswordInvalid by remember { mutableStateOf(false) }
        var isAuthenticated by remember { mutableStateOf(false) }
        var selectedDrink by remember { mutableStateOf("Drink 1") }
        var newPrice by remember { mutableStateOf("") }
        var totalCoins by remember { mutableIntStateOf(0) }
        var totalCans by remember { mutableIntStateOf(0) }
        var cashDisplay by remember { mutableStateOf("") }
        var showCashSlot by remember { mutableStateOf(false) }

        // Create focus requesters for each password digit field
        val focusRequesters = remember { List(6) { FocusRequester() } }

        // Check window size for responsive design
        val windowSize = rememberWindowSize()
        val isDesktop = windowSize == WindowSize.EXPANDED

        // Adjust field size and padding based on platform
        val digitFieldSize = if (isDesktop) 56.dp else 52.dp // Increased size for mobile
        val digitFieldPadding = if (isDesktop) 6.dp else 2.dp // Reduced padding for mobile to prevent text cutoff

        // Function to verify password
        val verifyPassword = {
            val password = passwordDigits.joinToString("")
            if (password.length == 6 && password == "123456") {
                isAuthenticated = true
                isPasswordInvalid = false
            } else {
                isPasswordInvalid = true
            }
        }

        val navigator = LocalNavigator.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Maintainer Panel", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.clearAuthentication()
                            navigator?.pop()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VendingMachineColors.MachinePanelColor,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isAuthenticated) {
                    // PASSWORD ENTRY SECTION
                    Text(
                        "TYPE PASSWORD HERE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // 6-digit password input fields with auto-focus
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        passwordDigits.forEachIndexed { index, digit ->
                            OutlinedTextField(
                                value = digit,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                        // Update the digit value
                                        val newDigits = passwordDigits.toMutableList()
                                        newDigits[index] = newValue
                                        passwordDigits = newDigits

                                        // If a digit is entered and not the last field, focus next field
                                        if (newValue.isNotEmpty() && index < passwordDigits.size - 1) {
                                            focusRequesters[index + 1].requestFocus()
                                        }
                                        // If it's the last field and a digit is entered, auto-submit
                                        else if (newValue.isNotEmpty() && index == passwordDigits.size - 1) {
                                            verifyPassword()
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .size(digitFieldSize)
                                    .focusRequester(focusRequesters[index])
                                    .padding(horizontal = digitFieldPadding),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center,
                                    // Reduce text size on desktop for better display within field
                                    fontSize = if (isDesktop) 18.sp else 16.sp
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                                    unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f)
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    // Request focus on first field when screen is shown
                    LaunchedEffect(Unit) {
                        delay(300) // Short delay to ensure UI is ready
                        focusRequesters[0].requestFocus()
                    }

                    // Error message for invalid password
                    if (isPasswordInvalid) {
                        Text(
                            "PASSWORD INVALID",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Clear password fields and refocus first field after showing error
                        LaunchedEffect(isPasswordInvalid) {
                            if (isPasswordInvalid) {
                                delay(1000) // Show error message for a moment
                                passwordDigits = List(6) { "" }
                                delay(300)
                                focusRequesters[0].requestFocus()
                                isPasswordInvalid = false // Hide error message when clearing fields
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { verifyPassword() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VendingMachineColors.ButtonColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("VERIFY PASSWORD", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // AUTHENTICATED SECTIONS

                    // COIN COUNT SECTION
                    SectionCard(title = "Coin Count") {
                        Text(
                            "Press the button to count the coins of the selected denomination",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CoinButton("10C") { totalCoins++ }
                            CoinButton("20C") { totalCoins++ }
                            CoinButton("50C") { totalCoins++ }
                            CoinButton("RM 1") { totalCoins++ }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Total Coins: $totalCoins", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CAN COUNT SECTION
                    SectionCard(title = "Can Count") {
                        Text(
                            "Press the button to count the cans of the selected drink",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Row 1 of drink buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DrinkButton("Drink 1") {
                                selectedDrink = "Drink 1"
                                totalCans++
                            }
                            DrinkButton("Drink 2") {
                                selectedDrink = "Drink 2"
                                totalCans++
                            }
                            DrinkButton("Drink 3") {
                                selectedDrink = "Drink 3"
                                totalCans++
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Row 2 of drink buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DrinkButton("Drink 4") {
                                selectedDrink = "Drink 4"
                                totalCans++
                            }
                            DrinkButton("Drink 5") {
                                selectedDrink = "Drink 5"
                                totalCans++
                            }
                            // Empty space to maintain symmetry with the row above
                            Spacer(modifier = Modifier.width(80.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Total Cans: $totalCans", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // PRICE UPDATE SECTION
                    SectionCard(title = "Price Update") {
                        Text(
                            "Enter the new price for $selectedDrink",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("New Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                                unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Update price logic
                                newPrice.toDoubleOrNull()?.let { price ->
                                    viewModel.updateDrinkPrice(selectedDrink, price)
                                    newPrice = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VendingMachineColors.ButtonColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("UPDATE PRICE", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CASH VIEW & COLLECTION SECTION
                    SectionCard(title = "Cash View & Collection") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    // Display total cash using the formatting function
                                    cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.getTotalCash())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VendingMachineColors.ButtonColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("VIEW TOTAL CASH")
                            }

                            Button(
                                onClick = {
                                    // Collect cash logic using the formatting function
                                    showCashSlot = true
                                    cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.collectCash())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VendingMachineColors.ButtonColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("COLLECT CASH")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "COLLECT ALL CASH HERE",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cash slot display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    color = if (showCashSlot) VendingMachineColors.DisplayColor else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (cashDisplay.isNotEmpty()) {
                                Text(
                                    cashDisplay,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            } else {
                                Text(
                                    "Cash Slot",
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // FINALIZE BUTTON
                    Button(
                        onClick = {
                            // Record maintenance service and navigate back
                            viewModel.recordMaintenance()
                            navigator?.pop()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VendingMachineColors.AccentColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "PRESS HERE WHEN FINISHED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SectionCard(
        title: String,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachineBackground
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
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                content()
            }
        }
    }

    @Composable
    private fun CoinButton(
        denomination: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.AccentColor.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(denomination, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun DrinkButton(
        drinkName: String,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.AccentColor.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(drinkName, fontWeight = FontWeight.Bold)
        }
    }
}
