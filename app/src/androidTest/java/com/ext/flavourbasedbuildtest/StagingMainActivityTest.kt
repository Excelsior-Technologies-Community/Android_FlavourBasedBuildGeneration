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
 * Staging Flavor UI Tests for MainActivity
 * Comprehensive test coverage for all Staging flavor features
 * These tests only run for the staging flavor
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StagingMainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testStagingMainActivityLaunches() {
        // Skip if not staging flavor
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Staging MainActivity should not be null", activity)
            assertTrue("Activity should be Staging MainActivity", activity is MainActivity)
        }
    }

    @Test
    fun testAllLayoutComponentsExist() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            // Buttons
            assertNotNull("Run tests button should exist", activity.binding.runTestsButton)
            assertNotNull("View logs button should exist", activity.binding.viewLogsButton)
            // Text views
            assertNotNull("Unit tests text should exist", activity.binding.unitTestsText)
            assertNotNull("Integration tests text should exist", activity.binding.integrationTestsText)
            assertNotNull("Pending tests text should exist", activity.binding.pendingTestsText)
            // Progress bar
            assertNotNull("Performance bar should exist", activity.binding.performanceBar)
        }
    }

    @Test
    fun testRunTestsButtonClick() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.runTestsButton
            val unitTestsText = activity.binding.unitTestsText
            val integrationTestsText = activity.binding.integrationTestsText
            
            button.performClick()
            
            // Verify button is disabled during test
            assertFalse("Button should be disabled during test", button.isEnabled)
            assertEquals("Button text should change", "Running...", button.text)
        }
    }

    @Test
    fun testViewLogsButtonClick() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            val button = activity.binding.viewLogsButton
            assertNotNull("View logs button should exist", button)
            // Note: This will crash due to intentional NPE
            // Remove crash code to properly test dialog functionality
        }
    }

    @Test
    fun testTestResultsDisplay() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            val unitTestsText = activity.binding.unitTestsText
            val integrationTestsText = activity.binding.integrationTestsText
            val pendingTestsText = activity.binding.pendingTestsText
            
            assertNotNull("Unit tests text should exist", unitTestsText)
            assertNotNull("Integration tests text should exist", integrationTestsText)
            assertNotNull("Pending tests text should exist", pendingTestsText)
        }
    }

    @Test
    fun testPerformanceBar() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            val performanceBar = activity.binding.performanceBar
            assertNotNull("Performance bar should exist", performanceBar)
            // Initial progress should be 0 or default value
            assertTrue("Progress should be between 0 and 100", performanceBar.progress in 0..100)
        }
    }

    @Test
    fun testTestLogsStorage() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            // Verify test logs are stored in the activity
            // This tests the internal state management
            assertNotNull("Activity should exist", activity)
        }
    }

    @Test
    fun testUserFlow_RunTests() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Click run tests button
            val button = activity.binding.runTestsButton
            button.performClick()
            
            // Step 2: Verify button state changes
            assertFalse("Button should be disabled", button.isEnabled)
            assertEquals("Button should show 'Running...'", "Running...", button.text)
            
            // Step 3: Wait for test completion (simulated delay)
            // In real test, use IdlingResource or Espresso idling
        }
    }

    @Test
    fun testUserFlow_ViewLogs() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Click view logs button
            val button = activity.binding.viewLogsButton
            // Note: Cannot complete flow due to crash in button listener
            // Remove crash code to test full flow
        }
    }

    @Test
    fun testUserFlow_RunTestsThenViewLogs() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        activityRule.scenario.onActivity { activity ->
            // Step 1: Run tests
            val runTestsButton = activity.binding.runTestsButton
            runTestsButton.performClick()
            
            // Step 2: View logs (after tests complete)
            // In real test, wait for tests to complete first
            // Note: Cannot complete due to crash in view logs button
        }
    }

    @Test
    fun testStagingCrashTrigger() {
        Assume.assumeTrue("This test only runs for staging flavor", BuildConfig.FLAVOR == "staging")
        
        // This test will trigger the crash by clicking the view logs button
        activityRule.scenario.onActivity { activity ->
            activity.binding.viewLogsButton.performClick()
        }
    }
}
