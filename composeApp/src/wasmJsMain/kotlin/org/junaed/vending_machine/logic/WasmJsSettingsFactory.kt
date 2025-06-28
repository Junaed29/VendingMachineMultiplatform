package org.junaed.vending_machine.logic

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

/**
 * WASM implementation of the SettingsFactory that uses browser's localStorage
 */
class WasmJsSettingsFactory : SettingsFactory {
    override fun createSettings(): Settings = StorageSettings()
}

/**
 * Get WASM-specific settings implementation
 */
actual fun getSettingsFactory(): SettingsFactory = WasmJsSettingsFactory()

