package org.junaed.vending_machine.logic

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * A service for handling persistent storage using multiplatform-settings
 * This provides type-safe access to settings with JSON serialization support
 */
class StorageService(private val settings: Settings) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Save an object to storage, serializing it to JSON
     * @param key The key to store the object under
     * @param value The object to store
     * @param serializer The serializer for the object type
     */
    fun <T> saveObject(key: String, value: T, serializer: KSerializer<T>) {
        val jsonString = json.encodeToString(serializer, value)
        settings[key] = jsonString
    }

    /**
     * Retrieve an object from storage, deserializing it from JSON
     * @param key The key the object is stored under
     * @param serializer The serializer for the object type
     * @param defaultValue Optional default value if the key doesn't exist
     * @return The deserialized object or the default value
     */
    fun <T> getObject(key: String, serializer: KSerializer<T>, defaultValue: T? = null): T? {
        val jsonString = settings.getStringOrNull(key) ?: return defaultValue
        return try {
            json.decodeFromString(serializer, jsonString)
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * Save a simple value (String, Int, Long, Float, Double, Boolean)
     */
    fun saveValue(key: String, value: Any) {
        when(value) {
            is String -> settings[key] = value
            is Int -> settings[key] = value
            is Long -> settings[key] = value
            is Float -> settings[key] = value
            is Double -> settings[key] = value
            is Boolean -> settings[key] = value
            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }

    /**
     * Get a String value
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return settings.getStringOrNull(key) ?: defaultValue
    }

    /**
     * Get an Int value
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return settings.getIntOrNull(key) ?: defaultValue
    }

    /**
     * Get a Boolean value
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return settings.getBooleanOrNull(key) ?: defaultValue
    }

    /**
     * Check if a key exists in storage
     */
    fun hasKey(key: String): Boolean {
        return settings.hasKey(key)
    }

    /**
     * Remove a key from storage
     */
    fun remove(key: String) {
        settings.remove(key)
    }

    /**
     * Clear all stored data
     */
    fun clear() {
        settings.clear()
    }
}
