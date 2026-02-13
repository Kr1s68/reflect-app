# "Reflect" — General Life Journal App

## Context

This is the implementation plan for **Task 3** of the DLBCSEMSE02 (Mobile Software Engineering II) course assignment. The task requires developing a journal app that lets users add, update, view, and delete entries, with an overview list and detailed views. The app must use several Android Activities, include unit tests, follow Material Design, and be hosted on GitHub.

**Chosen theme:** General Life Journal — a flexible digital diary for thoughts, experiences, and memories.
**Tech stack:** Kotlin + Jetpack Compose + Room Database, following MVVM architecture.

---

## Requirements

These are the **mandatory acceptance criteria** from the assignment that must all be fulfilled:

| # | Requirement | Status |
|---|---|---|
| R1 | The app lets users **add, update, view, and delete** journal entries (full CRUD) | Planned |
| R2 | The app provides an **overview of all journal entries** a user has added | Planned |
| R3 | The app provides a **detailed view** of every entry | Planned |
| R4 | The app implements **several Android Activities** (minimum 3) | Planned |
| R5 | The app is **tested using unit tests** | Planned |
| R6 | The app follows **Material Design** and Android app quality guidelines | Planned |
| R7 | **Source code documentation** is appropriate (KDoc comments) | Planned |
| R8 | All code uploaded to a **GitHub repository** with link in project report | Planned |

### Report Requirements

| # | Requirement |
|---|---|
| D1 | Wireframes for initial app design |
| D2 | Software design overview (components and how they interact) |
| D3 | Relevant source code extracts with detailed explanations |
| D4 | Critical evaluation of whether the app fulfils targeted functionality |
| D5 | Future improvement suggestions |
| D6 | Lessons learned from the project |

### Evaluation Criteria

- **Transfer** — Demonstrate knowledge of Android concepts, design patterns, libraries
- **Documentation** — Clear description of project idea, dev process, artifacts
- **Resources** — Reference technical literature, tutorials, forums used
- **Process** — Document design/implementation decisions, challenges, solutions
- **Creativity** — Originality, effort in making the app fun and easy to use
- **Quality** — Acceptance criteria met, standard design guidelines followed, unit tests

---

## App Concept

**Reflect** is a General Life Journal where users can capture thoughts, experiences, and everyday moments. Beyond the minimum CRUD requirements, it includes creative features to score well on the Creativity criterion:

1. **Mood Tracking** — Each entry has a mood (emoji-based: Great, Good, Okay, Bad, Terrible)
2. **Entry Categories** — Predefined tags: Personal, Work, Travel, Health, Gratitude, Ideas, Other
3. **Photo Attachment** — One photo per entry from camera or gallery
4. **Search & Filter** — Search entries by title/content, filter by mood or category
5. **Favorites** — Bookmark important entries
6. **Statistics Dashboard** — Total entries, mood distribution, category breakdown, writing streak

### Journal Entry Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | Long (auto) | Auto | Primary key |
| `title` | String | Yes | Short title (max 100 chars) |
| `content` | String | Yes | Main body text |
| `dateCreated` | Long | Auto | Creation timestamp |
| `dateModified` | Long | Auto | Last modified timestamp |
| `mood` | Int (enum ordinal) | No | Mood: GREAT/GOOD/OKAY/BAD/TERRIBLE |
| `category` | String (enum name) | No | Category tag |
| `photoUri` | String? | No | URI of attached photo |
| `isFavorite` | Boolean | No | Bookmarked flag |

---

## Architecture — MVVM

```
VIEW LAYER (Activities + Compose Screens)
    |
    | observes StateFlow, calls ViewModel methods
    v
VIEWMODEL LAYER (JournalViewModel, DetailViewModel, StatsViewModel)
    |
    | calls Repository methods
    v
REPOSITORY LAYER (JournalRepository)
    |
    | accesses DAO
    v
DATA LAYER (Room Database -> JournalDao -> JournalEntry entity)
```

**Key design decisions:**
- **Repository pattern** — abstracts data source, enables testing with fakes
- **StateFlow over LiveData** — more idiomatic Kotlin, better Compose integration
- **Manual DI via Application class** — simple enough for a university project, avoids Hilt complexity
- **Enums for Mood/Category** — type-safe, stored as Int/String in Room

---

## Screens & Activities

