# VIMTO Vending Machine Multiplatform

A comprehensive Kotlin Multiplatform application that simulates a vending machine system across Android, iOS, Desktop, and Web platforms. The application simulates the operations of a soft drinks dispenser with full purchasing, maintenance, and simulation capabilities.

## Project Overview

This vending machine application allows users to:
- Purchase drinks by inserting virtual coins
- Validate coins based on Malaysian specifications
- Perform maintenance operations on the vending machine
- Simulate vending machine operations
- Track and view transaction history

## Technology Stack

- **Kotlin Multiplatform**: Shared codebase for Android, iOS, Desktop, and Web
- **Jetpack Compose**: Modern declarative UI toolkit for all platforms
- **Voyager**: Navigation library for Compose Multiplatform
- **Kotlinx Serialization**: Cross-platform JSON serialization
- **Multiplatform Settings**: Cross-platform key-value storage
- **Kotlinx DateTime**: Cross-platform date and time utilities

## Project Structure

```
VendingMachineMultiplatform/
├── build.gradle.kts                // Root build configuration
├── gradle.properties               // Gradle and project properties
├── settings.gradle.kts             // Project settings and module config
├── composeApp/                     // Main multiplatform module
│   ├── build.gradle.kts            // Module build configuration
│   └── src/                        // Source code directory
│       ├── commonMain/             // Shared code for all platforms
│       │   ├── kotlin/org/junaed/vending_machine/
│       │   │   ├── model/          // Data models
│       │   │   ├── viewmodel/      // ViewModels for UI state/logic
│       │   │   ├── logic/          // Business logic
│       │   │   └── ui/             // UI components
│       │   │       ├── components/ // Reusable UI components
│       │   │       ├── screens/    // Full application screens
│       │   │       ├── theme/      // Theme definitions
│       │   │       └── utils/      // UI utility classes
│       ├── androidMain/            // Android-specific code
│       ├── iosMain/                // iOS-specific code
│       ├── desktopMain/            // Desktop-specific code
│       └── wasmJsMain/             // Web/WASM-specific code
├── iosApp/                         // iOS application wrapper
│   ├── Configuration/              // iOS configuration
│   └── iosApp/                     // iOS app entry point
└── gradle/                         // Gradle configuration
    └── libs.versions.toml          // Dependencies version catalog
```

## Component Responsibilities

### Models (`model/`)
- `Coin.kt`: Data model for coins with physical properties (diameter, thickness, weight)
- `DrinkItem.kt`: Data model for drinks available in the vending machine
- `Transaction.kt`: Data model for tracking vending machine transactions
- `MaintenanceSettings.kt`: Settings for the vending machine maintenance

### ViewModels (`viewmodel/`)
- `VendingMachineViewModel.kt`: Manages state and business logic for vending machine operations
- `MaintenanceViewModel.kt`: Handles maintenance operations and settings

### Business Logic (`logic/`)
- `CoinRepository.kt`: Handles coin validation based on physical properties
- `SettingsFactory.kt`: Platform-specific settings implementations
- Platform-specific settings factories:
  - `AndroidSettingsFactory.kt`
  - `IOSSettingsFactory.kt`
  - `DesktopSettingsFactory.kt`
  - `WasmJsSettingsFactory.kt`

### UI Components (`ui/`)
- **Screens**:
  - `MainMenuScreen.kt`: Entry point with navigation to other screens
  - `VendingMachineScreen.kt`: Main interface for purchasing drinks
  - `MaintenanceScreen.kt`: Interface for maintenance operations
  - `SimulatorScreen.kt`: Simulates vending machine operations
- **Reusable Components**:
  - `CoinButton.kt`: UI component for coin insertion
  - `DrinkSelectionButton.kt`: UI component for selecting drinks
- **Theme**:
  - `VendingMachineTheme.kt`: Theme and styling for the application

### Platform Entry Points
- `App.kt`: Root composable with navigation setup
- Android: `MainActivity.kt` and `VendingMachineApp.kt`
- iOS: Integration through `iosApp/iosApp/iOSApp.swift`
- Desktop: `desktopMain/main.kt`
- Web/WASM: `wasmJsMain/main.kt`

## How to Run/Build

### Android
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug

# Run with Android Studio
# Open project and run 'composeApp' configuration
```

### iOS
```bash
# Generate Xcode project
./gradlew :composeApp:podInstall

# Open the project in Xcode
open iosApp/iosApp.xcodeproj

# Build and run from Xcode on simulator or device
```

### Desktop
```bash
# Run the desktop application
./gradlew :composeApp:run

# Package for distribution
./gradlew :composeApp:packageDistributionForCurrentOS
```

### Web/WASM
```bash
# Run development server
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Build for production
./gradlew :composeApp:wasmJsBrowserProductionWebpack
```

## Special Setup Notes

1. **Environment Requirements**:
   - JDK 11 or newer
   - Android SDK installed and configured
   - For iOS builds: macOS with Xcode 14+ and CocoaPods
   - For web builds: Node.js and npm

2. **First-time Setup**:
   ```bash
   # Initialize the project
   ./gradlew build
   ```

3. **Generated Files**:
   - `local.properties`: Automatically created, contains path to Android SDK
   - `build/` directory: Contains compiled code and resources
   - `kotlin-js-store/`: Contains JavaScript-related dependencies
