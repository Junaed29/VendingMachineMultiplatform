package org.junaed.vending_machine.simulator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.screens.VendingMachineScreen
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * CustomerPanelScreen - Wrapper around the production VendingMachineScreen
 *
 * This screen reuses the existing customer interface and makes it reactive
 * to the simulation state.
 */
@Composable
fun CustomerPanelScreen(viewModel: SimRuntimeViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .background(Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = VendingMachineColors.MachineBackground
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                "Customer Panel",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // This box wraps the customer UI and applies disabled styling when not running
            Box(modifier = Modifier.fillMaxSize()) {
                // The actual production UI is here
                VendingMachineScreen().Content()

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
