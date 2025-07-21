package org.junaed.vending_machine.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
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
        val isTablet = windowSize == WindowSize.MEDIUM

        // Calculate padding based on screen size
        val horizontalPadding = when {
            isDesktop -> 32.dp
            isTablet -> 24.dp
            else -> 16.dp
        }

        // State variables
        var isAuthenticated by remember { mutableStateOf(viewModel.isMaintenanceMode) }
        var cashDisplay by remember { mutableStateOf("") }

        // Power cut simulation state
        var showPowerCut by remember { mutableStateOf(false) }

        // Handle power cut simulation
        LaunchedEffect(showPowerCut) {
            if (showPowerCut) {
                // Show power cut for 2 seconds then restore
                delay(2000)
                showPowerCut = false
                navigator?.pop()
            }
        }

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
            // Apply the power cut overlay on top of everything when active
            Box(modifier = Modifier.fillMaxSize()) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    val maxWidth = this.maxWidth
                    val contentWidth = if (isDesktop || isTablet) maxWidth * 0.85f else maxWidth
                    val isWideScreen = maxWidth >= 840.dp

                    // Responsive layout - side-by-side sections for wide screens
                    if (isWideScreen && isAuthenticated) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = horizontalPadding),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left column - Maintenance info and coin management
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MaintenanceActiveCard(message = viewModel.maintenanceMessage)
                                CoinManagementSection(viewModel = viewModel)

                                // Add power cut simulation button
                                Button(
                                    onClick = { showPowerCut = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .widthIn(max = 400.dp)
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        "SIMULATE POWER CUT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                FinalizeMaintananceButton(
                                    onClick = {
                                        viewModel.recordMaintenance()
                                        navigator?.pop()
                                    }
                                )
                            }

                            // Right column - Drink inventory and cash collection
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                DrinkInventoryAndPriceSection(viewModel = viewModel, isWideScreen = isWideScreen)
                                CashCollectionSection(
                                    cashDisplay = cashDisplay,
                                    onViewTotalCash = {
                                        cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.getTotalCash())
                                    },
                                    onCollectCash = {
                                        cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.collectCash())
                                    }
                                )
                            }
                        }
                    } else {
                        // Standard vertical layout for narrow screens or login screen
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = horizontalPadding)
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (!isAuthenticated) {
                                // Login section when not authenticated
                                PasswordEntrySection(
                                    viewModel = viewModel,
                                    isDesktop = isDesktop,
                                    onAuthenticated = { isAuthenticated = true }
                                )
                            } else {
                                // Maintenance mode sections when authenticated (vertical layout)
                                MaintenanceActiveCard(message = viewModel.maintenanceMessage)
                                CoinManagementSection(viewModel = viewModel)
                                DrinkInventoryAndPriceSection(viewModel = viewModel, isWideScreen = false)
                                CashCollectionSection(
                                    cashDisplay = cashDisplay,
                                    onViewTotalCash = {
                                        cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.getTotalCash())
                                    },
                                    onCollectCash = {
                                        cashDisplay = "RM " + formatToTwoDecimalPlaces(viewModel.collectCash())
                                    }
                                )

                                // Add power cut simulation button
                                Button(
                                    onClick = { showPowerCut = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .widthIn(max = 400.dp)
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        "SIMULATE POWER CUT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

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

                // Power cut overlay
                PowerCutOverlay(isVisible = showPowerCut)
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
            title = {
                Text(
                    "Maintainer Panel",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
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
        var activeFieldIndex by remember { mutableIntStateOf(0) }

        // New state variables for toast-like messages
        var showPasswordMessage by remember { mutableStateOf(false) }
        var isPasswordValid by remember { mutableStateOf(false) }

        // Create focus requesters for each digit field
        val focusRequesters = remember { List(6) { FocusRequester() } }

        // Field size and padding based on platform - increased sizes for mobile
        // Mobile needs larger touch targets for comfortable interaction
        val digitFieldSize = if (isDesktop) 60.dp else 56.dp
        val digitFieldPadding = if (isDesktop) 6.dp else 4.dp

        // Function to verify password
        val verifyPassword = {
            val password = passwordDigits.joinToString("")
            if (viewModel.validatePassword(password)) {
                isPasswordValid = true
                showPasswordMessage = true
                // Authentication will be handled after showing the "PASSWORD VALID" message
            } else {
                isPasswordValid = false
                showPasswordMessage = true
                isPasswordInvalid = true
            }
        }

        // Handle the toast message visibility and actions
        LaunchedEffect(showPasswordMessage) {
            if (showPasswordMessage) {
                // Show message for a short duration
                delay(1000)
                showPasswordMessage = false

                if (isPasswordValid) {
                    // If password was valid, authenticate after showing message
                    onAuthenticated()
                } else {
                    // If password was invalid, clear fields and reset focus
                    passwordDigits = List(6) { "" }
                    delay(300)
                    focusRequesters[0].requestFocus()
                    activeFieldIndex = 0
                    isPasswordInvalid = false
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "TYPE PASSWORD HERE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Password input fields with adaptive layout
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val maxWidth = this.maxWidth
                    // Determine dynamic parameters based on screen width
                    // Use two-row layout for more devices to improve mobile experience
                    val isNarrowScreen = maxWidth < 400.dp && !isDesktop

                    // Adjust field size for narrower screens
                    val actualFieldSize = when {
                        maxWidth < 300.dp -> 48.dp
                        maxWidth < 360.dp -> 52.dp
                        else -> digitFieldSize
                    }

                    // Increased spacing for better touch targets on mobile
                    val fieldSpacing = if (isDesktop) 8.dp else 12.dp

                    // Split into two rows for mobile and narrower screens for better usability
                    if (isNarrowScreen) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp) // Increased vertical spacing
                        ) {
                            // First row: first 3 digits
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (index in 0 until 3) {
                                    PasswordDigitField(
                                        digit = passwordDigits[index],
                                        onValueChange = {
                                            handleDigitChange(index, it, passwordDigits, focusRequesters) { newDigits ->
                                                passwordDigits = newDigits
                                                activeFieldIndex = if (it.isNotEmpty()) index + 1 else index
                                            }
                                        },
                                        focusRequester = focusRequesters[index],
                                        size = actualFieldSize,
                                        padding = digitFieldPadding,
                                        isDesktop = isDesktop,
                                        isActive = activeFieldIndex == index,
                                        onNext = {
                                            if (index < 5) focusRequesters[index + 1].requestFocus()
                                            else verifyPassword()
                                        }
                                    )
                                }
                            }

                            // Second row: last 3 digits
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (index in 3 until 6) {
                                    PasswordDigitField(
                                        digit = passwordDigits[index],
                                        onValueChange = {
                                            handleDigitChange(index, it, passwordDigits, focusRequesters) { newDigits ->
                                                passwordDigits = newDigits
                                                activeFieldIndex = if (it.isNotEmpty()) index + 1 else index
                                            }
                                        },
                                        focusRequester = focusRequesters[index],
                                        size = actualFieldSize,
                                        padding = digitFieldPadding,
                                        isDesktop = isDesktop,
                                        isActive = activeFieldIndex == index,
                                        onNext = {
                                            if (index < 5) focusRequesters[index + 1].requestFocus()
                                            else verifyPassword()
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Single row layout for wider screens
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            passwordDigits.forEachIndexed { index, digit ->
                                PasswordDigitField(
                                    digit = digit,
                                    onValueChange = {
                                        handleDigitChange(index, it, passwordDigits, focusRequesters) { newDigits ->
                                            passwordDigits = newDigits
                                            activeFieldIndex = if (it.isNotEmpty()) index + 1 else index
                                        }
                                    },
                                    focusRequester = focusRequesters[index],
                                    size = actualFieldSize,
                                    padding = digitFieldPadding,
                                    isDesktop = isDesktop,
                                    isActive = activeFieldIndex == index,
                                    onNext = {
                                        if (index < 5) focusRequesters[index + 1].requestFocus()
                                        else verifyPassword()
                                    }
                                )
                            }
                        }
                    }
                }

                // Request focus on first field when screen is shown
                LaunchedEffect(Unit) {
                    delay(300) // Short delay to ensure UI is ready
                    focusRequesters[0].requestFocus()
                    activeFieldIndex = 0
                }

                // Display toast-like message for password validation
                if (showPasswordMessage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isPasswordValid) "PASSWORD VALID" else "PASSWORD INVALID",
                            color = if (isPasswordValid) Color(0xFF4CAF50) else Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
                // Keep the original error message UI for backward compatibility but don't show it
                // when showPasswordMessage is true
                else if (isPasswordInvalid) {
                    Text(
                        "INCORRECT PASSWORD",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Clear password fields and refocus first field after showing error
                    LaunchedEffect(isPasswordInvalid) {
                        if (isPasswordInvalid) {
                            delay(1000) // Show error message for a moment
                            passwordDigits = List(6) { "" }
                            delay(300)
                            focusRequesters[0].requestFocus()
                            activeFieldIndex = 0
                            isPasswordInvalid = false // Hide error message when clearing fields
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Hint text
                Text(
                    "Enter the 6-digit maintenance code",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { verifyPassword() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .widthIn(max = 300.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "VERIFY PASSWORD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // Helper function to handle digit input and focus management
    private fun handleDigitChange(
        index: Int,
        newValue: String,
        currentDigits: List<String>,
        focusRequesters: List<FocusRequester>,
        updateDigits: (List<String>) -> Unit
    ) {
        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
            // Update the digit value
            val newDigits = currentDigits.toMutableList()
            newDigits[index] = newValue
            updateDigits(newDigits)

            // If a digit is entered and not the last field, focus next field
            if (newValue.isNotEmpty() && index < currentDigits.size - 1) {
                focusRequesters[index + 1].requestFocus()
            }
        }
    }

    @Composable
    private fun PasswordDigitField(
        digit: String,
        onValueChange: (String) -> Unit,
        focusRequester: FocusRequester,
        size: androidx.compose.ui.unit.Dp,
        padding: androidx.compose.ui.unit.Dp,
        isDesktop: Boolean,
        isActive: Boolean = false, // New parameter for active field
        onNext: () -> Unit = {} // New parameter for next action
    ) {
        val activeBorderColor = VendingMachineColors.AccentColor
        val touchTargetSize = if (isDesktop) size else size + 8.dp // Larger touch target for mobile

        OutlinedTextField(
            value = digit,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .size(touchTargetSize)
                .focusRequester(focusRequester)
                .padding(horizontal = padding)
                // Make the whole area clickable for better mobile experience
                .clickable(enabled = true, onClick = { focusRequester.requestFocus() })
                // Visual indication of active field with thicker border
                .then(if (isActive) Modifier.border(2.dp, activeBorderColor, RoundedCornerShape(8.dp)) else Modifier),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.2f),
                unfocusedContainerColor = VendingMachineColors.AccentColor.copy(alpha = 0.1f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedIndicatorColor = activeBorderColor,
                unfocusedIndicatorColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp)
        )
    }

    //----------------------------------------------------------------------------------------------
    // MAINTENANCE ACTIVE CARD
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun MaintenanceActiveCard(message: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 800.dp)
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.AccessGrantedColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "MAINTENANCE MODE ACTIVE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Door Unlocked - Access Granted",
                    fontSize = 16.sp,
                    color = Color.White
                )
                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        message,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
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
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 800.dp),
            colors = CardDefaults.cardColors(
                containerColor = VendingMachineColors.MachineBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
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
                "PRESS BELOW TO DETERMINE NUMBER OF COINS IN SELECTED DENOMINATION",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Radio buttons for denomination selection - handle narrow screens better
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidth = this.maxWidth
                val isNarrowScreen = maxWidth < 400.dp

                if (isNarrowScreen) {
                    // Stacked layout for very narrow screens (e.g. small phones)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // First row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CoinRadioButton(
                                text = "10c",
                                selected = selectedDenomination == 10,
                                onClick = { selectedDenomination = 10 }
                            )

                            CoinRadioButton(
                                text = "20c",
                                selected = selectedDenomination == 20,
                                onClick = { selectedDenomination = 20 }
                            )
                        }

                        // Second row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CoinRadioButton(
                                text = "50c",
                                selected = selectedDenomination == 50,
                                onClick = { selectedDenomination = 50 }
                            )

                            CoinRadioButton(
                                text = "RM1",
                                selected = selectedDenomination == 100,
                                onClick = { selectedDenomination = 100 }
                            )
                        }
                    }
                } else {
                    // Standard horizontal layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 10 Sen radio button
                        CoinRadioButton(
                            text = "10c",
                            selected = selectedDenomination == 10,
                            onClick = { selectedDenomination = 10 }
                        )

                        // 20 Sen radio button
                        CoinRadioButton(
                            text = "20c",
                            selected = selectedDenomination == 20,
                            onClick = { selectedDenomination = 20 }
                        )

                        // 50 Sen radio button
                        CoinRadioButton(
                            text = "50c",
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
                }
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

            // Add increment button for coin count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val currentCount = viewModel.coinsByDenomination[selectedDenomination] ?: 0
                        if (currentCount < 20) {
                            viewModel.updateCoinQuantity(selectedDenomination, currentCount + 1)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VendingMachineColors.ButtonColor,
                        disabledContainerColor = Color.Gray
                    ),
                    enabled = (viewModel.coinsByDenomination[selectedDenomination] ?: 0) < 20,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    Text("Add Coin", fontWeight = FontWeight.Medium)
                }
            }

            // Show max coin message if at limit
            if ((viewModel.coinsByDenomination[selectedDenomination] ?: 0) >= 20) {
                Text(
                    text = "Maximum coin limit reached (20)",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                .widthIn(min = 72.dp)
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
    private fun DrinkInventoryAndPriceSection(viewModel: MaintenanceViewModel, isWideScreen: Boolean) {
        // State for tracking selected brand
        var selectedBrand by remember { mutableStateOf("BRAND 1") } // Default to BRAND 1
        var newPrice by remember { mutableStateOf("") }

        // Get the current inventory count
        val count = viewModel.drinkStockLevels[selectedBrand] ?: 0

        SectionCard(title = "Drink Inventory & Price Management") {
            Text(
                "PRESS BELOW TO DETERMINE NUMBER OF CANS IN SELECTED DENOMINATION",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Using FlowRow to automatically wrap buttons to multiple lines on smaller screens
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                maxItemsInEachRow = if (isWideScreen) 5 else 3,
                horizontalArrangement = Arrangement.SpaceBetween,
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
                "TOTAL NUMBER OF CANS IN SELECTED DENOMINATION",
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Current price display
            val currentPrice = viewModel.drinkPriceSettings[selectedBrand] ?: 0.0
            Text(
                text = if (currentPrice < 1.0) {
                    // For prices less than 1 RM, display in cents (e.g., 50c)
                    "Current Price: ${(currentPrice * 100).toInt()}c"
                } else {
                    // For prices 1 RM and above, display in ringgit (e.g., RM 1.50)
                    "Current Price: RM ${formatToTwoDecimalPlaces(currentPrice)}"
                },
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
                label = { Text("TYPE NEW DRINKS PRICE HERE", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp),
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
                .widthIn(min = 80.dp)
        ) {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .widthIn(max = 300.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
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
                        .widthIn(max = 400.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VendingMachineColors.ButtonColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "PRESS HERE TO COLLECT ALL CASH",
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
                    .widthIn(max = 400.dp)
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
                .widthIn(max = 400.dp)
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
        var errorMessage by remember { mutableStateOf("") }

        if (showUpdateField) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                errorMessage = ""

                                // Check for maximum value of 20
                                value.toIntOrNull()?.let {
                                    if (it > 20) {
                                        errorMessage = "Maximum 20 cans allowed"
                                    }
                                }
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
                        singleLine = true,
                        isError = errorMessage.isNotEmpty()
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val quantity = newValue.toIntOrNull() ?: 0
                            if (quantity >= 0 && quantity <= 20 && onUpdateQuantity(quantity)) {
                                showUpdateField = false
                            } else if (quantity > 20) {
                                errorMessage = "Maximum 20 cans allowed"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VendingMachineColors.ButtonColor
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(4.dp),
                        enabled = errorMessage.isEmpty()
                    ) {
                        Text("Save", fontSize = 12.sp)
                    }
                }

                // Display error message if any
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .widthIn(max = 300.dp)
                ) {
                    Text("Update Drink Quantity", fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // POWER CUT SIMULATION OVERLAY
    //----------------------------------------------------------------------------------------------

    @Composable
    private fun PowerCutOverlay(isVisible: Boolean) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Power Outage",
                    color = Color.Red.copy(alpha = 0.7f),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
