# APKURL Security Scanner

Android app that scans URLs and APK files and shows a risk result in a clean dashboard UI.

## Features

- Scan suspicious URLs
- Scan APK files from file picker or share intent
- View risk score and risk level summary
- Save and review recent scan history
- Supports deep links and shared text/links from other apps

## Tech Stack

- Kotlin
- Android SDK (minSdk 26, targetSdk 36)
- Jetpack Compose + Material 3
- Navigation Compose
- Kotlin Coroutines
- Retrofit + OkHttp + Gson
- DataStore Preferences

## Project Structure

- `app/src/main/java/com/example/finalapkurl/presentation` - ViewModels
- `app/src/main/java/com/example/finalapkurl/data` - repositories and data flow
- `app/src/main/java/com/example/finalapkurl/data/remote` - VirusTotal API layer
- `app/src/main/java/com/example/finalapkurl/data/local` - local history persistence
- `app/src/main/java/com/example/finalapkurl/ui` - Compose screens, components, theme
- `app/src/main/java/com/example/finalapkurl/domain` - scoring and risk mapping logic

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- Android SDK 36
- JDK 11

### Run Locally

1. Clone this repository
2. Open the project in Android Studio
3. Sync Gradle
4. Run the `app` module on an emulator or Android device

## API Key Note

The current project uses a `BuildConfig` field for the VirusTotal API key in `app/build.gradle.kts`.

For production, avoid committing API keys and move secrets to a secure local or CI-managed source (for example, `local.properties` or environment variables).

## Testing

- Unit tests: `app/src/test`
- Instrumentation tests: `app/src/androidTest`

## Resume-Friendly Summary

Built an Android security scanning app in Kotlin using Jetpack Compose. Integrated VirusTotal for URL/APK analysis, implemented coroutine-based scanning workflows, and persisted scan history with DataStore.
