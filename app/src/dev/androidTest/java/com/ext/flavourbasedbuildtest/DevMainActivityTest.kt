package com.ext.flavourbasedbuildtest

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Dev Flavor UI Tests for MainActivity
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DevMainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testDevMainActivityLaunches() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Dev MainActivity should not be null", activity)
            assertTrue("Activity should be Dev MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testDevSpecificUIElements() {
        activityRule.scenario.onActivity { activity ->
            // Test dev-specific UI elements
            assertNotNull("Verbose logs switch should exist", activity.binding.verboseLogsSwitch)
            assertNotNull("Network inspector switch should exist", activity.binding.networkInspectorSwitch)
            assertNotNull("Test API button should exist", activity.binding.testApiButton)
            assertNotNull("Clear cache button should exist", activity.binding.clearCacheButton)
        }
    }

    @Test
    fun testDevIntentionalCrash() {
        // INTENTIONAL CRASH: Dev flavor-specific crash to verify crash detection
        throw RuntimeException("DEV FLAVOR CRASH: This is a test crash for Dev MainActivity to verify crash detection in CI/CD")
    }
}
