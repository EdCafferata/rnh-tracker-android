# RNH Tracker (Android)

🔒 Laatste security check: 2026-08-30 00:05 CEST

Android port of [RNH GPX Tracker](https://github.com/EdCafferata/RNH-GpxTracker) (internally `OpenRHNTracker`), a free GPX tracker originally built for the Ronde om Noord-Holland.

This is a rebrand of [BVK Tracker Android](https://github.com/EdCafferata/bvk-tracker-android) — the iOS app is itself a clone of BVK GPX Tracker, so the Android port follows the same pattern: same architecture, different branding, colors, and default map location. See that repo's README for the shared Milestone 1 feature list and the full multi-milestone roadmap (weather/water-level overlays, scale bar, preferences, Wear OS companion — none of that is ported yet, on either BVK or RNH Android).

## Status

**Working (Milestone 1, same as BVK Tracker Android):**
- Map view (osmdroid): OpenStreetMap, Carto DB, Carto DB Retina, OpenTopoMap, Carto DB Dark Matter, with an OpenSeaMap sea-marks overlay toggle
- Live GPS tracking via a foreground service (plain platform `LocationManager`, no Play Services dependency), start/stop, live distance + speed, "follow my location"
- Save as standard `.gpx`, share via the system share sheet, saved-tracks list with delete
- Verified end-to-end on the emulator with simulated GPS movement near the route's IJmuiden starting point

**Not yet ported:** same list as BVK Tracker Android — weather (Open-Meteo, incl. wave height/period via the Marine API on the iOS side), OpenWeatherMap tile overlays, scale bar, full preferences, Wear OS companion.

## Requirements

- JDK 17+ (project built and tested with Homebrew's `openjdk@21`)
- Android SDK, compileSdk 36, minSdk 26
- Gradle 9.6.1+ (via the included wrapper)

## Build

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools   # or your own SDK path
./gradlew assembleDebug
```

The debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## Licence

GPL-3.0 — see [LICENSE](LICENSE), same as the iOS app.
