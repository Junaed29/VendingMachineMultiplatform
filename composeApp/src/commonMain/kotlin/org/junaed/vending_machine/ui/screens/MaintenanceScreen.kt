package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

    //----------------------------------------------------------------------------------------------
    // UTILITY METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Helper function to format double values to 2 decimal places
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

    //----------------------------------------------------------------------------------------------
    // MAIN SCREEN CONTENT
    //----------------------------------------------------------------------------------------------

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        // Initialize the ViewModel with persistent storage support
        val viewModel = remember { MaintenanceViewModel() }
        val navigator = LocalNavigator.current
        val windowSize = rememberWindowSize()
        val isDesktop = windowSize == WindowSize.EXPANDED

        // State variables
        var isAuthenticated by remember { mutableStateOf(viewModel.isMaintenanceMode) }
        var cashDisplay by remember { mutableStateOf("") }

        // Check if already in maintenance mode when screen is shown
        LaunchedEffect(Unit) {
            if (viewModel.isMaintenanceMode) {
                // Already authenticated, no need to show password entry
                isAuthenticated = true
            }
        }

        Scaffold(
            topBar = {
                MaintenanceTopAppBar(
                    onBackClick = {
                       // Uncomment to require re-authentication when returning to this screen
                       // viewModel.clearAuthentication()
                        navigator?.pop()
                    }
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
                    // Login section when not authenticated
                    PasswordEntrySection(
                        viewModel = viewModel,
                        isDesktop = isDesktop,
                        onAuthenticated = { isAuthenticated = true }
                    )
                } else {
                    // Maintenance mode sections when authenticated
                    MaintenanceActiveCard(message = viewModel.maintenanceMessage)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Coin count section
                    CoinManagementSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Drink inventory and price management section
                    DrinkInventoryAndPriceSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cash collection section
                    CashCollectionSection(
                        cashDisplay = cashDisplay,
                        onViewTotalCash = {
                            cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.getTotalCash())
                        },
                        onCollectCash = {
                            cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.collectCash())
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Finalize maintenance button
                    FinalizeMaintananceButton(
                        onClick = {
                            viewModel.recordMaintenance()
                            navigator?.pop()
                        }
                    )
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // TOP APP BAR
    //----------------------------------------------------------------------------------------------

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MaintenanceTopAppBar(onBackClick: () -> Unit) {
        TopAppBar(
            title = { Text("Maintainer Panel", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                        tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = VendingMachineColors.MachinePanelColor,
                titleContentColor = Color.White
            )
        )
    }

    //----------------------------------------------------------------------------------------------
    // PASSWORD ENTRY SECTION
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun PasswordEntrySection(
        viewModel: MaintenanceViewModel,
        isDesktop: Boolean,
        onAuthenticated: () -> Unit
    ) {
        // State variables for password entry
        var passwordDigits by remember { mutableStateOf(List(6) { "" }) }
        var isPasswordInvalid by remember { mutableStateOf(false) }

        // Create focus requesters for each digit field
        val focusRequesters = remember { List(6) { FocusRequester() } }

        // Field size and padding based on platform
        val digitFieldSize = if (isDesktop) 56.dp else 52.dp
        val digitFieldPadding = if (isDesktop) 6.dp else 2.dp

        // Function to verify password
        val verifyPassword = {
            val password = passwordDigits.joinToString("")
            if (viewModel.validatePassword(password)) {
                isPasswordInvalid = false
                onAuthenticated()
            } else {
                isPasswordInvalid = true
            }
        }

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
    }

    //----------------------------------------------------------------------------------------------
    // MAINTENANCE ACTIVE CARD
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun MaintenanceActiveCard(message: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.AccessGrantedColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "MAINTENANCE MODE ACTIVE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    "Door Unlocked - Access Granted",
                    fontSize = 14.sp,
                    color = Color.White
                )
                if (message.isNotEmpty()) {
                    Text(
                        message,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // SECTION CARD TEMPLATE
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun SectionCard(
        title: String,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
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

    //----------------------------------------------------------------------------------------------
    // COIN MANAGEMENT SECTION
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun CoinManagementSection(viewModel: MaintenanceViewModel) {
        // State for tracking selected denomination
        var selectedDenomination by remember { mutableStateOf(10) } // Default to 10 sen

        SectionCard(title = "Coin Count") {
            Text(
                "Select a coin denomination to view count",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Radio buttons for denomination selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 10 Sen radio button
                CoinRadioButton(
                    text = "10C",
                    selected = selectedDenomination == 10,
                    onClick = { selectedDenomination = 10 }
                )

                // 20 Sen radio button
                CoinRadioButton(
                    text = "20C",
                    selected = selectedDenomination == 20,
                    onClick = { selectedDenomination = 20 }
                )

                // 50 Sen radio button
                CoinRadioButton(
                    text = "50C",
                    selected = selectedDenomination == 50,
                    onClick = { selectedDenomination = 50 }
                )

                // 1 Ringgit radio button
                CoinRadioButton(
                    text = "RM1",
                    selected = selectedDenomination == 100,
                    onClick = { selectedDenomination = 100 }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Label for the coin count display
            Text(
                "TOTAL NUMBER OF COINS IN SELECTED DENOMINATION",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grey box for displaying the count (fetched from the database via ViewModel)
            val count = viewModel.coinsByDenomination[selectedDenomination] ?: 0
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            /*
            // Update quantity field for the selected denomination
            CoinQuantityUpdateField(
                selectedDenomination = selectedDenomination,
                currentCount = count,
                onUpdateQuantity = { newQuantity ->
                    viewModel.updateCoinQuantity(selectedDenomination, newQuantity)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
             */

            // Total cash value
            Text(
                "Total Cash Value: RM ${formatToTwoDecimalPlaces(viewModel.calculateTotalCoinValue())}",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    @Composable
    private fun CoinRadioButton(
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected)
                    VendingMachineColors.AccentColor
                else
                    VendingMachineColors.ButtonColor.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .height(40.dp)
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }

    //----------------------------------------------------------------------------------------------
    // DRINK INVENTORY AND PRICE SECTION (COMBINED)
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun DrinkInventoryAndPriceSection(viewModel: MaintenanceViewModel) {
        // State for tracking selected brand
        var selectedBrand by remember { mutableStateOf("BRAND 1") } // Default to BRAND 1
        var newPrice by remember { mutableStateOf("") }

        // Get the current inventory count
        val count = viewModel.drinkStockLevels[selectedBrand] ?: 0

        SectionCard(title = "Drink Inventory & Price Management") {
            Text(
                "Select a drink brand to manage inventory and pricing",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Using FlowRow to automatically wrap buttons to multiple lines on smaller screens
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(12.dp) // Added spacing between rows
            ) {
                // Create radio buttons for each brand
                viewModel.drinkStockLevels.keys.sorted().forEach { brand ->
                    DrinkRadioButton(
                        text = brand,
                        selected = selectedBrand == brand,
                        onClick = { selectedBrand = brand }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Label for the can count display
            Text(
                "TOTAL NUMBER OF CANS IN SELECTED BRAND",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Grey box for displaying the count (fetched from the database via ViewModel)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity update field for selected brand
            DrinkQuantityUpdateField(
                selectedBrand = selectedBrand,
                currentCount = count,
                onUpdateQuantity = { newQuantity ->
                    viewModel.updateDrinkQuantity(selectedBrand, newQuantity)
                    true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total drinks
            Text(
                "Total Drinks: ${viewModel.drinkStockLevels.values.sum()}",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Price management section
            Text(
                "PRICE MANAGEMENT",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            // Current price display
            val currentPrice = viewModel.drinkPriceSettings[selectedBrand] ?: 0.0
            Text(
                "Current Price: RM ${formatToTwoDecimalPlaces(currentPrice)}",
                color = VendingMachineColors.DisplayColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // New price input
            OutlinedTextField(
                value = newPrice,
                onValueChange = { input ->
                    // Only accept valid decimal numbers
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        newPrice = input
                    }
                },
                label = { Text("New Price (RM)", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                    unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Update price logic with validation
                    newPrice.toDoubleOrNull()?.let { price ->
                        if (price > 0) {
                            viewModel.updateDrinkPrice(selectedBrand, price)
                            newPrice = ""
                        }
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
    }

    @Composable
    private fun DrinkRadioButton(
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected)
                    VendingMachineColors.AccentColor
                else
                    VendingMachineColors.ButtonColor.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .height(40.dp)
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }

    //----------------------------------------------------------------------------------------------
    // COIN QUANTITY UPDATE FIELD
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun CoinQuantityUpdateField(
        selectedDenomination: Int,
        currentCount: Int,
        onUpdateQuantity: (Int) -> Boolean
    ) {
        // State variables for the update field
        var newValue by remember(selectedDenomination) { mutableStateOf("$currentCount") }
        var showUpdateField by remember(selectedDenomination) { mutableStateOf(false) }

        if (showUpdateField) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "New Quantity: ",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = newValue,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            newValue = value
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                        unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val quantity = newValue.toIntOrNull() ?: 0
                        if (onUpdateQuantity(quantity)) {
                            showUpdateField = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Save", fontSize = 12.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { showUpdateField = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Update Coin Quantity", fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // CASH COLLECTION SECTION
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun CashCollectionSection(
        cashDisplay: String,
        onViewTotalCash: () -> Unit,
        onCollectCash: () -> Unit
    ) {
        // Local state to track if cash is in the dispenser (collected but not yet taken)
        var cashInDispenser by remember { mutableStateOf(false) }
        var dispenserAmount by remember { mutableStateOf("") }

        // Remember the last valid cash amount for collection
        var lastCashAmount by remember { mutableStateOf("") }

        // Ensure dispenser shows as active whenever it contains cash
        LaunchedEffect(dispenserAmount) {
            cashInDispenser = dispenserAmount.isNotEmpty()
        }

        // Needed to capture any changes in cashDisplay for the collect button
        LaunchedEffect(cashDisplay) {
            if (cashDisplay.isNotEmpty()) {
                lastCashAmount = cashDisplay
            }
        }

        SectionCard(title = "Cash View & Collection") {
            // View Total Cash Button
            Button(
                onClick = {
                    onViewTotalCash()
                    // Only set dispenserAmount if it's not RM 0.00
                    if (cashDisplay != "RM 0.00") {
                        dispenserAmount = cashDisplay
                    } else {
                        dispenserAmount = ""
                    }
                    // Force dispenser to inactive state for zero amount
                    if (cashDisplay == "RM 0.00") {
                        cashInDispenser = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VendingMachineColors.ButtonColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "PRESS HERE TO VIEW TOTAL CASH HELD BY MACHINE",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display total cash value
            if (cashDisplay.isNotEmpty() && !cashInDispenser) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        // Using the most current cashDisplay value
                        text = cashDisplay,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Collect All Cash Button
            Button(
                onClick = {
                    // Capture the current cash display before collecting
                    if (cashDisplay.isNotEmpty() && cashDisplay != "RM 0.00") {
                        lastCashAmount = cashDisplay
                    }

                    // Call the collect cash function
                    onCollectCash()

                    // Use the last known cash amount if available, otherwise use current display
                    if (lastCashAmount.isNotEmpty() && lastCashAmount != "RM 0.00") {
                        dispenserAmount = lastCashAmount
                        cashInDispenser = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VendingMachineColors.ButtonColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "TRANSFER CASH TO DISPENSER",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "COLLECT ALL CASH HERE",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cash dispenser box - clickable to clear when cash is present
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = if (cashInDispenser) VendingMachineColors.DisplayColor else Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    // Make it clickable when cash is in the dispenser
                    .then(
                        if (cashInDispenser) {
                            Modifier.clickable {
                                // Execute the actual cash collection
                                onCollectCash()

                                // Clear the dispenser (simulating cash being taken)
                                cashInDispenser = false
                                dispenserAmount = ""

                                // Refresh the cash display to show the new total (which should be 0)
                                onViewTotalCash()
                            }
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Show the dispenser amount when cash is collected
                    if (cashInDispenser && dispenserAmount.isNotEmpty() ) {
                        Text(
                            text = dispenserAmount, // Using dispenserAmount instead of cashDisplay
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tap to collect cash",
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.DarkGray
                        )
                    } else {
                        Text(
                            "Cash Dispenser",
                            fontSize = 18.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            "Empty",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            if (cashInDispenser) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Cash ready for collection",
                    fontSize = 14.sp,
                    color = VendingMachineColors.DisplayColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // FINALIZE MAINTENANCE BUTTON
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun FinalizeMaintananceButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
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

    //----------------------------------------------------------------------------------------------
    // DRINK QUANTITY UPDATE FIELD
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun DrinkQuantityUpdateField(
        selectedBrand: String,
        currentCount: Int,
        onUpdateQuantity: (Int) -> Boolean
    ) {
        // State variables for the update field
        var newValue by remember(selectedBrand) { mutableStateOf("$currentCount") }
        var showUpdateField by remember(selectedBrand) { mutableStateOf(false) }

        if (showUpdateField) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "New Quantity: ",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = newValue,
                    onValueChange = { value ->
                        if (value.isEmpty() || value.all { it.isDigit() }) {
                            newValue = value
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(100.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                        unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val quantity = newValue.toIntOrNull() ?: 0
                        if (quantity >= 0 && onUpdateQuantity(quantity)) {
                            showUpdateField = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Save", fontSize = 12.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { showUpdateField = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Update Drink Quantity", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
