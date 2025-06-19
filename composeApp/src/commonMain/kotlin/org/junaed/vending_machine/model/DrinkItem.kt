package org.junaed.vending_machine.model

/**
 * Data model representing a drink in the vending machine
 */
data class DrinkItem(
    val name: String,
    val price: String,
    val inStock: Boolean = false
)
