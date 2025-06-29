package org.junaed.vending_machine.ui.screens.simulator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.ui.screens.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * DummyPanelScreen - A placeholder screen to use instead of real panel screens
 *
 * This screen avoids using the actual functional panels that might affect database state.
 * Used for testing and simulation purposes only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DummyPanelScreen(
    title: String,
    message: String,
    viewModel: SimRuntimeViewModel,
    onClose: () -> Unit = {} // Add onClose callback parameter with default value
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = VendingMachineColors.MachineBackground
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            TopAppBar(
                title = {
                    Text(
                        title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VendingMachineColors.MachinePanelColor
                ),
                actions = {
                    // Add close button (X) to the top bar
                    IconButton(
                        onClick = { onClose() } // Trigger onClose when clicked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                Text(
                    message,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Display simulation status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VendingMachineColors.MachinePanelColor.copy(alpha = 0.7f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Simulation Running: ${if (viewModel.isRunning) "Yes" else "No"}",
                    fontSize = 14.sp,
                    color = VendingMachineColors.DisplayColor
                )
            }
        }
    }
}
