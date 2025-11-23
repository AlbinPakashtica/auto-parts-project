This is a Kotlin Multiplatform project targeting Android, iOS, Web, Server.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/androidApp](./androidApp) Contains the Android Application, it is where you write the android specific code, 
  and what calls both the shared code and the shared compose UI.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

* [/webApp](./webApp) contains web React application. It uses the Kotlin/JS library produced
  by the [shared](./shared) module.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
1. Install [Node.js](https://nodejs.org/en/download) (which includes `npm`)
2. Build Kotlin/JS shared code:
   - on macOS/Linux
     ```shell
     ./gradlew :shared:jsBrowserDevelopmentLibraryDistribution
     ```
   - on Windows
     ```shell
     .\gradlew.bat :shared:jsBrowserDevelopmentLibraryDistribution
     ```
3. Build and run the web application
   ```shell
   npm install
   npm run start
   ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

### **1\. The High-Level Concept**

In this monorepo, you will have a "Shared" core.

* **100% Shared (The Kernel):** Data models (e.g., CarPart, User), API Client, Caching/Database, Business Logic. This lives in the :shared module.
* **Mobile Shared (UI):** The UI screens and ViewModels used by Android and iOS. This lives in the :composeApp module.
* **Web Specific:** A separate React-based web application that consumes the logic from :shared but implements its own UI.
* **Platform Specific:** The "Shells" (androidApp, iosApp) that bootstrap the application on each device.

### **2\. Directory Structure**
Here is the hierarchy based on your specific file tree:
```

/car-parts-project  
├── build.gradle.kts          // Root build file  
├── settings.gradle.kts       // Defines modules  
├── gradle.properties         // Versions and JVM settings  
│  
├── /androidApp               // ANDROID ENTRY POINT (Shell)  
│   ├── build.gradle.kts  
│   └── /src/main             // AndroidManifest.xml and MainActivity  
│  
├── /composeApp               // SHARED MOBILE UI  
│   ├── build.gradle.kts  
│   └── /src  
│       ├── /commonMain       // Shared Compose Screens & ViewModels  
│       ├── /commonTest  
│       ├── /androidMain      // Android-specific UI overrides  
│       └── /iosMain          // iOS-specific UI overrides  
│  
├── /shared                   // CORE LOGIC (No UI)  
│   ├── build.gradle.kts  
│   └── /src  
│       ├── /commonMain       // API Client, Database, Models  
│       ├── /commonTest  
│       ├── /androidMain  
│       ├── /iosMain  
│       ├── /jsMain           // Bridge for the Web App  
│       └── /jvmMain          // Shared logic for Server/Desktop  
│  
├── /webApp                   // REACT WEB APP  
│   ├── package.json  
│   ├── /src  
│   │   └── /components       // React components (CarPartList, Header)  
│   └── /kotlin               // Bridge to the :shared module  
│  
├── /iosApp                   // IOS ENTRY POINT (Shell)  
│   ├── iosApp.xcodeproj  
│   └── /iosApp  
│       └── ContentView.swift // Entry point loading Compose UI  
│  
├── /server                   // BACKEND API  
│   ├── build.gradle.kts  
│   └── /src  
│       ├── /main             // Ktor Server code  
│       └── /test  
│  
└── /gradle                   // Gradle wrapper files

```

### **3\. Module Breakdown**

#### **A. The shared (Core Module)**

This is the brain of your application. It contains no UI code.

* **Technology:** Pure Kotlin Multiplatform.
* **Key Folders:**
    * jsMain: Exposes your Kotlin models to JavaScript for the React app.
    * jvmMain: Shared logic specific to the Java Virtual Machine (useful for the Server or Android-specific optimisations).
* **Responsibility:**
    * **Models:** data class CarPart, enum class EngineType.
    * **Networking:** Ktor Client configuration and API calls.
    * **Storage:** SQLDelight database or DataStore.

#### **B. The composeApp (Mobile UI Module)**

This module consumes :shared and draws the UI for mobile devices. It is a **Library** module used by the Android and iOS apps.

* **Technology:** Jetpack Compose Multiplatform.
* **Responsibility:**
    * All UI Screens (PartListScreen, CheckoutScreen).
    * Navigation logic.
    * State Management (ViewModels).

#### **C. The androidApp (Android Shell)**

This is the native entry point for Android.

* **Responsibility:**
    * Applies the com.android.application plugin.
    * Contains the AndroidManifest.xml.
    * Launches the MainActivity which sets the content to the Main Composable defined in composeApp.

#### **D. The webApp (React Module)**

A specific web implementation that uses standard DOM elements.

* **Technology:** Kotlin/JS wrappers for React (kotlin-react).
* **Connection:** It imports the compiled JS library generated by the :shared module (jsMain) to reuse models and API logic.

#### **E. The server (Backend)**

* **Technology:** Ktor Server.
* **Benefit:** Shares the exact CarPart data class with :shared.

### **4\. Recommended KMP Libraries (Enterprise Grade)**

For a complex E-Commerce app, you need libraries that ensure stability and testability.

#### **Core & Architecture**

| Capability | Library | Why? |
| :---- | :---- | :---- |
| **Networking** | **Ktor Client** | The standard for KMP. Highly configurable. |
| **Database** | **SQLDelight** | Type-safe SQL. Generates Kotlin code from .sq files. Superior to Room for multiplatform stability (especially JS/Native). |
| **DI** | **Koin** | Lightweight dependency injection. Essential for decoupling your API from your UI. |
| **Navigation** | **Voyager** | Designed specifically for Compose Multiplatform. Simpler than Decompose, but robust enough for complex flows. |
| **Async** | **Coroutines & Flow** | Built-in Kotlin async handling. |
| **Dates** | **kotlinx-datetime** | Essential for handling order timestamps consistently across timezones. |

#### **Testing (Crucial for Complex Apps)**

| Capability | Library | Why? |
| :---- | :---- | :---- |
| **Unit Tests** | **kotlin.test** | The standard assertion library. |
| **Mocking** | **Mokkery** or **MockK** | *Note:* MockK is powerful but can be tricky on Native/JS. **Mokkery** is a newer, KSP-based alternative gaining popularity for KMP. |
| **Flow Tests** | **Turbine** | Essential for testing your ViewModels and Data Flows. |
| **UI Tests** | **Compose UI Test** | Allows you to write UI tests that run on JVM (fast) and verify UI logic. |

#### **Consistency & Quality**

| Capability | Library | Why? |
| :---- | :---- | :---- |
| **Linting** | **Ktlint** (via Spotless) | Enforces code style. Use the **Spotless Gradle Plugin** to auto-format code before commits. |
| **Static Analysis** | **Detekt** | Finds code smells (complexity, too many parameters, etc.). |
| **Image Loading** | **Coil 3.0** | The new standard for KMP image loading. |

### **5\. Implementation Strategy**

#### **Step 1: The "Shared" Kernel**

Start by implementing your CarPart model and your CarPartApi interface in the :shared module.


```kotlin
// shared/src/commonMain/kotlin/models/CarPart.kt  
@Serializable  
data class CarPart(  
val id: String,  
val name: String,  
val price: Double  
)
```
#### **Step 2: The Web Bridge**

In your webApp, you will use these models. Kotlin/JS translates them so you can use them in React components.

```kotlin
// webApp/src/main/kotlin/App.kt  
val part = CarPart(id = "1", name = "Brake Pad", price = 45.00)  
// Render using React DSL...
```
#### **Step 3: The Mobile UI**

In composeApp, you build the visual cards.

```kotlin
// composeApp/src/commonMain/kotlin/PartCard.kt  
@Composable  
fun PartCard(part: CarPart) {  
// Use Compose UI components  
}
```