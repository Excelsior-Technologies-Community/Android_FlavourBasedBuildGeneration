package com.ext.flavourbasedbuildtest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : BaseMainActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "DevPrefs"
    private val KEY_VERBOSE_LOGS = "verbose_logs"
    private val KEY_NETWORK_INSPECTOR = "network_inspector"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setupDevUI()
    }

    private fun setupDevUI() {
        // Setup dev-specific UI elements
        val verboseLogsSwitch = binding.verboseLogsSwitch
        val networkInspectorSwitch = binding.networkInspectorSwitch
        val testApiButton = binding.testApiButton
        val clearCacheButton = binding.clearCacheButton
        val memoryUsageText = binding.memoryUsageText
        val connectionStatus = binding.connectionStatus
        val responseTimeText = binding.responseTimeText

        // Load saved switch states
        verboseLogsSwitch.isChecked = sharedPreferences.getBoolean(KEY_VERBOSE_LOGS, false)
        networkInspectorSwitch.isChecked = sharedPreferences.getBoolean(KEY_NETWORK_INSPECTOR, false)

        // Start live memory updates
        startLiveMemoryUpdates(memoryUsageText)

        // Switch listeners - save to SharedPreferences
        verboseLogsSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("DevUI", "Verbose Logs: ${if (isChecked) "Enabled" else "Disabled"}")
            sharedPreferences.edit().putBoolean(KEY_VERBOSE_LOGS, isChecked).apply()
            Toast.makeText(this, "Verbose Logs ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        networkInspectorSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d("DevUI", "Network Inspector: ${if (isChecked) "Enabled" else "Disabled"}")
            sharedPreferences.edit().putBoolean(KEY_NETWORK_INSPECTOR, isChecked).apply()
            Toast.makeText(this, "Network Inspector ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Button listeners
        testApiButton.setOnClickListener {
            Log.d("DevUI", "Testing API Connection")
            connectionStatus.text = "Testing..."
            connectionStatus.setTextColor(getColor(android.R.color.darker_gray))

            // Simulate API test
            connectionStatus.postDelayed({
                connectionStatus.text = "Connected"
                connectionStatus.setTextColor(getColor(android.R.color.holo_green_dark))
                responseTimeText.text = "Response Time: 125ms"
                Toast.makeText(this, "API Connection Successful", Toast.LENGTH_SHORT).show()
            }, 1500)
        }

        clearCacheButton.setOnClickListener {
            Log.d("DevUI", "Clearing Cache")
            clearCache()
            Toast.makeText(this, "Cache Cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLiveMemoryUpdates(memoryUsageText: TextView) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val activityManager = getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val runtime = Runtime.getRuntime()

        val runnable =
            object : Runnable {
                override fun run() {
                    // Get JVM memory info
                    val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                    val maxMemory = runtime.maxMemory() / (1024 * 1024)
                    val totalMemory = runtime.totalMemory() / (1024 * 1024)

                    // Get device memory info
                    val memoryInfo = android.app.ActivityManager.MemoryInfo()
                    activityManager.getMemoryInfo(memoryInfo)
                    val totalDeviceMemory = memoryInfo.totalMem / (1024 * 1024)
                    val availableDeviceMemory = memoryInfo.availMem / (1024 * 1024)
                    val usedDeviceMemory = totalDeviceMemory - availableDeviceMemory

                    memoryUsageText.text =
                        """
                        App Memory: $usedMemory MB / $maxMemory MB (Allocated: $totalMemory MB)
                        Device Memory: $usedDeviceMemory MB / $totalDeviceMemory MB (Available: $availableDeviceMemory MB)
                        """.trimIndent()

                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        handler.post(runnable)
    }

    private fun clearCache() {
        // Clear SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Reset switches
        binding.verboseLogsSwitch.isChecked = false
        binding.networkInspectorSwitch.isChecked = false

        // Clear app cache
        try {
            val cacheDir = cacheDir
            val appCache = cacheDir.listFiles()
            if (appCache != null) {
                for (file in appCache) {
                    if (file.isDirectory) {
                        deleteDir(file)
                    } else {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DevUI", "Error clearing cache: ${e.message}")
        }

        // Update memory usage after clearing
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        binding.memoryUsageText.text = "Memory Usage: $usedMemory MB / $maxMemory MB"
    }

    private fun deleteDir(dir: java.io.File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(java.io.File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
        }
        return dir.delete()
    }

    // INTENTIONAL CRASH METHOD FOR TESTING
    fun triggerIntentionalCrash() {
        // This method is called by UI tests to verify crash detection
        throw RuntimeException("DEV FLAVOR CRASH: Intentional crash in Dev MainActivity for CI/CD testing")
    }
}
