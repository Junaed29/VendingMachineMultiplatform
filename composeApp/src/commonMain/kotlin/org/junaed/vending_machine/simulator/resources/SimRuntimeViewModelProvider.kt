package org.junaed.vending_machine.simulator.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import org.junaed.vending_machine.simulator.viewmodel.SimRuntimeViewModel

/**
 * LocalSimRuntimeViewModel - Composition local for providing the simulator view model
 * across the component hierarchy without explicit passing
 */
val LocalSimRuntimeViewModel = staticCompositionLocalOf<SimRuntimeViewModel> {
    error("No SimRuntimeViewModel provided")
}

/**
 * SimRuntimeViewModelProvider - Helper composable to provide the simulator view model
 * to all child composables in the hierarchy
 */
@Composable
fun SimRuntimeViewModelProvider(
    viewModel: SimRuntimeViewModel,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalSimRuntimeViewModel provides viewModel) {
        content()
    }
}
