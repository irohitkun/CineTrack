# CineTrack

A modern movie & TV tracking application for Android. Discover content via TMDB, track what you're watching, and manage your personal library — **not** a streaming platform.


## Tech Stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** + **Repository Pattern**
- **Hilt** (Dependency Injection)
- **Retrofit** + **OkHttp** + **Moshi** (TMDB API)
- **Room** (user data only)
- **Coil** (image loading)
- **Coroutines** + **StateFlow**
- **DataStore** (preferences / theme)

## Locked Decisions

| Item | Choice |
|------|--------|
| App name | CineTrack (working title) |
| Package | `com.cinetrack.app` |
| Project path | `~/AndroidStudioProjects/CineTrack` |
| minSdk | 26 |
| targetSdk / compileSdk | 36 |
| AGP / Gradle | AGP 8.8.2 / Gradle 8.11.1 |
| Theme default | **Dark mode** (black / dark gray / white / blue accent) |
| Light mode | Supported (Settings in Phase 7) |
| Catalog storage | **Never** store TMDB catalog in Room |
| Library offline | Cache minimal display fields on user items: `title`, `posterPath` (+ optional backdrop/runtime/genreIds) |
| API key | `local.properties` → `BuildConfig.TMDB_API_KEY` (never hardcode) |
| Nice-to-haves (Phase 8) | Prioritize with client; not required for core v1 |

## Requirements

- Android Studio (Ladybug or newer recommended)
- JDK 11+
- Android SDK 36
- A free [TMDB API key](https://www.themoviedb.org/settings/api)

## Setup

1. Open `~/AndroidStudioProjects/CineTrack` in Android Studio.

2. Add your TMDB API key to `local.properties` (git-ignored):

   ```properties
   sdk.dir=/path/to/your/Android/sdk
   TMDB_API_KEY=your_api_key_here
   ```

3. Sync Gradle and run on a device/emulator (API 26+).

## Architecture

```
TMDB API
    │
 Retrofit
    │
 Repository
    │
───────────────
│             │
TMDB        Room
(Live)     (User Data)
│             │
└───────┬─────┘
        │
 Compose UI (ViewModel + StateFlow)
```

### Room (user data only)

Suggested entity fields for `UserMedia`:

- `tmdbId`, `mediaType` (movie/tv)
- `status` (Watching / Completed / PlanToWatch)
- `personalRating` (0–10), `favorite`
- `notes`, `review`
- `currentSeason`, `currentEpisode` (TV)
- `dateAdded`, `dateFinished`
- Display cache only: `title`, `posterPath` (not a catalog mirror)

## Project Structure

```
app/src/main/java/com/cinetrack/app/
├── data/
│   ├── api/           # Retrofit, DTOs, interceptors
│   ├── database/      # Room entities, DAOs, DB
│   └── repository/
├── di/                # Hilt modules
├── domain/            # Models
├── navigation/        # Nav graph & routes
├── ui/
│   ├── screens/       # home, search, details, library, profile, settings
│   ├── components/
│   └── theme/
├── utils/
├── MainActivity.kt
└── CineTrackApplication.kt
```

## Build Phases (source of truth)

| Phase | Status | Deliverable |
|-------|--------|-------------|
| **0** | ✅ Done | Scaffold, Material 3 theme, Hilt, bottom nav shell, README, builds |
| **1** | ✅ Done | Domain models, TMDB API layer, Room, repositories, Hilt DI modules |
| **2** | ✅ Done | Reusable Compose components (PosterCard, SectionRow, Loading/Error/Empty) |
| **3** | ✅ Done | Home: Trending / Now Playing / Upcoming / Top Rated / Popular (movies + TV) |
| **4** | ✅ Done | Search: live debounced TMDB search (movies + TV) |
| **5** | ✅ Done | Details: full metadata, cast, library actions, TV episode progress |
| **6** | ✅ Done | Library: Watching / Completed / Plan To Watch / Favorites from Room |
| **7** | ✅ Done | Profile stats + Settings (theme, clear/export/import library, TMDB attribution) |
| **8** | Pending | Polish: recently viewed, search history, infinite scroll, etc. |

### Phase details (for resume)

**Phase 1 — Data layer**
- TMDB: trending, popular, top rated, upcoming, now playing, movie/TV details, search, images, genres, cast
- Repositories: `TmdbRepository` (network), `LibraryRepository` (Room)
- Loading / empty / error / retry handled at UI layer later

**Phase 2 — UI kit**
- PosterCard, horizontal SectionRow, LoadingState, EmptyState, ErrorState+Retry, RatingBadge

**Phase 3 — Home**
- Parallel section loads via ViewModel StateFlow; LazyRow posters → Details

**Phase 4 — Search**
- Debounce ~300ms; show poster, title, year, media type, TMDB rating

**Phase 5 — Details**
- Backdrop, poster, tagline, overview, genres, dates, runtime, rating, companies, seasons
- Actions: Add / Watching / Completed / Plan To Watch / Favorite / Share
- TV: Season± Episode±, progress bar, Mark Complete

**Phase 6 — Library**
- Sections from Room only; cards show poster, title, status, personal rating, TV progress

**Phase 7 — Profile / Settings**
- Stats: completed counts, watching, plan, favorites, avg rating, estimated hours, most-watched genre
- Settings: dark/light, clear library, export/import JSON, About + TMDB attribution

**Phase 8 — Nice-to-haves (optional)**
- High: pull-to-refresh, animations, empty states
- Medium: recently viewed, search history, infinite scroll
- Lower: shared elements, genre filter, recommendations, release calendar, countdowns

## Bottom Navigation

Home · Search · Library · Profile / Statistics

## How to Continue

1. Open this project in Cursor / Android Studio.
2. Read this README (especially **Locked Decisions** and **Build Phases**).
3. Tell the agent: **"start Phase 1"** (or the next pending phase).
4. Optional: paste your TMDB key into `local.properties` before networking work.

Chat export is optional. Prefer this README as continuity.

## TMDB Attribution

This product uses the [TMDB API](https://www.themoviedb.org/documentation/api) but is not endorsed or certified by TMDB.

## Credits

Authored by [Rohit](https://github.com/irohitkun)

Co-Authored by [Cursor AI](<cursoragent@cursor.com>)
