package org.junaed.vending_machine.logic

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

/**
 * JVM/Desktop implementation of the SettingsFactory
 */
class DesktopSettingsFactory : SettingsFactory {
    private val preferences = Preferences.userRoot().node("org.junaed.vending_machine")

    override fun createSettings(): Settings = PreferencesSettings(preferences)
}

/**
 * Get desktop-specific settings implementation
 */
actual fun getSettingsFactory(): SettingsFactory = DesktopSettingsFactory()
