# s8137431Assignment2 — Movie Explorer

NIT3213 Assignment 2: an Android app with Login, Dashboard, and Details screens that
authenticate against and fetch data from the `nit3213apinew` API.

## Features

- **Login** — signs in against the Sydney-campus auth endpoint and validates input before
  calling the network.
- **Dashboard** — lists the movies returned for the session's `keypass` in a RecyclerView,
  with loading, empty, and retryable error states.
- **Details** — shows the full record (including description) for a selected movie.

## Architecture

- **UI**: single `MainActivity` hosting three fragments (`LoginFragment`, `DashboardFragment`,
  `DetailsFragment`) through one Jetpack Navigation Component graph
  (`res/navigation/nav_graph.xml`), with a `MaterialToolbar` wired to the `NavController` for
  titles and Up navigation.
- **Presentation**: MVVM. `LoginViewModel` and `DashboardViewModel` expose UI state via
  Kotlin `StateFlow` (plus a `SharedFlow` for the one-shot "navigate to dashboard" event) and
  are unit tested in isolation from Android.
- **Data**: `AuthRepository` / `MovieRepository` wrap a Retrofit `ApiService` and return a
  small `ApiResult` sealed type (`Success` / `Error(type)`) so the UI layer can distinguish
  network, timeout, unauthorized, and server errors instead of catching raw exceptions.
- **DI**: Hilt (`NetworkModule`, `RepositoryModule`) provides the OkHttp/Retrofit stack and
  binds the repository interfaces to their implementations.

```
app/src/main/java/com/aashika/assignment2/
  di/                 Hilt modules (network, repositories)
  data/               DTOs, ApiService, repositories, ApiResult, domain model
  ui/login/           LoginFragment + LoginViewModel
  ui/dashboard/       DashboardFragment + DashboardViewModel + MovieAdapter
  ui/details/         DetailsFragment
app/src/test/java/com/aashika/assignment2/
  ui/login/           LoginViewModelTest
  ui/dashboard/       DashboardViewModelTest
```

## Tech stack

Kotlin 2.4, AGP 9.3 with built-in Kotlin support (no separate `kotlin-android` plugin), KSP,
Hilt, Retrofit 3 + OkHttp, Navigation Component with Safe Args, and JUnit4 + MockK +
kotlinx-coroutines-test for ViewModel tests.

## Building and running

**Requirements**: a recent Android Studio release with JDK 17+ (a bundled JDK works — the
project was built and tested here against Eclipse Temurin 25), and an internet connection the
first time you build (Gradle 9.7.1 and all dependencies are downloaded automatically).

> **AGP/Android Studio compatibility**: this project pins the Android Gradle Plugin version in
> `gradle/libs.versions.toml` (`[versions] agp = ...`). AGP 9.x is very new, and each Android
> Studio release only supports AGP versions up to whatever shipped with it — if Gradle sync
> fails with an "incompatible AGP version" error naming a lower supported version, lower the
> `agp` value in `gradle/libs.versions.toml` to match (Android Studio's own error message
> tells you the exact version to use) and re-sync.

1. Clone the repository and open the project root in Android Studio.
2. Let Gradle sync; on first sync it will download the Gradle 9.7.1 distribution, the Android
   Gradle Plugin, and the SDK components declared in `app/build.gradle.kts`
   (`compileSdk = 37`, `minSdk = 26`).
3. Run the `app` configuration on a device or emulator running Android 8.0 (API 26) or later.

From the command line instead:

```bash
./gradlew assembleDebug   # build the debug APK
./gradlew installDebug    # install it on a connected device/emulator
```

## Running the tests

```bash
./gradlew testDebugUnitTest
```

This runs the `LoginViewModelTest` and `DashboardViewModelTest` suites (9 tests) covering
field validation, the login-success navigation event, error-type mapping, error clearing, and
the dashboard retry flow.

## Notes

- The login credentials for this assignment are fixed by the brief: username `8137431`,
  password `Aashika` (case-sensitive), against the `/sydney/auth` endpoint.
- The API is hosted on Render's free tier, which can cold-start slowly after inactivity;
  network timeouts are set to 30s and a slow first request is surfaced as a normal
  "couldn't load" state rather than a crash.
