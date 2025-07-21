package org.junaed.vending_machine.utils

/**
 * Application configuration constants
 */
object AppConfig {
    // GitHub repository information
    const val GITHUB_REPO_OWNER = "Junaed29"
    const val GITHUB_REPO_NAME = "VendingMachineMultiplatform"

    // Current release version - update this when creating a new release
    const val CURRENT_RELEASE_TAG = "v1.0.7"

    // Base URL for GitHub repository
    const val GITHUB_REPO_BASE_URL = "https://github.com/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME"

    // URL for releases section
    const val GITHUB_RELEASES_URL = "$GITHUB_REPO_BASE_URL/releases"

    // URL for a specific release's downloads
    const val GITHUB_RELEASE_DOWNLOAD_URL = "$GITHUB_RELEASES_URL/download/$CURRENT_RELEASE_TAG"

    // Returns the download URL for an asset in the current release
    fun getReleaseDownloadUrl(assetName: String): String {
        return "$GITHUB_RELEASE_DOWNLOAD_URL/$assetName"
    }

    // Binary file names
    const val WINDOWS_ASSET_NAME = "DrinkBot-1.0.0.msi"
    const val MAC_ASSET_NAME = "DrinkBot-1.0.0.dmg"
    const val ANDROID_ASSET_NAME = "DrinkBot-Vending-Machine.apk"
}
