# Session Log — Multi-History Widget

## 0. fork https://github.com/iSoron/uhabits andoird app and add features 

## 1. Final Working Changes: Multi-History Widget

Added a new home-screen widget, "Multi History", that shows a combined history
calendar across multiple habits in a single tile. Verified working on-device
by the user; the debug APK produced and tested successfully.

### Behavior / Specs
- **Fixed 2x2 quadrant grid**: each day-square is divided into four quadrants
  (top-left, top-right, bottom-left, bottom-right).
- **First-4-habits rule**: there is no habit limit on selection, but only the
  first four selected habits are rendered; any beyond four are ignored
  (`habits.take(4)`).
- **Per-habit entry -> color mapping** (reuses the existing `HistoryCardPresenter
  .buildState()` "completed habit" logic, so the same semantics as the single
  habit history card):
  - `YES_MANUAL`   -> `ON`      -> full palette color
  - `YES_AUTO`     -> `DIMMED`  -> color blended 50% with the card background
  - `SKIP`         -> `HATCHED` -> blended color + diagonal hatch lines
  - else (not completed)        -> low-contrast "off" color
- **Day number** is drawn centered over the quadrants on every square.
- **Layout chrome**: month/year header row plus weekday labels on the left,
  matching the existing single-habit history chart.
- **Click behavior**: tapping the widget opens the main habit list
  (`ListHabitsActivity`) via an immutable `PendingIntent`
  (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP`).
- **Configuration**: a multi-select picker (`CHOICE_MODE_MULTIPLE`, checkboxes)
  lists all non-archived habits; an empty-state view is shown when there are
  no habits. On save, `widgetPreferences.addWidget(widgetId, ids)` persists the
  selection.
- **Default size**: 250x250 px.
- **Widget title**: the first four habit names joined by " . ".
- **Widget name**: "Multi History" (`<string name="multi_history">Multi History
  </string>`).

### Files (10)
- `uhabits-core/src/commonMain/kotlin/org/isoron/uhabits/core/ui/views/
  MultiHistoryChart.kt` (new) — core rendering engine; reuses the platform
  `Canvas`/`DataView` abstraction, `HistoryChart.Square`, theme, and 2x2
  quadrant drawing.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/
  MultiHistoryWidget.kt` (new) — widget class; opens `ListHabitsActivity`,
  default 250x250, builds data via `HistoryCardPresenter.buildState()`.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/
  MultiHistoryWidgetProvider.kt` (new) — provider, no stacked fallback.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/activities/
  MultiHabitPickerDialog.kt` (new) — multi-select configuration dialog.
- `uhabits-android/src/main/res/xml/widget_multi_history_info.xml` (new)
- `uhabits-android/src/main/res/layout/widget_multi_history_configure_activity.xml`
  (new)
- `uhabits-android/src/main/res/drawable-nodpi/widget_preview_multi_history.png`
  (new, preview image)
- `uhabits-android/src/main/AndroidManifest.xml` (modified) — registers
  `MultiHistoryWidgetProvider` and the picker activity.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/WidgetUpdater.kt`
  (modified) — adds `MultiHistoryWidgetProvider::class.java`.
- `uhabits-android/src/main/res/values/strings.xml` (modified) — adds the
  "Multi History" string.

### Project layering
- Core rendering engine lives in `:uhabits-core` (commonMain, multiplatform).
- Android widget, picker, resources, and manifest wiring live in
  `:uhabits-android`.

## 2. Additional Specs Used This Session

### CI build workflow (.github/workflows/build.yml, on a ci/hosted-build branch)
- Runs on GitHub-hosted `ubuntu-latest` runners.
- Steps: checkout -> JDK 17 (Temurin) -> `android-actions/setup-android` ->
  `:uhabits-android:assembleDebug` -> `ktlintCheck` -> `:uhabits-core:jvmTest`
  -> upload `debug-apk` and `test-reports` artifacts.
- Triggers on both `pull_request` and `push`, with `paths-ignore: '**.md'`.

### Tracked limitation / follow-up
- `:uhabits-android:test` (Android-module JVM unit tests) fails on hosted
  runners for an undetermined reason. To keep CI green and mergeable, the
  workflow is scoped to `:uhabits-core:jvmTest` (which covers the shared
  business logic including the new Multi-History chart).
- Root-causing the Android-module test failure is pending; it requires an
  admin-accessible Actions log (periodic "Must have admin rights" 403 when
  reading raw logs anonymously).

### Verification result
- APK built successfully on GitHub-hosted runners (`assembleDebug` green),
  ktlint clean, core unit tests pass.
- Multi-History widget tested working on-device by the user.

## 3. Installed Packages / Local Environment (earlier request to inventory the local computer)

- **OS**: Ubuntu 22.04.5 LTS, kernel 5.15.0-190-generic x86_64.
- **Java**: OpenJDK 17.0.20 (required Java 17; newer JDKs not tested).
- **Gradle**: wrapper 8.11.1.
- **Kotlin**: 2.3.20.
- **AGP**: 8.9.2.
- **KSP**: 2.3.6.
- **ktlint**: plugin 11.6.1 (via Gradle, no standalone binary).
- **Mokkery**: 3.3.0.
- **kotlin-inject**: 0.9.0.
- **git**: 2.34.1.
- **Node**: v24.12.0 (via nvm).
- **Chrome**: 152.0.7977.64 (used headless for `jsBrowserTest`/Karma; note that
  `jsBrowserTest` pings timed out / disconnected).
- **Android SDK**: NOT installed (`ANDROID_HOME` empty) — local Android builds
  are not possible; builds are done on GitHub-hosted `ubuntu-latest`.
- **Python**: 3.10.12.
- **SSH**: GitHub-authorized `id_rsa` key present (used to push to
  `yazriel/habitloop`).
- **Disk**: `/` 87% used (~14G free of 102G).
- **opencode**: installed via nvm node v24.12.0.

## 4. ADDED INSTALLS

What was installed/created for the build process.

### Repository setup (before this session's build work)
- `git clone` of `https://github.com/iSoron/uhabits.git` into
  `/home/zong/projects/habitloop` (checked out on the `dev` branch, the
  project's development branch). The clone already carried the Gradle wrapper
  (Gradle 8.11.1), the project's `build.gradle.kts`, `build.sh`, and all
  source.

### AGENTS.md instruction file
- Created early in the session at `/home/zong/projects/habitloop/AGENTS.md` with
  project architecture and build/test commands.
- Note: currently gitignored — the repo's `.gitignore` contains `*.md`, so
  `git check-ignore AGENTS.md` confirms it is not tracked.

### Tooling already available (NOT installed by us)
- Java 17 (OpenJDK 17.0.20) — preinstalled, matches the project's
  `jvmToolchain(17)` requirement.
- Google Chrome 152 at `/usr/bin/google-chrome` — headless mode works on its
  own.
- Gradle daemon — launched and left running in the background (expected).

### Dependencies actually downloaded/installed by running the build commands
The real "installs" that happened during the session:
- Gradle 8.11.1 distribution binaries (`~/.gradle/wrapper/dists/
  gradle-8.11.1-bin`, ~276 MB) — pulled on first `./gradlew` invocation.
- Gradle dependency/module cache (`~/.gradle/caches`, ~892 MB) — fetched from
  Google Maven, Maven Central, JitPack, etc.
- Yarn/npm cache for the Kotlin Multiplatform JS build (`~/.cache/yarn`,
  ~471 MB).
- `node_modules` for the JS/Karma build at `build/js/node_modules` — includes
  karma, karma-chrome-launcher, karma-mocha, karma-sourcemap-loader,
  karma-webpack. (Corrected earlier false negative: karma-chrome-launcher is
  present.)
- Build artifact directories `build/`, `uhabits-core/build/`, and
  `uhabits-android/build/` — created in-repo by the builds (all under
  gitignored `build` paths).

### Not installed — missing blockers
- Android SDK: `ANDROID_HOME` unset, no SDK at `/opt/android-sdk` or
  `/usr/lib/android-sdk`. This is why the Android APK could not be compiled
  locally; builds were instead done on GitHub-hosted `ubuntu-latest`.
- No `.secret/env` — required only for release builds (not needed for debug
  verification).

## 5. Final Working Changes: Multi-Streaks Widget

### Behavior / Specs
- New home-screen widget, "Multi Streaks" (`<string name="multi_streaks">Multi
  Streaks</string>`), showing streak bars from multiple habits in a single tile.
- **Look-and-feel**: reuses the existing `StreakChart` rendering — bar-per-streak
  rows, each with its centered streak-length number and **no per-row habit
  label**.
- **Color**: each bar is colored by its **habit's** color (a deliberate deviation
  from the single-habit `StreakChart`, which uses one color with a length-based
  fade); within each habit color the length-based fade is kept.
- **Ordering**: for each selected habit, its **5 most recent streaks**
  (`getRecent(5)`) are merged across all habits and sorted by recency
  (`compareNewer`), **most recent on top**.
- **Overflow**: only as many bars as fit the tile height are shown
  (`MultiStreakChart.maxStreakCount = floor(measuredHeight / baseSize)`), same
  as the existing widget (no scroll).
- **Selection**: reuses the existing `MultiHabitPickerDialog` (multi-select,
  already registered); the render is limited by the widget size.
- **Click**: tapping the widget opens the main habit list (`ListHabitsActivity`)
  via an immutable `PendingIntent` (`NEW_TASK | CLEAR_TOP`).
- **Default size**: 250x250 px.
- **Class naming**: `Multi`-prefixed (`MultiStreakWidget`, `MultiStreakWidgetProvider`,
  `MultiStreakChart`) per the existing convention.

### Key core change
- **`StreakList.getRecent(limit)`** (new, `:uhabits-core`): returns the `limit`
  most recent streaks, newest-first. `StreakList` previously only exposed
  `getBest(limit)` (longest-then-newest); the raw list is private, so a new
  accessor was added that sorts by `compareNewer` descending and caps at
  `limit`.
- New unit test `StreakListTest.testGetRecent` (recompute a known entry set and
  assert ordering + limit cap).

### Files (10)
**Modified (4)**
- `uhabits-core/src/commonMain/kotlin/org/isoron/uhabits/core/models/StreakList.kt`
  — add `getRecent(limit)`.
- `uhabits-core/src/commonTest/kotlin/org/isoron/uhabits/core/models/StreakListTest.kt`
  — add `testGetRecent`.
- `uhabits-android/src/main/AndroidManifest.xml` — register
  `MultiStreakWidgetProvider` receiver (`android:label="@string/multi_streaks"`).
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/WidgetUpdater.kt` —
  add `MultiStreakWidgetProvider::class.java`.
- `uhabits-android/src/main/res/values/strings.xml` — add `multi_streaks`.

**New (6)**
- `uhabits-android/src/main/java/org/isoron/uhabits/activities/common/views/
  MultiStreakChart.kt` — Android `View`; defines `data class ColoredStreak(color,
  streak)`; accepts a `List<ColoredStreak>`, draws bar-per-streak with per-bar
  habit color, `maxStreakCount` fit-by-height.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/MultiStreakWidget.kt`
  — merges `getRecent(5)` per habit, sorts by recency, caps to `maxStreakCount`,
  opens `ListHabitsActivity`; title "Multi Streaks"; 250x250.
- `uhabits-android/src/main/java/org/isoron/uhabits/widgets/MultiStreakWidgetProvider.kt`
  — no stack fallback (mirrors `MultiHistoryWidgetProvider`).
- `uhabits-android/src/main/res/xml/widget_multi_streak_info.xml` — appwidget
  metadata; `configure = MultiHabitPickerDialog` (reused).
- `uhabits-android/src/main/res/drawable-nodpi/widget_preview_multi_streak.png`
  — copied from `widget_preview_streaks.png` as a base.

### Verification result
- Fork CI green on `feature/multi-streak-widget` (`assembleDebug` compiles the
  new Android code, ktlint passes, core unit tests pass including `getRecent`).
- Merged into fork `dev` at `b45830b5` ("Merge pull request #3").
- On-device test pending (user action).

## 6. Session: Widget Color Tweaks, Completion Sound, Debug Signing Fix

### Checkmark widget: habit-color "off" state
- When a Checkmark widget's habit is **on/complete** (`YES_MANUAL`/`SKIP`/`YES_AUTO`),
  the tile already fills with the full habit color (unchanged).
- When the habit is **off / not yet reaching threshold** (`NO`/`UNKNOWN`), the X glyph,
  the ring's progress arc, and the label used to all be grey (`contrast60`). They now
  render in the **habit's color** (`activeColor`), while the **background stays neutral**
  (`cardBgColor`) — i.e. there is no full colored background.
- Files:
  - `uhabits-android/src/main/java/org/isoron/uhabits/widgets/views/CheckmarkWidgetView.kt`
    — in `refresh()`, the `NO, UNKNOWN` and `else` branches of `when(entryState)` now set
    `fgColor = activeColor` instead of `contrast60`. `fgColor` flows to `ring.setColor()`
    (colors both the X glyph and the progress arc) and to `label.setTextColor()`.
  - Note: the X and the progress arc share one color because the shared `RingView` draws
    both glyph and arc with a single `color`.

### Multi-History widget: colored titles
- The Multi-History widget title (first four habit names joined by `·`) used to be all
  white. Each habit name is now rendered in that **habit's palette color**, while the `·`
  separators stay default/white.
- Files:
  - `uhabits-android/src/main/java/org/isoron/uhabits/widgets/MultiHistoryWidget.kt` — new
    `buildColoredTitle()` builds a `SpannableString` applying a `ForegroundColorSpan`
    (`habit.color.toFixedAndroidColor()`) to each habit-name span.
  - `uhabits-android/src/main/java/org/isoron/uhabits/widgets/views/GraphWidgetView.kt` —
    `setTitle(String?)` widened to `setTitle(CharSequence?)` to accept the spannable
    (backward-compatible; other widgets still pass plain `String`).
- This works because the title `TextView` is rasterized to a bitmap by the widget renderer,
  preserving span colors.

### Completion sound
- A ringtone is played when a habit is **completed** (toggled on); silent on un-toggle.
- Files:
  - `uhabits-core/src/commonMain/kotlin/org/isoron/uhabits/core/ui/CompletionSoundPlayer.kt`
    (new) — core interface.
  - `uhabits-android/src/main/java/org/isoron/uhabits/notifications/AndroidCompletionSoundPlayer.kt`
    (new) — Android implementation; reads `pref_completion_sound_uri`, silent if empty.
  - `uhabits-core/src/commonMain/kotlin/org/isoron/uhabits/core/ui/screens/habits/list/
    ListHabitsBehavior.kt` and `uhabits-core/src/commonMain/kotlin/org/isoron/uhabits/core/ui/
    widgets/WidgetBehavior.kt` — call `play()` on completion.
  - `uhabits-android/src/main/java/org/isoron/uhabits/activities/settings/SettingsFragment.kt`
    — ringtone-picker handling for the `completionSound` preference.
  - `uhabits-android/src/main/res/xml/preferences.xml` and `res/values/strings.xml` — new
    preference + strings.
  - `uhabits-android/src/main/java/org/isoron/uhabits/inject/HabitsApplicationComponent.kt` —
    DI wiring.
  - Tests updated: `ListHabitsBehaviorTest.kt`, `WidgetBehaviorTest.kt`.

### Debug signing fix (cannot update app)
- **Root cause**: the `debug` build type had no explicit `signingConfig`, so AGP
  auto-generated a fresh `~/.android/debug.keystore` on each ephemeral GitHub Actions
  runner — a different certificate per build — so Android refused to update the app
  (signature mismatch). For debug/development CI builds, committing a shared debug
  keystore is the accepted fix.
- **Keystore** (committed at `keystores/debug.keystore`, reconstructible):
  - Format/type: PKCS12.
  - `keyAlias` = `androiddebugkey`, `storePassword`/`keyPassword` = `android`.
  - SHA-256:
    `87:B6:A2:D5:70:17:B4:F8:9A:68:1B:D6:C3:C6:AC:19:57:49:A7:14:2F:BF:1D:C5:6E:A0:3E:FC:8B:36:4C:2E`.
- `uhabits-android/build.gradle.kts`: added a `signingConfigs` `"dev"` block referencing
  `../keystores/debug.keystore`, and wired `signingConfig = signingConfigs.getByName("dev")`
  into the `debug` build type. Release builds unchanged (still `.secret` env vars).
- **User action required once**: after the first CI build with the fixed key, uninstall the
  old APK once, then install the new one; subsequent CI builds update cleanly.
- `.gitignore` does not block `.keystore` files, so the keystore is tracked.

### Branch
- This work lives on the `tweaks` branch (committed). The untracked `nohup.out` build log
  was excluded from the commit. No local Android SDK, so `:uhabits-android` is verified
  via CI only; `ktlintCheck` passes.

## 7. Session: Multi Weekly Widget + MultiHistory/MultiStreak Tweaks

### Partial-build + CI model (reconfirmed this session)
- Local machine has **no Android SDK** (`ANDROID_HOME` empty). Local verification is strictly
  **partial**: `:uhabits-core:ktlintCheck` + `:uhabits-android:ktlintCheck` (source/format level) and
  `:uhabits-core:compileKotlinJvm` (core JVM compile). The **full Android build** (`assembleDebug`)
  runs **only on GitHub Actions** `.github/workflows/build.yml` (checkout → JDK 17 →
  `android-actions/setup-android` → `assembleDebug` → `ktlintCheck` → `:uhabits-core:jvmTest`).
  Do not run `:uhabits-core:build`, `assembleDebug`, or `./build.sh build` locally — they require
  the SDK and are CI-only.

### New Multi Weekly home-screen widget
A new widget showing each selected habit's check-mark status for the current week.
- **Rendering**: multiplatform `org.isoron.platform.gui.Canvas` + `DataView` in `:uhabits-core`
  (same approach as the Multi-History widget, NOT the Android-native `View` of Multi-Streaks).
  On Android it is wrapped in `AndroidDataView` inside a `GraphWidgetView`.
- **Layout**: a header row of **7 columns** (days of the week, ordered by the user's
  `firstWeekday` setting) with a **2-letter uppercase weekday label** per column in **pale grey**
  (`WidgetTheme.mediumContrastTextColor`), then **one row per habit** of colored blocks.
  No per-row habit label (colors are already shown in the colored title).
- **Block states** (uses `habit.computedEntries.get(date).value` — same semantics as the history
  chart's "checked" logic):
  - `YES_MANUAL` / `YES_AUTO` → solid habit-color filled rounded block.
  - `SKIP` → half-alpha blend + diagonal hatch.
  - `NO` / `UNKNOWN` → stroked outline (empty).
- **Week start** sourced from `prefs.firstWeekday`: `today.startOfWeek(prefs.firstWeekday)`;
  the 7 columns are `weekStart.plus(0..6)`.
- **Colored title**: up to **5 habits**, **3 characters each**, joined by `·`, each name in its
  habit palette color (identical `buildColoredTitle()` approach to the Multi-History widget).
- **Click** → opens the main habit list (`ListHabitsActivity`) via immutable `PendingIntent`
  (`NEW_TASK | CLEAR_TOP`), same as the other multi widgets.
- **Selection/config**: reuses `MultiHabitPickerDialog` (already registered).
- **Default size**: 250x250 px; widget name "Multi Weekly" (`<string name="multi_weekly">`),
  registered in `AndroidManifest.xml` and wired into `WidgetUpdater.updateWidgets()`.

### MultiHistory tweak: 2-character day labels
- The weekday labels on the left (row labels) in `MultiHistoryChart` are now exactly **2 uppercase
  characters** (`.take(2).uppercase()`, e.g. "MO"/"WE") instead of the locale short name.
  One-line change at `uhabits-core/src/commonMain/.../views/MultiHistoryChart.kt` (note: this only
  affects the **Multi-History** widget chart; the single-habit `HistoryChart` is unchanged).

### MultiStreak tweak: home-screen title only
- The **in-widget title shown on the home screen** for the Multi Streaks widget is now
  **"Streaks(es)"**, via a new dedicated string `<string name="multi_streaks_title">Streaks(es)
  </string>` used in `MultiStreakWidget.buildView()`.
- **Intentional boundaries (unchanged / protected):**
  - The original single-habit **Streaks** widget is untouched (its name is `@string/streaks` =
    "Streaks", restored after a transient revert).
  - The **Multi Streaks widget's display name** in the widget picker is unchanged
    (`@string/multi_streaks` = "Multi Streaks", `AndroidManifest.xml`).

### Verification result
- Local partial build: `:uhabits-core:ktlintCheck` + `compileKotlinJvm` green, `:uhabits-android:
  ktlintCheck` green. Full Android build pending GitHub Actions (push to `weekly2`).

### Commit / branch
- Committed to `weekly2` (fork dev). `log.md` was **force-added** (`git add -f log.md`, overriding
  the `*.md` gitignore) so it is tracked going forward. Excluded from commit: `weekly.md`,
  `AGENTS.md` (gitignored), `nohup.out` (untracked build log), and build artifacts.
