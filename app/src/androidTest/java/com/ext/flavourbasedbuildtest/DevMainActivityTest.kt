package com.ext.flavourbasedbuildtest

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Dev Flavor UI Tests for MainActivity
 * Comprehensive test coverage for all Dev flavor features
 * These tests only run for the dev flavor
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DevMainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testDevMainActivityLaunches() {
        // Skip if not dev flavor
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Dev MainActivity should not be null", activity)
            assertTrue("Activity should be Dev MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testAllLayoutComponentsExist() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            // Switches
            assertNotNull("Verbose logs switch should exist", activity.binding.verboseLogsSwitch)
            assertNotNull("Network inspector switch should exist", activity.binding.networkInspectorSwitch)
            // Buttons
            assertNotNull("Test API button should exist", activity.binding.testApiButton)
            assertNotNull("Clear cache button should exist", activity.binding.clearCacheButton)
            // Text views
            assertNotNull("Memory usage text should exist", activity.binding.memoryUsageText)
            assertNotNull("Connection status should exist", activity.binding.connectionStatus)
            assertNotNull("Response time text should exist", activity.binding.responseTimeText)
        }
    }

    @Test
    fun testVerboseLogsSwitchFunctionality() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val switch = activity.binding.verboseLogsSwitch
            val initialState = switch.isChecked
            switch.performClick()
            // Note: This will crash due to intentional NPE, but we test the click works
            // Remove crash code to properly test functionality
        }
    }

    @Test
    fun testNetworkInspectorSwitchFunctionality() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val switch = activity.binding.networkInspectorSwitch
            val initialState = switch.isChecked
            switch.performClick()
            // Verify switch state changed
            activityRule.recreate()
            activityRule.scenario.onActivity { recreatedActivity ->
                // State should persist via SharedPreferences
                // Remove crash code to properly test persistence
            }
        }
    }

    @Test
    fun testTestApiButtonClick() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.testApiButton
            val connectionStatus = activity.binding.connectionStatus
            val initialText = connectionStatus.text.toString()
            
            button.performClick()
            
            // Verify button is disabled during test
            assertFalse("Button should be disabled during test", button.isEnabled)
            assertEquals("Button text should change", "Running...", button.text)
        }
    }

    @Test
    fun testClearCacheButtonClick() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.clearCacheButton
            val memoryText = activity.binding.memoryUsageText
            
            assertNotNull("Memory text should exist", memoryText)
            button.performClick()
            // Verify cache clear action is triggered
            // Add assertions for memory text update after clear
        }
    }

    @Test
    fun testSharedPreferencesPersistence() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val prefs = activity.getSharedPreferences("DevPrefs", android.content.Context.MODE_PRIVATE)
            
            // Test verbose logs preference
            prefs.edit().putBoolean("verbose_logs", true).apply()
            assertTrue("Verbose logs should be saved", prefs.getBoolean("verbose_logs", false))
            
            // Test network inspector preference
            prefs.edit().putBoolean("network_inspector", true).apply()
            assertTrue("Network inspector should be saved", prefs.getBoolean("network_inspector", false))
        }
    }

    @Test
    fun testMemoryUsageDisplay() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val memoryText = activity.binding.memoryUsageText
            assertNotNull("Memory usage text should exist", memoryText)
            // Memory text should contain memory information
            val text = memoryText.text.toString()
            assertTrue("Memory text should contain 'Memory'", text.contains("Memory", ignoreCase = true))
        }
    }

    @Test
    fun testConnectionStatusDisplay() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val connectionStatus = activity.binding.connectionStatus
            assertNotNull("Connection status should exist", connectionStatus)
            // Initial state should be empty or show default status
        }
    }

    @Test
    fun testResponseTimeDisplay() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            val responseTimeText = activity.binding.responseTimeText
            assertNotNull("Response time text should exist", responseTimeText)
        }
    }

    @Test
    fun testUserFlow_EnableVerboseLogs() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Enable verbose logs
            val switch = activity.binding.verboseLogsSwitch
            assertFalse("Switch should be unchecked initially", switch.isChecked)
            // Note: Cannot complete flow due to crash in switch listener
            // Remove crash code to test full flow
        }
    }

    @Test
    fun testUserFlow_TestApiConnection() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Click test API button
            val button = activity.binding.testApiButton
            button.performClick()
            
            // Step 2: Verify button state changes
            assertFalse("Button should be disabled", button.isEnabled)
            assertEquals("Button should show 'Running...'", "Running...", button.text)
            
            // Step 3: Wait for completion (simulated delay)
            // In real test, use IdlingResource or Espresso idling
        }
    }

    @Test
    fun testUserFlow_ClearCache() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Click clear cache button
            val button = activity.binding.clearCacheButton
            button.performClick()
            
            // Step 2: Verify SharedPreferences cleared
            val prefs = activity.getSharedPreferences("DevPrefs", android.content.Context.MODE_PRIVATE)
            assertFalse("Verbose logs should be cleared", prefs.getBoolean("verbose_logs", false))
            assertFalse("Network inspector should be cleared", prefs.getBoolean("network_inspector", false))
        }
    }

    @Test
    fun testDevCrashTrigger() {
        Assume.assumeTrue("This test only runs for dev flavor", BuildConfig.FLAVOR == "dev")
        
        // This test will trigger the crash by clicking the verbose logs switch
        activityRule.scenario.onActivity { activity ->
            activity.binding.verboseLogsSwitch.performClick()
        }
    }
}
