plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.ext.flavourbasedbuildtest"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.ext.flavourbasedbuildtest"
        minSdk = 26
        targetSdk = 36
        
        // Auto versioning: Use CI version code or timestamp-based for local builds
        val ciVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull()
        versionCode = ciVersionCode ?: (System.currentTimeMillis() / 1000).toInt()
        versionName = System.getenv("VERSION_NAME") ?: "1.0.${versionCode}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "../your_keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "123456"
            keyAlias = System.getenv("KEY_ALIAS") ?: "your_key_alias"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "123456"
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    flavorDimensions += "environment"

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

    // Flavor-specific ProGuard rules
    productFlavors.forEach { flavor ->
        when (flavor.name) {
            "dev" -> flavor.proguardFiles("proguard-dev-rules.pro")
            "staging" -> flavor.proguardFiles("proguard-staging-rules.pro")
            "prod" -> flavor.proguardFiles("proguard-prod-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Flavor-specific dependencies
    // Uncomment and add actual library versions when needed
    // devImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
    // prodImplementation("com.google.firebase:firebase-crashlytics:18.4.0")
    // stagingImplementation("com.google.firebase:firebase-crashlytics:18.4.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}