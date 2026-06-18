# Elaina

[中文](README.md)

Elaina is an Android application that optimizes device performance and power consumption by binding app processes to specific CPU cores. It requires Root access and the AppOpt Magisk module.

## Features

### Core
- **CPU Core Binding**: Bind apps/processes/threads to specific CPU core groups (Power Saving / Balanced / High Performance)
- **Real-time CPU Monitoring**: Line or bar chart display of per-core utilization
- **System Info**: Device model, CPU core/thread count, manufacturer
- **Root Manager Detection**: Auto-detect Magisk / KernelSU / APatch with version info; tap to open
- **Rule Management**: Add, edit, and delete CPU affinity rules

### Dual UI Styles
- **MiuiX**: Standard Miuix Design style (Material You)
- **LiquidGlass**: Glass morphism style with real-time blur, lens refraction, and vibrancy via Kyant Backdrop

### LiquidGlass Customization (Settings → UI Style → LiquidGlass → Customize LiquidGlass)
- **Vibrancy**: Background color bleed effect
- **Blur Radius**: Frosted glass intensity (0–50 dp)
- **Lens Dilation**: Lens refraction area
- **Lens Blur**: Lens edge blur spread
- **Chromatic Aberration**: Rainbow refraction effect
- **Highlight**: Ambient light reflection on press, enhancing 3D glass feel
- **Reset to Defaults**: One-tap restore

### Appearance
- **Theme System**: Follow System / Light / Dark / Monet color extraction
- **Custom Accent Color**: Custom seed color in Monet mode (with expand/collapse animation)
- **Background Style**: Default / Dream Fluid Gradient / Custom Image
- **Card Press Feedback**: None / Sink / Tilt

### Other
- **Update Check**: Auto-check Gitee Release on startup
- **Debug Mode**: Full log output
- **Crash Logging**: Auto-records screen and user action on crash
- **Log Export**: Save or share application logs

## Architecture

```
Elaina
├── navigation/          # Navigation (NavBar, MainNavScreen)
├── screen/
│   ├── home/           # Home
│   ├── thread/         # Thread management
│   ├── log/            # Log viewer
│   ├── settings/       # Settings
│   ├── loading/        # Startup
│   └── welcome/        # Welcome
├── ui/
│   ├── component/      # LiquidGlass component library (20+ components)
│   └── utils/          # UI utilities (InteractiveHighlight, DampedDragAnimation)
├── utils/              # Business utilities
│   ├── AppSettings.kt / SettingsStore.kt  # Settings (DataStore)
│   ├── CrashHandler.kt                    # Crash capture
│   ├── UpdateChecker.kt                   # Update checker (GitHub API)
│   ├── RootUtils.kt                       # Root detection (Magisk/KSU/APatch)
│   ├── ModuleChecker.kt                   # Module detection
│   ├── CpuMonitor.kt                      # CPU monitoring
│   └── SystemInfoProvider.kt              # System info
├── service/            # Background service
└── viewmodel/          # ViewModel
```

## Dependencies

- **Jetpack Compose** + **Material3**: Declarative UI
- **Miuix KMP**: MIUI/HyperOS style component library
- **Kyant Backdrop**: Glass morphism engine (vibrancy, blur, lens)
- **Kyant Shapes**: Capsule / RoundedRectangle shapes
- **Vico**: CPU chart library (line / bar)
- **Coil**: Image loading
- **DataStore**: Key-value persistence
- **Kotlin Coroutines + Flow**: Async and reactive data streams
- **AndroidX Navigation** + **Navigation3**: Navigation framework
- **Material Icons Extended**: Icon library

## Build

```bash
git clone https://github.com/JetComX/Elaina.git
cd thread-box
./gradlew assembleDebug
```

### Requirements
- Android Studio Hedgehog (2023.1.1) or higher
- Kotlin 2.2+
- AGP 9.2+
- compileSdk 37
- minSdk 31

## Usage

1. **Install**: Download APK from Releases
2. **Root Access**: Required for CPU affinity
3. **Magisk Module**: Install AppOpt (thread optimization), use "Get Module" in-app
4. **Add Rules**: Thread page → FAB → fill package name, CPU core range
5. **Switch UI**: Settings → UI Style → MiuiX / LiquidGlass
6. **Customize Effects**: LiquidGlass mode → Customize LiquidGlass

## Permissions

| Permission | Purpose |
|------------|---------|
| INTERNET | Update checking |
| ACCESS_NETWORK_STATE | Network state detection |
| FOREGROUND_SERVICE | Background service |
| QUERY_ALL_PACKAGES | Query installed apps |

## Credits

Special thanks to [Thread Optimizer (AppOpt)](http://appopt.suto.top/) by SutoLiu — the Magisk module Elaina depends on.

Thanks to these open-source projects:
Jetpack Compose, Miuix KMP, Kyant Backdrop, Kyant Shapes, Kotlin Coroutines, Vico, Coil, AndroidX Navigation, DataStore, Material Icons Extended, AndroidX Lifecycle, NavigationEvent Compose

Full list available in-app: Settings → Credits.

## License

MIT License

## Author

By [JetComX](https://github.com/JetComX)
