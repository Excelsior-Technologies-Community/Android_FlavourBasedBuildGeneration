package com.ext.flavourbasedbuildtest

import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Set status bar to light theme (dark icons)
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Example: Using BuildConfig.BASE_URL and AUTH_KEY
        val baseUrl = BuildConfig.BASE_URL
        val authKey = BuildConfig.AUTH_KEY

        Log.d("MainActivity1", "Current Base URL: $baseUrl")
        Log.d("MainActivity1", "Build Flavor: ${BuildConfig.FLAVOR}")
        Log.d("MainActivity1", "Build Type: ${BuildConfig.BUILD_TYPE}")
        Log.d("MainActivity1", "Organization Auth Key: $authKey")

        // Feature flags
        Log.d("MainActivity1", "Debug Features: ${BuildConfig.ENABLE_DEBUG_FEATURES}")
        Log.d("MainActivity1", "Analytics: ${BuildConfig.ENABLE_ANALYTICS}")
        Log.d("MainActivity1", "Crash Reporting: ${BuildConfig.ENABLE_CRASH_REPORTING}")
        Log.d("MainActivity1", "Analytics ID: ${BuildConfig.ANALYTICS_ID}")
        Log.d("MainActivity1", "Firebase Project ID: ${BuildConfig.FIREBASE_PROJECT_ID}")

        // Flavor-specific code
        Log.d("MainActivity1", "Flavor Name: ${FlavorConfig.getFlavorName()}")
        Log.d("MainActivity1", "API Timeout: ${FlavorConfig.getApiTimeout()}ms")
        Log.d("MainActivity1", "Debug Enabled: ${FlavorConfig.isDebugEnabled()}")
        Log.d("MainActivity1", "Log Level: ${FlavorConfig.getLogLevel()}")

        // Network configuration
        Log.d("MainActivity1", "Connectivity Timeout: ${NetworkConfig.getConnectivityTimeout()}ms")
        Log.d("MainActivity1", "Read Timeout: ${NetworkConfig.getReadTimeout()}ms")
        Log.d("MainActivity1", "Write Timeout: ${NetworkConfig.getWriteTimeout()}ms")
        Log.d("MainActivity1", "Retry on Failure: ${NetworkConfig.shouldRetryOnFailure()}")
        Log.d("MainActivity1", "Max Retries: ${NetworkConfig.getMaxRetries()}")

        // Use baseUrl and authKey for your API calls (e.g., Retrofit)
        // val httpClient = OkHttpClient.Builder()
        //     .addInterceptor { chain ->
        //         val request = chain.request().newBuilder()
        //             .addHeader("Content-Auth", authKey)
        //             .build()
        //         chain.proceed(request)
        //     }
        //     .build()
        //
        // val retrofit = Retrofit.Builder()
        //     .baseUrl(baseUrl)
        //     .client(httpClient)
        //     .addConverterFactory(GsonConverterFactory.create())
        //     .build()
    }
}