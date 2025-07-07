# DrinkBot Vending Machine Multiplatform

[![Web App](https://img.shields.io/badge/Web_App-Live-4CC2FF?style=for-the-badge&logo=web)](https://junaed29.github.io/VendingMachineMultiplatform/) 
[![GitHub Release](https://img.shields.io/github/v/release/Junaed29/VendingMachineMultiplatform?style=for-the-badge)](https://github.com/Junaed29/VendingMachineMultiplatform/releases/latest)
[![CI/CD Status](https://img.shields.io/github/actions/workflow/status/Junaed29/VendingMachineMultiplatform/ci-cd.yml?branch=main&style=for-the-badge&label=CI%2FCD)](https://github.com/Junaed29/VendingMachineMultiplatform/actions/workflows/ci-cd.yml)
[![License](https://img.shields.io/github/license/Junaed29/VendingMachineMultiplatform?style=for-the-badge)](LICENSE)

A comprehensive **Kotlin Multiplatform** application that simulates a vending machine system across Android, iOS, Desktop, and Web platforms using **Compose Multiplatform**. Featuring coin validation physics, drink selection, maintenance controls, and transaction history—all from a single codebase with 90%+ shared code.

<div align="center">
  <!-- Screenshots can be added here in the future
  <p>
    <img src="screenshots/android_main.jpg" width="200" alt="Android Screenshot" />
    <img src="screenshots/ios_main.jpg" width="200" alt="iOS Screenshot" />
    <img src="screenshots/desktop_main.jpg" width="320" alt="Desktop Screenshot" />
    <img src="screenshots/web_main.jpg" width="400" alt="Web Screenshot" />
  </p>
  -->
  
  <p>
    <a href="https://junaed29.github.io/VendingMachineMultiplatform/">
      <img src="https://img.shields.io/badge/Try_Web_App-4CC2FF?style=for-the-badge&logo=web&logoColor=white" alt="Try Web App" />
    </a>
    <a href="https://github.com/Junaed29/VendingMachineMultiplatform/releases/latest">
      <img src="https://img.shields.io/badge/Download_Apps-00C853?style=for-the-badge&logo=android&logoColor=white" alt="Download Apps" />
    </a>
    <a href="#-features">
      <img src="https://img.shields.io/badge/View_Features-FF5722?style=for-the-badge&logo=readme&logoColor=white" alt="View Features" />
    </a>
  </p>
</div>

## 📑 Table of Contents

- [Features](#-features)
- [Key Highlights](#-key-highlights)
- [Technology Stack](#-technology-stack)
- [Supported Platforms](#-supported-platforms)
- [Project Architecture](#-project-architecture)
- [Getting Started](#-getting-started)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

## ✨ Features

This interactive vending machine application provides:

- 🥤 **Virtual Drink Purchasing** - Select and buy drinks using virtual coins
- 💰 **Realistic Coin Validation** - Based on Malaysian coin specifications
- 🔧 **Maintenance Mode** - Stock refill, cash collection, and price adjustment (Password: 123456)
- 🔍 **Simulation** - Test various vending machine operations and scenarios
- 📊 **Transaction History** - Track and analyze purchase records
- 🌐 **Cross-Platform** - Single codebase for Android, iOS, Desktop, and Web
- 🎨 **Modern UI** - Built with Jetpack Compose/Compose Multiplatform

## 🏆 Key Highlights

- **Truly Write Once, Run Anywhere** - Over 90% shared code between platforms
- **Realistic Physics Simulation** - Accurately validates coins based on physical properties (weight, diameter, thickness)
- **Adaptive UI** - Responsive design that works across mobile, tablet, desktop, and web interfaces
- **Modern Development** - Utilizes Kotlin's latest features including coroutines, flow, and serialization
- **Automated CI/CD** - Complete pipeline from commit to release with automated testing and deployment

## 🚀 Technology Stack

- **Kotlin Multiplatform** - Share code across all platforms
- **Compose Multiplatform** - Modern declarative UI for all platforms
- **Voyager** - Navigation library for Compose applications
- **Kotlinx Serialization** - Type-safe JSON serialization
- **Multiplatform Settings** - Key-value storage for all platforms
- **Kotlinx DateTime** - Cross-platform date and time utilities
- **Ktor** - Networking library for API calls

## 📱 Supported Platforms

| Platform | Status | Download |
|----------|--------|----------|
| Android  | ✅     | [APK](https://github.com/Junaed29/VendingMachineMultiplatform/releases/latest/download/DrinkBot-Vending-Machine.apk) |
| iOS      | ✅     | Build from source |
| Windows  | ✅     | [MSI](https://github.com/Junaed29/VendingMachineMultiplatform/releases/latest/download/DrinkBot-1.0.0.msi) |
| macOS    | ✅     | [DMG](https://github.com/Junaed29/VendingMachineMultiplatform/releases/latest/download/DrinkBot-1.0.0.dmg) |
| Web      | ✅     | [Web App](https://junaed29.github.io/VendingMachineMultiplatform/) |

## 🧩 Project Architecture

This project follows a clean architecture approach with clear separation of concerns:

```
VendingMachineMultiplatform/
├── composeApp/                     // Main multiplatform module
│   └── src/                       
│       ├── commonMain/             // Shared code for all platforms
│       │   └── kotlin/org/junaed/vending_machine/
│       │       ├── model/          // Domain models and data structures
│       │       ├── viewmodel/      // UI state management and business logic
│       │       ├── logic/          // Core business logic
│       │       └── ui/             // UI components
│       │           ├── components/ // Reusable UI elements
│       │           ├── screens/    // Application screens
│       │           ├── theme/      // Styling and theme definitions
│       │           └── utils/      // UI utility functions
│       ├── androidMain/            // Android-specific implementations
│       ├── iosMain/                // iOS-specific implementations
│       ├── desktopMain/            // Desktop (JVM) implementations
│       └── wasmJsMain/             // Web/WASM implementations
```

### Component Design

- **Models**: Data structures representing domain entities
- **ViewModels**: State holders that implement business logic and UI state
- **Logic**: Platform-independent business rules
- **UI**: Compose UI components for all platforms

## 🛠️ Getting Started

### Prerequisites

- **JDK 11+** (17+ recommended)
- **Android Studio Arctic Fox+** or **IntelliJ IDEA**
- **Android SDK** (for Android builds)
- **Xcode 14+** and **CocoaPods** (for iOS builds, macOS only)
- **Node.js** and **npm** (for web builds)

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Junaed29/VendingMachineMultiplatform.git
   cd VendingMachineMultiplatform
   ```

2. Open the project in Android Studio or IntelliJ IDEA

3. Create `local.properties` with your SDK path if it doesn't exist:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   ```

### Running the Project

#### Android
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on connected device
./gradlew :composeApp:installDebug

# Run directly with Android Studio
```

#### iOS
```bash
# Generate Xcode project
./gradlew :composeApp:podInstall

# Open the project in Xcode
open iosApp/iosApp.xcodeproj

# Build and run from Xcode
```

#### Desktop
```bash
# Run the desktop application
./gradlew :composeApp:run

# Package for distribution
./gradlew :composeApp:packageDistributionForCurrentOS
```

#### Web/WASM
```bash
# Run development server
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Build for production
./gradlew :composeApp:wasmJsBrowserProductionWebpack
```

## 🔄 CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment:

- **Automated Builds**: Every push to main branch triggers builds for all platforms
- **GitHub Pages**: Web version automatically deployed to GitHub Pages
- **Release Management**: Tags trigger automatic builds and release creation
- **Release Notes**: Automatic generation of release notes from commit messages

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📬 Contact

Junaed - [@Junaed29](https://github.com/Junaed29)

Project Link: [https://github.com/Junaed29/VendingMachineMultiplatform](https://github.com/Junaed29/VendingMachineMultiplatform)

---

<p align="center">
  Built with ❤️ using <a href="https://kotlinlang.org/docs/multiplatform.html">Kotlin Multiplatform</a> and 
  <a href="https://github.com/JetBrains/compose-multiplatform">Compose Multiplatform</a>
</p>
