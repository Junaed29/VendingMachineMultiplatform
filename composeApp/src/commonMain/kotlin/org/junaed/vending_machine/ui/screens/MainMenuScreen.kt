package org.junaed.vending_machine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.junaed.vending_machine.ui.screens.simulator.screens.OverallControlScreen
import org.junaed.vending_machine.ui.theme.VendingMachineColors
import org.junaed.vending_machine.ui.utils.isWebPlatform
import org.junaed.vending_machine.ui.utils.openUrlInBrowser

/**
 * Main Menu Screen for the Vending Machine app
 * This is the entry point of the application that provides navigation to other screens
 */
class MainMenuScreen: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "DrinkBot Vending Machine",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VendingMachineColors.MachinePanelColor
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Get the navigator instance to handle navigation between screens
            val navigator = LocalNavigator.current

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
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App logo or title
                    Text(
                        "DrinkBot",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp,
                        color = VendingMachineColors.AccentColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "Soft Drinks Dispenser",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Vending Machine screen
                    MainMenuButton(
                        text = "Vending Machine",
                        onClick = { navigator?.push(VendingMachineScreen()) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Maintenance screen
                    MainMenuButton(
                        text = "Maintenance",
                        onClick = { navigator?.push(MaintenanceScreen()) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to navigate to the Simulator screen
                    MainMenuButton(
                        text = "Simulator",
                        onClick = { navigator?.push(OverallControlScreen()) }
                    )

                    // Download section
                    DownloadSection()

                    Spacer(modifier = Modifier.height(48.dp))

                    // Copyright information
                    Text(
                        "© 2025 DrinkBot Soft Drinks Ltd",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun MainMenuButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VendingMachineColors.ButtonColor
            )
        ) {
            Text(
                text,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    /**
     * DownloadSection - Displays download buttons for different platforms
     * Only visible when running on the web platform
     */
    @Composable
    private fun DownloadSection() {
        // Only show download section when running on web platform
        if (isWebPlatform()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VendingMachineColors.MachinePanelColor.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Download for Other Platforms",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        color = Color.White.copy(alpha = 0.3f)
                    )

                    // Horizontal row of download buttons
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Download button for Windows
                        DownloadButton(
                            text = "Windows",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                openUrlInBrowser("https://github.com/vukan-markovic/Github-Android-Action/archive/refs/tags/1.2.zip")
                            }
                        )

                        // Download button for Mac
                        DownloadButton(
                            text = "Mac",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                openUrlInBrowser("https://github.com/vukan-markovic/Github-Android-Action/archive/refs/tags/1.2.zip")
                            }
                        )

                        // Download button for Android APK
                        DownloadButton(
                            text = "Android APK",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                openUrlInBrowser("https://github.com/vukan-markovic/Github-Android-Action/archive/refs/tags/1.2.zip")
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * DownloadButton - A styled button specifically for download actions
     */
    @Composable
    private fun DownloadButton(
        text: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3498DB), // Blue color for download buttons
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 4.dp
            )
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Download icon could be added here if desired
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
