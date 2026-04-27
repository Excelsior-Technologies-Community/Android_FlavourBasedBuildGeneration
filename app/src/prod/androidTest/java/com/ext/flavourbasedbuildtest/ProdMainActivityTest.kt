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
 * Prod Flavor UI Tests for MainActivity
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ProdMainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testProdMainActivityLaunches() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Prod MainActivity should not be null", activity)
            assertTrue("Activity should be Prod MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testProdSpecificUIElements() {
        activityRule.scenario.onActivity { activity ->
            // Test prod-specific UI elements
            assertNotNull("View details button should exist", activity.binding.viewDetailsButton)
            assertNotNull("Settings button should exist", activity.binding.settingsButton)
            assertNotNull("Active users text should exist", activity.binding.activeUsersText)
            assertNotNull("Revenue text should exist", activity.binding.revenueText)
        }
    }

    @Test
    fun testProdIntentionalCrash() {
        // INTENTIONAL CRASH: Call crash method in Prod MainActivity to verify crash detection
        activityRule.scenario.onActivity { activity ->
            activity.triggerIntentionalCrash()
        }
    }
}
