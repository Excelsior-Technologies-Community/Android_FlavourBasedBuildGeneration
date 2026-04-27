package com.ext.flavourbasedbuildtest

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.button.MaterialButton
import android.widget.TextView

import org.junit.Assert.*

/**
 * UI Tests for MainActivity using Espresso
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testActivityLaunches() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Activity should not be null", activity)
            assertNotNull("Binding should be initialized", activity.binding)
        }
    }

    @Test
    fun testVerboseLogsSwitchExists() {
        activityRule.scenario.onActivity { activity ->
            val switch = activity.binding.verboseLogsSwitch
            assertNotNull("Verbose logs switch should exist", switch)
            assertTrue("Switch should be a SwitchMaterial", switch is SwitchMaterial)
        }
    }

    @Test
    fun testNetworkInspectorSwitchExists() {
        activityRule.scenario.onActivity { activity ->
            val switch = activity.binding.networkInspectorSwitch
            assertNotNull("Network inspector switch should exist", switch)
            assertTrue("Switch should be a SwitchMaterial", switch is SwitchMaterial)
        }
    }

    @Test
    fun testTestApiButtonExists() {
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.testApiButton
            assertNotNull("Test API button should exist", button)
            assertTrue("Button should be a MaterialButton", button is MaterialButton)
            assertEquals("Button text should be 'Test API'", "Test API", button.text.toString())
        }
    }

    @Test
    fun testClearCacheButtonExists() {
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.clearCacheButton
            assertNotNull("Clear cache button should exist", button)
            assertTrue("Button should be a MaterialButton", button is MaterialButton)
            assertEquals("Button text should be 'Clear Cache'", "Clear Cache", button.text.toString())
        }
    }

    @Test
    fun testMemoryUsageTextExists() {
        activityRule.scenario.onActivity { activity ->
            val textView = activity.binding.memoryUsageText
            assertNotNull("Memory usage text should exist", textView)
            assertTrue("Should be a TextView", textView is TextView)
        }
    }

    @Test
    fun testConnectionStatusExists() {
        activityRule.scenario.onActivity { activity ->
            val textView = activity.binding.connectionStatus
            assertNotNull("Connection status should exist", textView)
            assertTrue("Should be a TextView", textView is TextView)
        }
    }

    @Test
    fun testResponseTimeTextExists() {
        activityRule.scenario.onActivity { activity ->
            val textView = activity.binding.responseTimeText
            assertNotNull("Response time text should exist", textView)
            assertTrue("Should be a TextView", textView is TextView)
        }
    }

    @Test
    fun testSharedPreferencesInitialized() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("SharedPreferences should be initialized", activity.sharedPreferences)
            assertEquals("Prefs name should be DevPrefs", "DevPrefs", activity.PREFS_NAME)
        }
    }

    @Test
    fun testMemoryUsageUpdates() {
        activityRule.scenario.onActivity { activity ->
            val textView = activity.binding.memoryUsageText
            // Wait a bit for memory update
            Thread.sleep(2000)
            val text = textView.text.toString()
            assertTrue("Memory text should contain 'App Memory'", text.contains("App Memory"))
            assertTrue("Memory text should contain 'Device Memory'", text.contains("Device Memory"))
        }
    }
}