### Activity 1: `MainActivity` — Journal Home (satisfies R2)
- **HomeScreen**: LazyColumn of entry cards, each showing mood emoji, title, date, content preview, category chip
- **SearchBar**: Search entries by title/content
- **FilterBottomSheet**: Filter by mood or category
- **EmptyStateView**: Shown when no entries exist
- **FAB**: "+" button to create new entry -> launches DetailActivity
- Top bar actions: search, filter, statistics/settings

### Activity 2: `DetailActivity` — View, Create, Edit (satisfies R1, R3)
- **DetailScreen**: Read-only view with title, full content, mood, category, photo, dates, edit/delete buttons
- **EditScreen**: Form with OutlinedTextFields, MoodSelector (emoji row), CategorySelector (chips/dropdown), PhotoSection (gallery/camera picker), favorite toggle
- Mode switching: if launched with entry ID -> detail view; without -> create mode
- Delete confirmation via AlertDialog

### Activity 3: `SettingsActivity` — Statistics & Settings
- **StatsScreen**: Total entries, current writing streak, mood distribution bars, category breakdown
- **SettingsScreen**: Dark/light theme toggle, about section

### Inter-Activity Navigation
```
MainActivity --(Intent + entryId)--> DetailActivity
MainActivity --(Intent)-----------> SettingsActivity
DetailActivity --(finish)---------> MainActivity (list auto-refreshes via Room Flow)
```

---

## Project Structure

```
app/src/main/java/com/reflect/journal/
├── ReflectApplication.kt              # Application class (DB + repo provider)
├── data/
│   ├── model/
│   │   ├── JournalEntry.kt            # Room @Entity
│   │   ├── Mood.kt                    # Enum with emoji + label
│   │   ├── Category.kt               # Enum with displayName + color
│   │   ├── MoodCount.kt              # DAO query helper
│   │   └── CategoryCount.kt          # DAO query helper
│   ├── local/
│   │   ├── JournalDao.kt             # Room @Dao (all queries)
│   │   └── JournalDatabase.kt        # Room @Database singleton
│   └── repository/
│       └── JournalRepository.kt      # Data abstraction layer
├── viewmodel/
│   ├── JournalViewModel.kt           # Home list + search/filter
│   ├── DetailViewModel.kt            # Single entry CRUD
│   ├── StatsViewModel.kt             # Statistics aggregation
│   └── ViewModelFactory.kt           # ViewModelProvider.Factory
├── ui/
│   ├── theme/
│   │   ├── Color.kt, Type.kt, Theme.kt   # Material 3 theming
│   ├── home/
│   │   ├── MainActivity.kt
│   │   ├── HomeScreen.kt, JournalEntryCard.kt
│   │   ├── SearchBar.kt, FilterBottomSheet.kt, EmptyStateView.kt
│   ├── detail/
│   │   ├── DetailActivity.kt
│   │   ├── DetailScreen.kt, EditScreen.kt
│   │   ├── MoodSelector.kt, CategorySelector.kt, PhotoSection.kt
│   ├── settings/
│   │   ├── SettingsActivity.kt
│   │   ├── StatsScreen.kt, SettingsScreen.kt
│   └── components/
│       ├── ConfirmDeleteDialog.kt, DateText.kt
└── util/
    ├── DateFormatter.kt, StreakCalculator.kt
```

---

## Unit Testing Strategy (satisfies R5)

| Test Class | Type | Location | What It Tests |
|---|---|---|---|
| `JournalDaoTest` | Instrumented | `androidTest/` | All DAO queries against in-memory Room DB (insert, update, delete, search, filter by mood/category, counts) |
| `JournalViewModelTest` | Local | `test/` | CRUD operations via fake repo, search/filter logic, toggleFavorite |
| `StatsViewModelTest` | Local | `test/` | Streak calculation, aggregation correctness |
| `StreakCalculatorTest` | Local | `test/` | Edge cases: empty list, gaps, consecutive days |
| `DateFormatterTest` | Local | `test/` | Date formatting output |
| `FakeJournalRepository` | Test double | `test/` | In-memory repo for ViewModel tests |

---

## Dependencies (`build.gradle.kts`)

