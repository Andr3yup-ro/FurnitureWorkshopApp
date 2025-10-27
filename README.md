# Atelier Manager (Android)

Bilingual (EN/RO) production management app for custom furniture businesses. Offline-first with Room, Jetpack Compose UI, Hilt DI, WorkManager, and Retrofit-ready networking.

## Modules
- app: Android app using Kotlin + Compose

## Requirements
- Android Studio Giraffe+ (or JDK 17 and Android SDK 35)

## Build (CI)
GitHub Actions workflow builds the project with Gradle on Ubuntu runners.

## Tech
- Jetpack Compose, Material3
- Room (local DB), DataStore (settings)
- Hilt (DI), WorkManager (background)
- Navigation Compose
- Retrofit (API-ready), Moshi
- Coil

## Internationalization
- English (default) and Romanian resources in `values-ro/`
- Language toggle stored via DataStore (manual toggle support pending UI)

## License
Proprietary
