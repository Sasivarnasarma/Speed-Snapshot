# Speed Snapshot

Speed Snapshot is a standard Android application designed to capture speed-related data using GPS.

## Tech Stack
- **Language:** Kotlin
- **UI:** XML Layouts (View Binding)
- **Build System:** Gradle Kotlin DSL (kts)
- **Minimum SDK:** 24
- **Target SDK:** 34

## Project Structure
- `app/`: Main application module.
  - `src/main/java/com/example/speedsnapshot/`:
    - `ui/`: UI components and Activities/Fragments.
    - `location/`: Location-related logic and services.
    - `permissions/`: Permission handling utilities.
    - `utils/`: Common utility classes.
  - `src/main/res/`: Resources like layouts, strings, and themes.
- `gradle/libs.versions.toml`: Version catalog for dependency management.

## Getting Started
### Prerequisites
- Android Studio Jellyfish or newer.
- JDK 17.

### Building the Project
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Build the project using `Build > Make Project`.

### Running the App
1. Connect an Android device or start an emulator.
2. Click the `Run` icon (green triangle) in Android Studio.
