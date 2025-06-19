package org.junaed.vending_machine

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform