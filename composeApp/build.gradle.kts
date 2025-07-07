import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    // Add kotlinx-serialization plugin for JSON serialization
    kotlin("plugin.serialization") version "2.2.0"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            // Set bundle ID for iOS
            linkerOpts.add("-Xbinary=bundleId=org.junaed.vending_machine.ios")
        }
    }
    
    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val desktopMain by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        // Create a common iOS source set
        val iosMain by creating {
            dependsOn(commonMain.get())
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(compose.material)
        }

        commonMain.dependencies {
            // Update multiplatform-settings dependencies to latest version
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.russhwolf:multiplatform-settings-serialization:1.3.0")
            // Update serialization dependency
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

            // Add kotlinx-datetime dependency
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material) // Adding material for all platforms
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.material.icons.extended)

            //voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.bottomSheet)
            implementation(libs.voyager.transitions)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(compose.material)
        }

        iosMain.dependencies {
            implementation(compose.material) // Explicitly adding material for iOS
        }
    }
}

android {
    namespace = "org.junaed.vending_machine"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.junaed.vending_machine"
        minSdk = 24
        targetSdk = 35  // Using Android 14 as target while testing Android 15 compatibility
        versionCode = 2
        versionName = "1.0.5"
    }

    signingConfigs {
        create("release") {
            // Check if we're building in CI (GitHub Actions)
            if (System.getenv("CI") == "true") {
                // Use the keystore file created in the CI/CD workflow
                storeFile = file("../android-keystore.jks")
                // Using exactly the same environment variable names as in GitHub workflow
                storePassword = System.getenv("KEY_STORE_PASSWORD") ?: "VendingMachineMultiplatform"
                keyAlias = System.getenv("KEY_ALIAS") ?: "myalias"
                keyPassword = System.getenv("KEY_PASSWORD") ?: "VendingMachineMultiplatform"
            } else {
                // For local development, use your existing keystore
                storeFile = file("../my-release-key.keystore")
                storePassword = project.findProperty("STORE_PASSWORD") as String? ?: System.getenv("KEY_STORE_PASSWORD") ?: ""
                keyAlias = "myalias" // This is your actual key alias from the keystore
                keyPassword = project.findProperty("KEY_PASSWORD") as String? ?: System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.junaed.vending_machine.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DrinkBot"
            packageVersion = "1.0.0"

            macOS {
                // Set a user-friendly name that will appear in Finder and Applications folder
                bundleID = "org.junaed.vending_machine"
                appCategory = "public.app-category.utilities"
                iconFile.set(project.file("src/desktopMain/resources/icons/DrinkBot.icns"))
                // Adding more specific icon configuration in Info.plist
                infoPlist {
                    extraKeysRawXml = """
                        <key>CFBundleIconFile</key>
                        <string>DrinkBot</string>
                    """
                }
                // Add package name to make it appear correctly in Finder
                packageBuildVersion = "1.0.0"
                dockName = "DrinkBot"
            }

            windows {
                iconFile.set(project.file("src/desktopMain/resources/icons/DrinkBot.ico"))
                // Friendly application name for Windows
                menuGroup = "DrinkBot"
            }

            linux {
                iconFile.set(project.file("src/commonMain/resources/icons/DrinkBot.png"))
            }
        }
    }
}
