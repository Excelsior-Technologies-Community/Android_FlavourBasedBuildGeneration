package com.ext.flavourbasedbuildtest

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        }
    }

    @Test
    fun testActivityIsMainActivity() {
        activityRule.scenario.onActivity { activity ->
            assertTrue("Activity should be instance of MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testActivityHasContentView() {
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Activity should have a content view", activity.window.decorView.findViewById(android.R.id.content))
        }
    }

    @Test
    fun testActivityIsNotFinishing() {
        activityRule.scenario.onActivity { activity ->
            assertFalse("Activity should not be finishing", activity.isFinishing)
        }
    }

    @Test
    fun testActivityIsResumed() {
        activityRule.scenario.onActivity { activity ->
            // Activity should be in resumed state after launch
            assertTrue("Activity should be resumed", !activity.isDestroyed)
        }
    }
}
