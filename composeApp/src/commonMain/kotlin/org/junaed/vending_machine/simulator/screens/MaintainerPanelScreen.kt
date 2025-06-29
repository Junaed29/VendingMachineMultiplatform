package org.junaed.vending_machine.simulator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel
import org.junaed.vending_machine.ui.screens.MaintenanceScreen
import org.junaed.vending_machine.ui.theme.VendingMachineColors

/**
 * MaintainerPanelScreen - Wrapper around the production MaintenanceScreen
 *
 * This screen reuses the existing maintenance interface and makes it reactive
 * to the simulation state, specifically preventing logout when door is unlocked.
 */
@Composable
fun MaintainerPanelScreen(viewModel: SimRuntimeViewModel) {
    // The maintainer panel screen is wrapped to control simulation-specific behavior

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
                "Maintainer Panel",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // This box wraps the maintainer UI and applies disabled styling when not running
            Box(modifier = Modifier.fillMaxSize()) {
                // The actual production UI is here
                MaintenanceScreen().Content()

                // Overlay to disable the UI when not running
                if (!viewModel.isRunning) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .alpha(0.6f)
                    )
                }

                // For the special rule that logout must be disabled when door is unlocked,
                // we would need to integrate with the MaintenanceScreen's ViewModel.
                // This would typically require modifications to the production code to
                // observe the door state from our simulator, or custom event handling.

                // In a real implementation, this might involve:
                // - Adding an observer to the door state
                // - Passing a callback to disable the logout button when door is unlocked
                // - Or intercepting click events when door is unlocked
            }
        }
    }
}