- **Compose BOM 2024.01.00** — UI framework (Material 3, icons, tooling)
- **Room 2.6.1** — Database (runtime, ktx, compiler via KSP)
- **Lifecycle 2.7.0** — ViewModel, runtime-ktx, viewmodel-compose, runtime-compose
- **Activity Compose 1.8.2** — Activity + Compose bridge
- **Coil Compose 2.5.0** — Image loading for photo display
- **JUnit 4, Coroutines Test, Core Testing** — Unit/instrumented testing

---

## Implementation Order

### Phase 1: Project Setup
- Create Android Studio project (Empty Compose Activity)
- Configure `build.gradle.kts` with all dependencies
- Set up package structure, `.gitignore`, initial commit

### Phase 2: Data Layer
- Create enums (`Mood`, `Category`), entity (`JournalEntry`), helper classes
- Create `JournalDao`, `JournalDatabase`, `JournalRepository`
- Create `ReflectApplication`, register in manifest
- **Write and run `JournalDaoTest`**

### Phase 3: ViewModel Layer
- Create `JournalViewModel`, `DetailViewModel`, `StatsViewModel`, `ViewModelFactory`
- Extract utilities: `StreakCalculator`, `DateFormatter`
- **Write `FakeJournalRepository` and all ViewModel/utility tests**

### Phase 4: Theme & Common Components
- Set up Material 3 theme (`Color.kt`, `Type.kt`, `Theme.kt`)
- Build reusable components: `ConfirmDeleteDialog`, `DateText`, `EmptyStateView`

### Phase 5: HomeScreen (MainActivity)
- Build `JournalEntryCard`, `SearchBar`, `FilterBottomSheet`, `HomeScreen`
- Wire up `MainActivity` with ViewModel

### Phase 6: Detail/Edit Screen (DetailActivity)
- Build `MoodSelector`, `CategorySelector`, `PhotoSection`
- Build `EditScreen` and `DetailScreen`
- Wire up `DetailActivity` with intent extras, mode switching
- Implement Intent-based navigation from MainActivity
- **Test full CRUD flow manually**

### Phase 7: Settings/Statistics (SettingsActivity)
- Build `StatsScreen` and `SettingsScreen`
- Wire up `SettingsActivity` with StatsViewModel

### Phase 8: Polish
- Content descriptions for accessibility
- String resources for all hardcoded text
- App launcher icon
- Input validation, back-navigation confirmation for unsaved changes
- Edge case handling

### Phase 9: Documentation & Testing
- Run all unit tests, fix failures
- Add KDoc comments to all public classes and functions
- Write `README.md` with build instructions, architecture overview, screenshots

### Phase 10: GitHub Upload
- Final commit, push to GitHub
- Verify repository is complete and buildable from clone

---

## Verification Plan

1. **CRUD Flow**: Create a new entry -> verify it appears in overview -> tap to view detail -> edit title/content/mood/category -> verify changes saved -> delete with confirmation -> verify removed from list
2. **Search & Filter**: Add 3+ entries with different moods/categories -> search by keyword -> filter by mood -> filter by category -> clear filters
3. **Photo Attachment**: Create entry with photo from gallery -> verify photo displays in detail view -> edit entry and change photo -> verify update
4. **Statistics**: Add entries across multiple days with varied moods/categories -> open Stats screen -> verify counts and streak are correct
5. **Unit Tests**: Run `./gradlew test` (all local tests pass) and `./gradlew connectedAndroidTest` (DAO tests pass)
6. **Material Design**: Verify proper use of TopAppBar, FAB, Cards, Chips, Dialogs, typography, spacing, and dark/light theming
7. **Build from Clean**: Clone repo -> open in Android Studio -> build -> run on emulator -> all features work

---

## Requirement Traceability

| Requirement | Satisfied By |
|---|---|
| R1: CRUD operations | DetailActivity (EditScreen + DetailScreen + delete dialog) |
| R2: Overview of all entries | HomeScreen in MainActivity (LazyColumn of JournalEntryCard) |
| R3: Detailed view | DetailScreen in DetailActivity |
| R4: Several Activities | MainActivity, DetailActivity, SettingsActivity (3 total) |
| R5: Unit tests | JournalDaoTest, JournalViewModelTest, StatsViewModelTest, StreakCalculatorTest, DateFormatterTest |
| R6: Material Design | Material 3 theme, TopAppBar, FAB, Cards, Chips, Dialogs, dynamic color |
| R7: Source code documentation | KDoc comments on all public classes and functions |
| R8: GitHub repository | Complete repo with README, build files, source, tests |
