package org.junaed.vending_machine.logic

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of the SettingsFactory
 */
class IOSSettingsFactory : SettingsFactory {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun createSettings(): Settings = NSUserDefaultsSettings(userDefaults)
}

/**
 * Get iOS-specific settings implementation
 */
actual fun getSettingsFactory(): SettingsFactory = IOSSettingsFactory()
