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
 * Staging Flavor UI Tests for MainActivity
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StagingMainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testStagingMainActivityLaunches() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Staging MainActivity should not be null", activity)
            assertTrue("Activity should be Staging MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testStagingSpecificUIElements() {
        activityRule.scenario.onActivity { activity ->
            // Test staging-specific UI elements
            assertNotNull("Run tests button should exist", activity.binding.runTestsButton)
            assertNotNull("View logs button should exist", activity.binding.viewLogsButton)
            assertNotNull("Unit tests text should exist", activity.binding.unitTestsText)
            assertNotNull("Integration tests text should exist", activity.binding.integrationTestsText)
        }
    }

    @Test
    fun testStagingIntentionalCrash() {
        // INTENTIONAL CRASH: Call crash method in Staging MainActivity to verify crash detection
        activityRule.scenario.onActivity { activity ->
            activity.triggerIntentionalCrash()
        }
    }
}
