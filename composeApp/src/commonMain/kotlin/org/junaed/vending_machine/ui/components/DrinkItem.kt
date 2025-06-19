package org.junaed.vending_machine.ui.components

/**
 * Represents a drink item in the vending machine
 * @param name The name of the drink
 * @param price The price of the drink as a string (e.g. "1.50")
 * @param inStock Whether the drink is in stock (true) or not (false)
 */
data class DrinkItem(val name: String, val price: String, val inStock: Boolean = false)
