# Reflect App

A modern Android journaling application built with Kotlin and Jetpack Compose. Reflect helps you capture your daily thoughts, track your moods, and organize your experiences with an intuitive and beautiful interface.

## Features

### Core Functionality
- **Journal Entries**: Create and manage daily journal entries with titles and detailed content
- **Mood Tracking**: Record your emotional state with each entry
- **Categories**: Organize entries by topic or theme
- **Photo Attachments**: Add images from your gallery to enhance your entries
- **Favorites**: Mark important entries for quick access
- **Search & Filter**: Find entries quickly using search and filter by mood, category, or favorites

### Statistics & Insights
- **Total Entry Count**: Track your journaling consistency
- **Writing Streak**: Monitor consecutive days of journaling
- **Mood Distribution**: Visualize your emotional patterns over time
- **Category Breakdown**: See how you organize your thoughts

### User Experience
- **Dark Mode**: Toggle between light and dark themes
- **Modern UI**: Built with Material 3 design principles
- **Responsive Design**: Optimized for various screen sizes

## Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI framework
- **Material 3**: Latest Material Design components

### Architecture & Libraries
- **Room Database**: Local data persistence with SQLite
- **ViewModel & LiveData**: MVVM architecture for reactive UI
- **Coroutines & Flow**: Asynchronous programming
- **Coil**: Image loading and caching
- **KSP**: Kotlin Symbol Processing for Room

### Testing
- **JUnit**: Unit testing framework
- **Espresso**: UI testing
- **Room Testing**: Database testing utilities
- **Coroutines Test**: Testing asynchronous code

## Requirements

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 36)
- **Compile SDK**: Android 14 (API 36)
- **Java Version**: 11

## Project Structure

```
app/src/main/java/com/example/reflectapp/
├── data/
│   ├── local/           # Room database and DAOs
│   ├── model/           # Data models and entities
│   └── repository/      # Data layer abstraction
├── ui/
│   ├── components/      # Reusable UI components
│   ├── detail/          # Entry creation and editing screens
│   ├── home/            # Main screen with entry list
│   ├── settings/        # Statistics and settings screens
│   └── theme/           # Material 3 theme configuration
├── util/                # Utility classes
├── viewmodel/           # ViewModels for business logic
└── ReflectApplication.kt
```

## Building the Project

### Clone the Repository
```bash
git clone <repository-url>
cd ReflectApp
```

### Open in Android Studio
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the ReflectApp directory
4. Wait for Gradle sync to complete

### Build and Run
```bash
./gradlew assembleDebug
```

Or use Android Studio's Run button to build and install on a device/emulator.

## Permissions

The app requires the following permissions:
- **READ_MEDIA_IMAGES** (Android 13+): Access photos from gallery
- **READ_EXTERNAL_STORAGE** (Android 12 and below): Access photos from gallery

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Key Components

### Data Models
- `JournalEntry`: Main entity representing a journal entry
- `Mood`: Enum for mood states
- `Category`: Enum for entry categories

### Database
- `JournalDatabase`: Room database implementation
- `JournalDao`: Data Access Object for database operations

### ViewModels
- `JournalViewModel`: Manages home screen data and operations
- `DetailViewModel`: Handles entry creation and editing
- `StatsViewModel`: Computes statistics and insights

### Utilities
- `DateFormatter`: Formats timestamps for display
- `StreakCalculator`: Calculates consecutive writing days

## Version

**Current Version**: 1.0

## License

This project is a personal journaling application.
