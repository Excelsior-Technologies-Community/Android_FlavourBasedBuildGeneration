# Android Product Flavors Implementation Guide

This guide provides step-by-step instructions to implement comprehensive Android product flavors with all advanced features.

## Table of Contents
1. [Directory Structure](#directory-structure)
2. [Basic Setup](#basic-setup)
3. [Flavor Configuration](#flavor-configuration)
4. [Feature Flags](#feature-flags)
5. [Flavor-Specific Code](#flavor-specific-code)
6. [Flavor-Specific Resources](#flavor-specific-resources)
7. [Flavor-Specific Permissions](#flavor-specific-permissions)
8. [Flavor-Specific Dependencies](#flavor-specific-dependencies)
9. [ProGuard Rules](#proguard-rules)
10. [Signing Configuration](#signing-configuration)
11. [Building and Testing](#building-and-testing)
12. [Common Issues and Solutions](#common-issues-and-solutions)
13. [Testing Checklist](#testing-checklist)

---

## Use Cases

Android Product Flavors are essential for managing multiple variants of your application. Here are detailed use cases and scenarios where product flavors provide significant value:

### 1. Multi-Environment Deployment

**Scenario:** You need to deploy your app to different environments (Development, Staging, Production) with different configurations.

**Benefits:**
- **Separate API Endpoints:** Each flavor connects to its respective backend server (dev-api.example.com, staging-api.example.com, api.example.com)
- **Different Authentication Keys:** Use test keys for dev/staging and production keys for prod
- **Feature Flags:** Enable debug features, logging, and analytics only in specific environments
- **Data Isolation:** Prevent test data from mixing with production data

**Example:** A banking app uses dev flavor with mock data for testing, staging with real test servers for QA, and prod with live production servers for end users.

### 2. White-Label Applications

**Scenario:** You need to create customized versions of your app for different clients or brands.

**Benefits:**
- **Custom Branding:** Different app names, logos, colors, and themes per client
- **Unique Package Names:** Each client gets their own app on Google Play Store
- **Client-Specific Features:** Enable/disable features based on client requirements
- **Custom Resources:** Different layouts, strings, and assets per brand

**Example:** A delivery management app creates white-labeled versions for "QuickDelivery", "FastShip", and "ExpressLogistics" with different branding and feature sets.

### 3. Free vs. Premium Versions

**Scenario:** You want to offer free and paid versions of your app with different feature sets.

**Benefits:**
- **Feature Gating:** Premium features only available in paid flavor
- **Different Dependencies:** Include ad libraries only in free version
- **Separate Analytics:** Track user behavior separately for free and premium users
- **Monetization:** In-app purchases or subscriptions in premium flavor

**Example:** A photo editor app offers a free version with basic filters and ads, while the premium version includes advanced editing tools and no ads.

### 4. Regional Variants

**Scenario:** Your app needs to adapt to different regions or countries with specific requirements.

**Benefits:**
- **Localization:** Different languages, currencies, and date formats per region
- **Compliance:** Region-specific permissions and privacy policies
- **Payment Methods:** Integrate local payment gateways per region
- **Content Filtering:** Show region-specific content or features

**Example:** An e-commerce app has flavors for US (USD, Stripe), EU (EUR, PayPal), and India (INR, UPI) with region-specific payment methods and compliance.

### 5. Internal vs. External Builds

**Scenario:** You need internal builds for employees/contractors and external builds for customers.

**Benefits:**
- **Internal Tools:** Debug menus, admin panels, and test features for internal builds
- **Security:** Restrict sensitive features to internal builds only
- **Testing:** Internal builds can include testing frameworks and debugging tools
- **Access Control:** Different authentication methods for internal vs external users

**Example:** A SaaS app has an internal flavor with admin dashboard and user management tools, while the external flavor only shows customer-facing features.

### 6. OEM/Device-Specific Builds

**Scenario:** You need to create optimized versions for specific device manufacturers or carriers.

**Benefits:**
- **Device Optimization:** Different UI layouts for different screen sizes or device types
- **Carrier Integration:** Carrier-specific features (billing, promotions)
- **Hardware Features:** Enable device-specific hardware features (cameras, sensors)
- **Performance Tuning:** Different performance settings based on device capabilities

**Example:** A fitness app has optimized versions for Samsung Galaxy (using Samsung Health SDK) and Google Pixel (using Google Fit API).

### 7. A/B Testing Variants

**Scenario:** You want to test different UI/UX variations with real users.

**Benefits:**
- **Parallel Testing:** Deploy multiple variants simultaneously
- **Feature Rollout:** Test new features with a subset of users
- **Performance Comparison:** Compare different implementations
- **User Feedback:** Gather feedback on different designs

**Example:** A social media app tests two different onboarding flows - one with video tutorial (variant A) and one with interactive guide (variant B).

### 8. API Version Compatibility

**Scenario:** You need to support multiple API versions during migration.

**Benefits:**
- **Gradual Migration:** Support old and new API versions simultaneously
- **Rollback Capability:** Quickly switch back to old API if issues arise
- **Client Segmentation:** Different clients can use different API versions
- **Testing:** Test new API with beta users before full rollout

**Example:** A messaging app migrates from REST API to GraphQL, maintaining both flavors during the transition period.

### 9. Debug vs. Release Builds

**Scenario:** You need different configurations for development and production builds.

**Benefits:**
- **Debugging Tools:** Stethoco, LeakCanary, and debug menus in debug builds
- **Logging:** Verbose logging in debug, minimal in production
- **Performance:** Optimized code in release, debug-friendly in development
- **Crash Reporting:** Different crash reporting configurations

**Example:** A game app includes a debug menu with god mode, level skip, and resource cheats in debug flavor for testing.

### 10. Compliance and Regulatory Variants

**Scenario:** Your app needs to comply with different regulatory requirements (GDPR, CCPA, etc.).

**Benefits:**
- **Privacy Policies:** Different privacy notices per region
- **Data Handling:** Different data collection and storage practices
- **Consent Flows:** Region-specific consent dialogs
- **Age Restrictions:** Different features based on age requirements

**Example:** A social networking app has GDPR-compliant flavor for EU users with explicit consent dialogs, and a standard flavor for other regions.

### Real-World Examples

**Uber:**
- Uses flavors for different cities with region-specific features
- Separate builds for internal testing and public release
- Driver app vs. rider app as different product flavors

**Spotify:**
- Free vs. Premium versions as different flavors
- Region-specific builds with different content licensing
- Internal test builds for employees

**Netflix:**
- Different builds for different regions with content restrictions
- Test builds for A/B testing new features
- Device-specific optimizations

**Banking Apps:**
- Dev/Staging/Production environments
- Internal builds for bank employees with admin features
- Region-specific builds for different countries

### When to Use Product Flavors

**Use Product Flavors when:**
- You need multiple versions of your app with different configurations
- Different versions require different resources (icons, strings, layouts)
- You need to deploy to multiple environments simultaneously
- Different versions have different dependencies
- You need to maintain separate package names for the same codebase

**Consider Alternatives when:**
- Differences are only in build type (debug/release) - use build types instead
- Differences are temporary or experimental - consider feature flags
- Variations are minor and don't warrant separate builds - use runtime configuration

---

## Directory Structure

**Step 1: Create the following directory structure**

```
app/
├── build.gradle.kts                    # Main build configuration
├── proguard-dev-rules.pro              # Dev ProGuard rules
├── proguard-staging-rules.pro          # Staging ProGuard rules
├── proguard-prod-rules.pro             # Prod ProGuard rules
├── src/
│   ├── main/
│   │   ├── java/.../MainActivity.kt    # Main activity
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml  # Default layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml        # Default strings
│   │   │   │   └── colors.xml         # Default colors
│   │   │   └── mipmap-anydpi/
│   │   │       └── ic_launcher.xml    # Default icon
│   │   └── AndroidManifest.xml        # Main manifest
│   ├── dev/
│   │   ├── java/.../
│   │   │   ├── FlavorConfig.kt        # Dev-specific config
│   │   │   └── NetworkConfig.kt       # Dev-specific network
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   └── ic_launcher_foreground.xml
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml  # Dev layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml        # Dev strings
│   │   │   │   ├── colors.xml         # Dev colors
│   │   │   │   └── dimens.xml         # Dev dimensions
│   │   │   └── mipmap-anydpi/
│   │   │       ├── ic_launcher.xml
│   │   │       └── ic_launcher_round.xml
│   │   └── AndroidManifest.xml        # Dev manifest
│   ├── staging/
│   │   ├── java/.../
│   │   │   ├── FlavorConfig.kt        # Staging-specific config
│   │   │   └── NetworkConfig.kt       # Staging-specific network
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   └── ic_launcher_foreground.xml
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml  # Staging layout
│   │   │   ├── values/
│   │   │   │   ├── strings.xml        # Staging strings
│   │   │   │   ├── colors.xml         # Staging colors
│   │   │   │   └── dimens.xml         # Staging dimensions
│   │   │   └── mipmap-anydpi/
│   │   │       ├── ic_launcher.xml
│   │   │       └── ic_launcher_round.xml
│   │   └── AndroidManifest.xml        # Staging manifest
│   └── prod/
│       ├── java/.../
│       │   ├── FlavorConfig.kt        # Prod-specific config
│       │   └── NetworkConfig.kt       # Prod-specific network
│       ├── res/
│       │   ├── drawable/
│       │   │   ├── ic_launcher_background.xml
│       │   │   └── ic_launcher_foreground.xml
│       │   ├── layout/
│       │   │   └── activity_main.xml  # Prod layout
│       │   ├── values/
│       │   │   ├── strings.xml        # Prod strings
│       │   │   ├── colors.xml         # Prod colors
│       │   │   └── dimens.xml         # Prod dimensions
│       │   └── mipmap-anydpi/
│       │       ├── ic_launcher.xml
│       │       └── ic_launcher_round.xml
│       └── AndroidManifest.xml        # Prod manifest
└── build/
    └── outputs/
        ├── apk/
        │   ├── dev/debug/
        │   ├── dev/release/
        │   ├── staging/debug/
        │   ├── staging/release/
        │   ├── prod/debug/
        │   └── prod/release/
        └── bundle/
            ├── devRelease/
            ├── stagingRelease/
            └── prodRelease/
```

---

## Overview

Product flavors allow you to create different versions of your app with:
- Different configurations (API URLs, keys, etc.)
- Different resources (icons, colors, strings, layouts)
- Different code implementations
- Different permissions
- Different dependencies

---

## Basic Setup

**Step 2: Enable BuildConfig**

In `app/build.gradle.kts`, add:

```kotlin
android {
    buildFeatures {
        buildConfig = true
    }
}
```

**Step 3: Add Flavor Dimension**

```kotlin
android {
    flavorDimensions += "environment"
}
```

---

## Flavor Configuration

**Step 4: Define Product Flavors**

In `app/build.gradle.kts`:

```kotlin
productFlavors {
    create("dev") {
        dimension = "environment"
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        buildConfigField("String", "AUTH_KEY", "\"dev_auth_key_xyz123\"")
        buildConfigField("boolean", "ENABLE_DEBUG_FEATURES", "true")
        buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
        buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "false")
        buildConfigField("String", "ANALYTICS_ID", "\"dev_analytics_id\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"dev-project-id\"")
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        applicationIdSuffix = ".dev"
        versionNameSuffix = "-dev"
    }
    create("staging") {
        dimension = "environment"
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        buildConfigField("String", "AUTH_KEY", "\"staging_auth_key_abc456\"")
        buildConfigField("boolean", "ENABLE_DEBUG_FEATURES", "true")
        buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
        buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
        buildConfigField("String", "ANALYTICS_ID", "\"staging_analytics_id\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"staging-project-id\"")
        minSdk = 28
        targetSdk = 36
        versionCode = 2
        applicationIdSuffix = ".staging"
        versionNameSuffix = "-staging"
    }
    create("prod") {
        dimension = "environment"
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        buildConfigField("String", "AUTH_KEY", "\"prod_auth_key_def789\"")
        buildConfigField("boolean", "ENABLE_DEBUG_FEATURES", "false")
        buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
        buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
        buildConfigField("String", "ANALYTICS_ID", "\"prod_analytics_id\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"prod-project-id\"")
        minSdk = 30
        targetSdk = 36
        versionCode = 3
    }
}
```

---

## Feature Flags

**Step 5: Use Feature Flags in Code**

In your Activity/Fragment:

```kotlin
val baseUrl = BuildConfig.BASE_URL
val authKey = BuildConfig.AUTH_KEY
val enableDebug = BuildConfig.ENABLE_DEBUG_FEATURES
val enableAnalytics = BuildConfig.ENABLE_ANALYTICS
val analyticsId = BuildConfig.ANALYTICS_ID
val firebaseProjectId = BuildConfig.FIREBASE_PROJECT_ID

Log.d("MainActivity", "Flavor: ${BuildConfig.FLAVOR}")
Log.d("MainActivity", "Base URL: $baseUrl")
Log.d("MainActivity", "Auth Key: $authKey")
```

---

## Flavor-Specific Code

**Step 6: Create Flavor-Specific Classes**

Create directory structure:
```
app/src/dev/java/com/ext/flavourbasedbuildtest/
app/src/staging/java/com/ext/flavourbasedbuildtest/
app/src/prod/java/com/ext/flavourbasedbuildtest/
```

**Step 7: Add Flavor-Specific Implementation**

**Dev - `app/src/dev/java/com/ext/flavourbasedbuildtest/FlavorConfig.kt`:**
```kotlin
package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Development"
    fun getApiTimeout(): Long = 30000L
    fun isDebugEnabled(): Boolean = true
    fun getLogLevel(): String = "VERBOSE"
}
```

**Staging - `app/src/staging/java/com/ext/flavourbasedbuildtest/FlavorConfig.kt`:**
```kotlin
package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Staging"
    fun getApiTimeout(): Long = 15000L
    fun isDebugEnabled(): Boolean = true
    fun getLogLevel(): String = "DEBUG"
}
```

**Prod - `app/src/prod/java/com/ext/flavourbasedbuildtest/FlavorConfig.kt`:**
```kotlin
package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Production"
    fun getApiTimeout(): Long = 10000L
    fun isDebugEnabled(): Boolean = false
    fun getLogLevel(): String = "ERROR"
}
```

**Step 8: Use in Code**

```kotlin
Log.d("MainActivity", "Flavor Name: ${FlavorConfig.getFlavorName()}")
Log.d("MainActivity", "API Timeout: ${FlavorConfig.getApiTimeout()}ms")
```

---

## Flavor-Specific Resources

**Step 9: Create Resource Directories**

```
app/src/dev/res/values/
app/src/staging/res/values/
app/src/prod/res/values/
```

**Step 10: Add Flavor-Specific Strings**

**Dev - `app/src/dev/res/values/strings.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Dev App</string>
    <string name="welcome_message">Welcome to Development Environment</string>
    <string name="debug_mode">Debug Mode: ON</string>
</resources>
```

**Staging - `app/src/staging/res/values/strings.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Staging App</string>
    <string name="welcome_message">Welcome to Staging Environment</string>
    <string name="debug_mode">Debug Mode: ON</string>
</resources>
```

**Prod - `app/src/prod/res/values/strings.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Prod App</string>
    <string name="welcome_message">Welcome to Production Environment</string>
    <string name="debug_mode">Debug Mode: OFF</string>
</resources>
```

**Step 11: Add Flavor-Specific Colors**

**Dev - `app/src/dev/res/values/colors.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_color">#FF0000</color>
    <color name="secondary_color">#000000</color>
    <color name="background_color">#FFFFFF</color>
</resources>
```

**Staging - `app/src/staging/res/values/colors.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_color">#FFA500</color>
    <color name="secondary_color">#000000</color>
    <color name="background_color">#FFFFFF</color>
</resources>
```

**Prod - `app/src/prod/res/values/colors.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary_color">#E53935</color>
    <color name="secondary_color">#FFFFFF</color>
    <color name="background_color">#FFFFFF</color>
</resources>
```

**Step 12: Add Flavor-Specific Dimensions**

**Dev - `app/src/dev/res/values/dimens.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="text_size_large">32sp</dimen>
    <dimen name="text_size_medium">24sp</dimen>
    <dimen name="margin_large">24dp</dimen>
    <dimen name="button_height">56dp</dimen>
</resources>
```

**Staging - `app/src/staging/res/values/dimens.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="text_size_large">28sp</dimen>
    <dimen name="text_size_medium">20sp</dimen>
    <dimen name="margin_large">20dp</dimen>
    <dimen name="button_height">48dp</dimen>
</resources>
```

**Prod - `app/src/prod/res/values/dimens.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="text_size_large">24sp</dimen>
    <dimen name="text_size_medium">18sp</dimen>
    <dimen name="margin_large">16dp</dimen>
    <dimen name="button_height">48dp</dimen>
</resources>
```

**Step 13: Add Flavor-Specific Layouts**

**Dev - `app/src/dev/res/layout/activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_color">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/welcome_message"
        android:textColor="@color/primary_color"
        android:textSize="@dimen/text_size_medium"
        app:layout_constraintBottom_toTopOf="@+id/button1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Dev Button 1"
        app:layout_constraintBottom_toTopOf="@+id/button2"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Dev Button 2"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button1" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Staging - `app/src/staging/res/layout/activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_color">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/welcome_message"
        android:textColor="@color/primary_color"
        android:textSize="@dimen/text_size_medium"
        app:layout_constraintBottom_toTopOf="@+id/button1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Staging Button 1"
        app:layout_constraintBottom_toTopOf="@+id/button2"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Staging Button 2"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button1" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Prod - `app/src/prod/res/layout/activity_main.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_color">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/welcome_message"
        android:textColor="@color/primary_color"
        android:textSize="@dimen/text_size_medium"
        app:layout_constraintBottom_toTopOf="@+id/button1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Prod Button 1"
        app:layout_constraintBottom_toTopOf="@+id/button2"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="@dimen/button_height"
        android:text="Prod Button 2"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button1" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Step 14: Add Flavor-Specific Icons**

Create drawable directories:
```
app/src/dev/res/drawable/
app/src/staging/res/drawable/
app/src/prod/res/drawable/
```

Add `ic_launcher_background.xml` and `ic_launcher_foreground.xml` with different colors for each flavor.

---

## Flavor-Specific Permissions

**Step 15: Create AndroidManifest for Each Flavor**

**Dev - `app/src/dev/AndroidManifest.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
</manifest>
```

**Staging - `app/src/staging/AndroidManifest.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.CAMERA" />
</manifest>
```

**Prod - `app/src/prod/AndroidManifest.xml`:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Minimal permissions -->
</manifest>
```

---

## Flavor-Specific Dependencies

**Step 16: Add Flavor-Specific Dependencies**

In `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Flavor-specific dependencies
    devImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
    prodImplementation("com.google.firebase:firebase-crashlytics:18.4.0")
    stagingImplementation("com.google.firebase:firebase-crashlytics:18.4.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

---

## ProGuard Rules

**Step 17: Create ProGuard Rules for Each Flavor**

**Dev - `app/proguard-dev-rules.pro`:**
```pro
-keep class com.ext.flavourbasedbuildtest.** { *; }
-dontwarn **
-verbose
```

**Staging - `app/proguard-staging-rules.pro`:**
```pro
-keep class com.ext.flavourbasedbuildtest.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
```

**Prod - `app/proguard-prod-rules.pro`:**
```pro
-keepattributes *Annotation*
-keep class com.ext.flavourbasedbuildtest.model.** { *; }
-keep class com.ext.flavourbasedbuildtest.api.** { *; }
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
```

**Step 18: Configure ProGuard in build.gradle.kts**

```kotlin
android {
    // Flavor-specific ProGuard rules
    productFlavors.forEach { flavor ->
        when (flavor.name) {
            "dev" -> flavor.proguardFiles("proguard-dev-rules.pro")
            "staging" -> flavor.proguardFiles("proguard-staging-rules.pro")
            "prod" -> flavor.proguardFiles("proguard-prod-rules.pro")
        }
    }
}
```

---

## Signing Configuration

**Step 19: Generate Keystore**

```bash
keytool -genkey -v -keystore your_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias your_key_alias
```

**Step 20: Configure Signing in build.gradle.kts**

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../your_keystore.jks")
            storePassword = "your_keystore_password"
            keyAlias = "your_key_alias"
            keyPassword = "your_key_password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Building and Testing

**Step 21: Build All Debug APKs**

```bash
./gradlew assembleDebug
```

This generates:
- `app/build/outputs/apk/dev/debug/app-dev-debug.apk`
- `app/build/outputs/apk/staging/debug/app-staging-debug.apk`
- `app/build/outputs/apk/prod/debug/app-prod-debug.apk`

**Step 22: Build All Release APKs**

```bash
./gradlew assembleRelease
```

This generates:
- `app/build/outputs/apk/dev/release/app-dev-release.apk`
- `app/build/outputs/apk/staging/release/app-staging-release.apk`
- `app/build/outputs/apk/prod/release/app-prod-release.apk`

**Step 23: Build All Signed AAB Files**

```bash
./gradlew bundleRelease
```

This generates:
- `app/build/outputs/bundle/devRelease/app-dev-release.aab`
- `app/build/outputs/bundle/stagingRelease/app-staging-release.aab`
- `app/build/outputs/bundle/prodRelease/app-prod-release.aab`

**Step 24: Build Specific Flavor**

```bash
# Build dev debug
./gradlew assembleDevDebug

# Build staging debug
./gradlew assembleStagingDebug

# Build prod debug
./gradlew assembleProdDebug

# Build dev release
./gradlew assembleDevRelease

# Build staging release
./gradlew assembleStagingRelease

# Build prod release
./gradlew assembleProdRelease

# Build dev AAB
./gradlew bundleDevRelease

# Build staging AAB
./gradlew bundleStagingRelease

# Build prod AAB
./gradlew bundleProdRelease
```

**Step 25: Install Specific Flavor**

```bash
# Install dev
adb install app/build/outputs/apk/dev/debug/app-dev-debug.apk

# Install staging
adb install app/build/outputs/apk/staging/debug/app-staging-debug.apk

# Install prod
adb install app/build/outputs/apk/prod/debug/app-prod-debug.apk

# Install release versions
adb install app/build/outputs/apk/dev/release/app-dev-release.apk
adb install app/build/outputs/apk/staging/release/app-staging-release.apk
adb install app/build/outputs/apk/prod/release/app-prod-release.apk
```

**Step 26: Uninstall Specific Flavor**

```bash
# Uninstall dev
adb uninstall com.ext.flavourbasedbuildtest.dev

# Uninstall staging
adb uninstall com.ext.flavourbasedbuildtest.staging

# Uninstall prod
adb uninstall com.ext.flavourbasedbuildtest
```

**Step 27: Clean Build**

```bash
# Clean all builds
./gradlew clean

# Clean specific flavor
./gradlew cleanDevDebug
./gradlew cleanStagingDebug
./gradlew cleanProdDebug
```

**Step 28: View Available Tasks**

```bash
# List all available tasks
./gradlew tasks

# List all build tasks
./gradlew tasks --group=build

# List all install tasks
./gradlew tasks --group=install
```

**Step 29: Gradle Commands**

```bash
# Build with more information
./gradlew assembleDebug --info

# Build with debug output
./gradlew assembleDebug --debug

# Build with stacktrace on error
./gradlew assembleDebug --stacktrace

# Build offline (no network)
./gradlew assembleDebug --offline

# Build with parallel execution
./gradlew assembleDebug --parallel

# Build with specific JVM options
./gradlew assembleDebug -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxPermSize=512m"
```

**Step 30: ADB Commands**

```bash
# List connected devices
adb devices

# List installed packages
adb shell pm list packages

# List packages with package name filter
adb shell pm list packages | grep flavourbasedbuildtest

# Get app info
adb shell dumpsys package com.ext.flavourbasedbuildtest.dev

# Clear app data
adb shell pm clear com.ext.flavourbasedbuildtest.dev

# Start app
adb shell am start -n com.ext.flavourbasedbuildtest.dev/.MainActivity

# Force stop app
adb shell am force-stop com.ext.flavourbasedbuildtest.dev

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# Record screen
adb shell screenrecord /sdcard/demo.mp4
adb pull /sdcard/demo.mp4

# View logs
adb logcat

# Filter logs by tag
adb logcat -s MainActivity1

# Clear logcat
adb logcat -c
```

**Step 31: Verify Build Configuration**

```bash
# Check BuildConfig fields in APK
aapt dump badging app/build/outputs/apk/dev/debug/app-dev-debug.apk

# Check manifest
aapt dump xmltree app/build/outputs/apk/dev/debug/app-dev-debug.apk AndroidManifest.xml

# Check resources
aapt dump resources app/build/outputs/apk/dev/debug/app-dev-debug.apk
```

**Step 32: Common Issues and Solutions**

**Issue: BuildConfig fields not found**
```kotlin
// Solution: Enable buildConfig in build.gradle.kts
buildFeatures {
    buildConfig = true
}
```

**Issue: Flavor-specific resources not loading**
```bash
# Solution: Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

**Issue: Staging build showing dev config**
```bash
# Solution: Clean build to clear cached data
./gradlew clean
./gradlew assembleDebug
```

**Issue: Keystore not found**
```kotlin
// Solution: Update keystore path in build.gradle.kts
storeFile = file("../your_keystore.jks")  // Check path is correct
```

**Issue: Buttons not found in prod layout**
```kotlin
// Solution: Use safe calls or add buttons to all layouts
val button1 = findViewById<Button>(R.id.button1)
button1?.visibility = View.GONE
```

**Step 33: Testing Checklist**

- [ ] Build all debug APKs successfully
- [ ] Build all release APKs successfully
- [ ] Build all AAB files successfully
- [ ] Install dev APK and verify:
  - [ ] App name is "Dev App"
  - [ ] Icon is black/white
  - [ ] BASE_URL is correct
  - [ ] AUTH_KEY is correct
  - [ ] Buttons are hidden
  - [ ] Debug features enabled
  - [ ] Analytics disabled
- [ ] Install staging APK and verify:
  - [ ] App name is "Staging App"
  - [ ] Icon is white/black
  - [ ] BASE_URL is correct
  - [ ] AUTH_KEY is correct
  - [ ] Buttons are visible
  - [ ] Debug features enabled
  - [ ] Analytics enabled
- [ ] Install prod APK and verify:
  - [ ] App name is "Prod App"
  - [ ] Icon is red/white
  - [ ] BASE_URL is correct
  - [ ] AUTH_KEY is correct
  - [ ] Buttons are hidden
  - [ ] Debug features disabled
  - [ ] Analytics enabled
- [ ] Verify all three can be installed simultaneously
- [ ] Verify logcat shows correct flavor information
- [ ] Test AAB files can be uploaded to Play Store console

---

## Dynamic UI Control

**Step 34: Code-Based Visibility Control**

In your Activity:

```kotlin
val button1 = findViewById<Button>(R.id.button1)
val button2 = findViewById<Button>(R.id.button2)

if (BuildConfig.FLAVOR == "staging") {
    button1.visibility = View.VISIBLE
    button2.visibility = View.VISIBLE
} else {
    button1.visibility = View.GONE
    button2.visibility = View.GONE
}
```

---

## Summary

This implementation provides:
- ✅ Different API URLs and auth keys per flavor
- ✅ Feature flags for debug, analytics, crash reporting
- ✅ Different app names, icons, colors per flavor
- ✅ Different permissions per flavor
- ✅ Different SDK versions and version codes per flavor
- ✅ Flavor-specific code implementations
- ✅ Flavor-specific resources (strings, colors, dimensions, layouts)
- ✅ Flavor-specific dependencies
- ✅ Different ProGuard rules per flavor
- ✅ Firebase project configuration per flavor
- ✅ Signed AAB generation for Play Store
- ✅ Dynamic UI control via code

All three builds can be installed simultaneously and will have their own unique configurations.

---

## License

This guide is provided as-is for educational and development purposes. You are free to use, modify, and distribute this guide for your projects.

Permission is hereby granted, free of charge, to any person obtaining a copy of this guide and associated documentation files (the "Guide"), to deal in the Guide without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Guide, and to permit persons to whom the Guide is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Guide.

THE GUIDE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE GUIDE OR THE USE OR OTHER DEALINGS IN THE GUIDE.
