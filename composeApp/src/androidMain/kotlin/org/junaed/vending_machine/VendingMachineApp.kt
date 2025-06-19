package org.junaed.vending_machine

import android.app.Application
import org.junaed.vending_machine.logic.initializeSettingsFactory

/**
 * Android Application class for VendingMachine
 */
class VendingMachineApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the settings factory with application context
        initializeSettingsFactory(applicationContext)
    }
}
