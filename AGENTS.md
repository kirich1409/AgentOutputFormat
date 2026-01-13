# Repository Guidelines

## Project Structure & Module Organization
- `build.gradle.kts` and `settings.gradle.kts` define a single-module Kotlin JVM project.
- `src/main/kotlin/` is the expected location for production Kotlin sources (currently empty).
- `src/test/kotlin/` is the expected location for unit tests (currently empty).
- `gradle/` and `gradlew*` provide the Gradle wrapper for consistent builds.

## Build, Test, and Development Commands
- `./gradlew build` — compiles sources and runs tests.
- `./gradlew test` — runs the test suite only.
- `./gradlew clean` — removes build outputs.
- `./gradlew tasks` — lists available Gradle tasks for this project.

## Coding Style & Naming Conventions
- Language: Kotlin JVM (Kotlin plugin version in `build.gradle.kts`).
- Indentation: 4 spaces; avoid tabs.
- Naming: use `UpperCamelCase` for classes, `lowerCamelCase` for functions/variables, and `lower_snake_case` for package segments (e.g., `dev.androidbroadcast.agent.customoutput`).
- Prefer small, focused functions and immutable `val` where possible.

## Testing Guidelines
- Testing framework: Kotlin test with JUnit Platform (`useJUnitPlatform()` in Gradle).
- Place tests under `src/test/kotlin/` with names like `FooTest`.
- Run tests via `./gradlew test`. Add targeted test tasks only if needed.

## Commit & Pull Request Guidelines
- No commit message conventions are established yet (repo has no commits). Use clear, imperative messages (e.g., "Add agent output processor") until a standard is defined.
- PRs should include a short description, rationale, and any relevant screenshots or sample output when behavior changes.

## Configuration & Tooling Notes
- Java toolchain is set to 21; ensure a JDK 21 is available or let Gradle download via toolchains.
- Add new dependencies in `build.gradle.kts` under the appropriate configuration (e.g., `implementation`, `testImplementation`).
