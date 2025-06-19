package org.junaed.vending_machine.logic

import android.content.Context
import android.content.SharedPreferences
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.junaed.vending_machine.App

/**
 * Android implementation of the SettingsFactory
 */
class AndroidSettingsFactory(private val context: Context) : SettingsFactory {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "vending_machine_preferences", Context.MODE_PRIVATE
    )

    override fun createSettings(): Settings = SharedPreferencesSettings(sharedPreferences)
}

// Global variable to store the application context
private var appContext: Context? = null

/**
 * Initialize the settings factory with a context
 * Call this from your Application class or MainActivity
 */
fun initializeSettingsFactory(context: Context) {
    appContext = context.applicationContext
}

/**
 * Get Android-specific settings implementation
 */
actual fun getSettingsFactory(): SettingsFactory {
    // Use the stored appContext, or throw an exception if it's not initialized
    val context = appContext ?: throw IllegalStateException(
        "Settings not initialized. Call initializeSettingsFactory() first."
    )
    return AndroidSettingsFactory(context)
}
