package org.junaed.vending_machine.logic

import com.russhwolf.settings.Settings


/**
 * A factory interface for creating platform-specific Settings implementations
 */
interface SettingsFactory {
    fun createSettings(): Settings
}

/**
 * Get platform-specific settings implementation
 */
expect fun getSettingsFactory(): SettingsFactory
