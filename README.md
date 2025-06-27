# VIMTO Vending Machine Multiplatform

A comprehensive Kotlin Multiplatform application that simulates a vending machine system across Android, iOS, and Desktop platforms. The application simulates the operations of a soft drinks dispenser with full purchasing, maintenance, and simulation capabilities.

## Project Overview

This vending machine application allows users to:
- Purchase drinks by inserting virtual coins
- Validate coins based on Malaysian specifications
- Perform maintenance operations on the vending machine
- Simulate vending machine operations
- Track and view transaction history

## Technology Stack

- **Kotlin Multiplatform**: Shared codebase for Android, iOS, and Desktop
- **Jetpack Compose**: Modern declarative UI toolkit for all platforms
- **Voyager**: Navigation library for Compose Multiplatform
- **Kotlinx Serialization**: Cross-platform JSON serialization
- **Multiplatform Settings**: Cross-platform key-value storage
- **Kotlinx DateTime**: Cross-platform date and time utilities

## Project Structure

### `/composeApp` Directory

Contains the shared code for all platforms with the following key directories:

#### `/src/commonMain`

Shared code across all platforms:

- **Model Package** (`org.junaed.vending_machine.model`):
  - `Coin.kt` - Data model for coins with physical properties (diameter, thickness, weight)
  - `DrinkItem.kt` - Data model for drinks available in the vending machine
  - `Transaction.kt` - Data model for tracking vending machine transactions
  - `MaintenanceSettings.kt` - Settings for the vending machine maintenance

- **ViewModel Package** (`org.junaed.vending_machine.viewmodel`):
  - `VendingMachineViewModel.kt` - Manages state and business logic for the vending machine operations
  - `MaintenanceViewModel.kt` - Manages maintenance operations and settings

- **Logic Package** (`org.junaed.vending_machine.logic`):
  - `CoinRepository.kt` - Handles coin validation and identification based on physical properties
  - `VendingMachineService.kt` - Core business logic for vending machine operations
  - `StorageService.kt` - Persistent storage for transactions and settings
  - `SettingsFactory.kt` - Platform-specific settings implementation

- **UI Package** (`org.junaed.vending_machine.ui`):
  - **Screens** (`screens` subpackage):
    - `MainMenuScreen.kt` - Entry point with navigation to other screens
    - `VendingMachineScreen.kt` - Main interface for purchasing drinks
    - `MaintenanceScreen.kt` - Interface for maintenance operations
    - `SimulatorScreen.kt` - Simulates vending machine operations
  
  - **Components** (`components` subpackage):
    - `CoinButton.kt` - UI component for coin insertion
    - `DrinkSelectionButton.kt` - UI component for selecting drinks

  - **Theme** (`theme` subpackage):
    - `VendingMachineTheme.kt` - Theme and styling for the application

- **App.kt** - Root composable that sets up the application's theme and navigation

#### Platform-Specific Directories

- `/src/androidMain` - Android-specific implementations
- `/src/iosMain` - iOS-specific implementations
- `/src/desktopMain` - Desktop-specific implementations

### `/iosApp` Directory

Contains iOS application entry point and configuration:
- `iosApp.swift` - Swift entry point for the iOS application
- `ContentView.swift` - SwiftUI wrapper for Compose UI

## Core Features

### Vending Machine Operations
- Coin insertion and validation based on Malaysian specifications
- Drink selection with inventory management
- Change calculation and dispensing
- Transaction recording

### Maintenance Mode
- Inventory management (add/remove drinks)
- Price configuration
- Available change management
- Transaction history viewing

### Simulator Mode
- Simulates real-world vending machine usage scenarios
- Testing different operational conditions

## Building and Running

### Android
```
./gradlew :composeApp:assembleDebug
```

### iOS
Open the Xcode project in the iosApp directory and run from there, or use:
```
./gradlew :composeApp:iosDeployIphoneDebug
```

### Desktop
```
./gradlew :composeApp:run
```

## Development and Contributions

This project demonstrates how to build a multiplatform application with shared business logic and UI using Kotlin and Compose. The architecture follows MVVM (Model-View-ViewModel) pattern with clear separation of concerns.

## License

© 2025 VIMTO Soft Drinks Ltd
