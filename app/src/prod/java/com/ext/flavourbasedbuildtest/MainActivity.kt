package com.ext.flavourbasedbuildtest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : BaseMainActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "ProdPrefs"
    private val KEY_NOTIFICATIONS = "notifications_enabled"
    private val KEY_THEME = "app_theme"
    private val KEY_LANGUAGE = "app_language"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setupProdUI()
    }

    private fun setupProdUI() {
        // Setup prod-specific UI elements
        val viewDetailsButton = binding.viewDetailsButton
        val settingsButton = binding.settingsButton
        val activeUsersText = binding.activeUsersText
        val revenueText = binding.revenueText
        val conversionText = binding.conversionText
        val satisfactionText = binding.satisfactionText
        val uptimeText = binding.uptimeText
        val responseTimeText = binding.responseTimeText
        val errorRateText = binding.errorRateText

        // Button listeners
        viewDetailsButton.setOnClickListener {
            Log.d("ProdUI", "Viewing Details")
            showDetailsDialog()
            // INTENTIONAL CRASH: Null pointer exception to verify crash reporting
            val nullString: String? = null
            nullString!!.length
        }

        settingsButton.setOnClickListener {
            Log.d("ProdUI", "Opening Settings")
            showSettingsDialog()
        }

        // Simulate real-time updates
        updateProdMetrics()
    }

    private fun updateProdMetrics() {
        val activeUsersText = findViewById<TextView>(R.id.activeUsersText)
        val revenueText = findViewById<TextView>(R.id.revenueText)

        // Simulate periodic updates
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable =
            object : Runnable {
                override fun run() {
                    // Update metrics with slight variations
                    val currentUsers = 45678 + (Math.random() * 100).toInt()
                    val currentRevenue = 128 + (Math.random() * 5).toInt()

                    activeUsersText.text = String.format("%,d", currentUsers)
                    revenueText.text = "$${currentRevenue}K"

                    handler.postDelayed(this, 5000)
                }
            }
        handler.postDelayed(runnable, 5000)
    }

    private fun showDetailsDialog() {
        val details =
            """
            Detailed Analytics Dashboard
            =============================
            
            User Metrics:
            - Active Users: 45,678
            - Daily Active: 12,345
            - Weekly Active: 28,901
            - Monthly Active: 45,678
            
            Revenue Metrics:
            - Daily Revenue: $128K
            - Weekly Revenue: $896K
            - Monthly Revenue: $3.8M
            - Yearly Revenue: $45.6M
            
            Conversion Metrics:
            - Sign-up Rate: 4.2%
            - Purchase Rate: 2.8%
            - Retention Rate: 68.5%
            
            System Health:
            - Uptime: 99.98%
            - Response Time: 125ms
            - Error Rate: 0.02%
            - Server Load: 42%
            
            User Satisfaction:
            - NPS Score: 72
            - App Rating: 4.7/5
            - Support Tickets: 23
            """.trimIndent()

        val scrollView = ScrollView(this)
        val textView =
            TextView(this).apply {
                text = details
                setPadding(40, 20, 40, 20)
                textSize = 12f
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            }
        scrollView.addView(textView)

        MaterialAlertDialogBuilder(this)
            .setTitle("Detailed Analytics")
            .setView(scrollView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSettingsDialog() {
        val settingsLayout =
            android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(50, 40, 50, 40)
            }

        // Notifications setting
        val notificationsText =
            TextView(this).apply {
                text = "Enable Notifications"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 20, 0, 10)
            }
        val notificationsSwitch =
            android.widget.Switch(this).apply {
                isChecked = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true)
            }
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply()
        }

        // Theme setting with dropdown
        val themeText =
            TextView(this).apply {
                text = "App Theme"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 30, 0, 10)
            }
        val themes = arrayOf("Light", "Dark", "System Default")
        val themeSpinner =
            android.widget.Spinner(this).apply {
                adapter =
                    android.widget.ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        themes,
                    )
                val savedTheme = sharedPreferences.getString(KEY_THEME, "Light")
                setSelection(themes.indexOf(savedTheme ?: "Light"))
            }

        // Language setting with dropdown
        val languageText =
            TextView(this).apply {
                text = "Language"
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(0, 30, 0, 10)
            }
        val languages = arrayOf("English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean")
        val languageSpinner =
            android.widget.Spinner(this).apply {
                adapter =
                    android.widget.ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        languages,
                    )
                val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, "English")
                setSelection(languages.indexOf(savedLanguage ?: "English"))
            }

        settingsLayout.addView(notificationsText)
        settingsLayout.addView(notificationsSwitch)
        settingsLayout.addView(themeText)
        settingsLayout.addView(themeSpinner)
        settingsLayout.addView(languageText)
        settingsLayout.addView(languageSpinner)

        MaterialAlertDialogBuilder(this)
            .setTitle("Settings")
            .setView(settingsLayout)
            .setPositiveButton("Save") { _, _ ->
                sharedPreferences.edit()
                    .putString(KEY_THEME, themes[themeSpinner.selectedItemPosition])
                    .putString(KEY_LANGUAGE, languages[languageSpinner.selectedItemPosition])
                    .apply()
                Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
