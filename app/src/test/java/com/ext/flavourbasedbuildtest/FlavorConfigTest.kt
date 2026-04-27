package com.ext.flavourbasedbuildtest

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for FlavorConfig
 * Note: These tests will run against the flavor-specific implementation
 * based on which build variant is being tested
 */
class FlavorConfigTest {

    @Test
    fun testGetFlavorNameReturnsNonEmptyString() {
        val flavorName = FlavorConfig.getFlavorName()
        assertNotNull("Flavor name should not be null", flavorName)
        assertTrue("Flavor name should not be empty", flavorName.isNotEmpty())
    }

    @Test
    fun testGetFlavorNameIsValid() {
        val flavorName = FlavorConfig.getFlavorName()
        val validNames = listOf("Development", "Staging", "Production")
        assertTrue("Flavor name should be one of the valid names", 
            flavorName in validNames)
    }

    @Test
    fun testGetApiTimeoutReturnsPositiveValue() {
        val timeout = FlavorConfig.getApiTimeout()
        assertTrue("API timeout should be positive", timeout > 0)
    }

    @Test
    fun testGetApiTimeoutIsInReasonableRange() {
        val timeout = FlavorConfig.getApiTimeout()
        
        // API timeout should be between 5 seconds and 1 minute
        assertTrue("API timeout should be at least 5 seconds", timeout >= 5000)
        assertTrue("API timeout should not exceed 1 minute", timeout <= 60000)
    }

    @Test
    fun testIsDebugEnabledReturnsBoolean() {
        val debugEnabled = FlavorConfig.isDebugEnabled()
        assertNotNull("Debug enabled should return a boolean", debugEnabled)
    }

    @Test
    fun testGetLogLevelReturnsNonEmptyString() {
        val logLevel = FlavorConfig.getLogLevel()
        assertNotNull("Log level should not be null", logLevel)
        assertTrue("Log level should not be empty", logLevel.isNotEmpty())
    }

    @Test
    fun testGetLogLevelIsValid() {
        val logLevel = FlavorConfig.getLogLevel()
        val validLogLevels = listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR")
        assertTrue("Log level should be one of the valid levels", 
            logLevel in validLogLevels)
    }

    @Test
    fun testDebugConfigurationConsistency() {
        val debugEnabled = FlavorConfig.isDebugEnabled()
        val logLevel = FlavorConfig.getLogLevel()
        
        if (debugEnabled) {
            // If debug is enabled, log level should be VERBOSE or DEBUG
            assertTrue("Debug enabled should have verbose or debug log level", 
                logLevel in listOf("VERBOSE", "DEBUG"))
        } else {
            // If debug is disabled, log level should be INFO, WARN, or ERROR
            assertTrue("Debug disabled should have info, warn, or error log level", 
                logLevel in listOf("INFO", "WARN", "ERROR"))
        }
    }

    @Test
    fun testFlavorNameMatchesExpectedPattern() {
        val flavorName = FlavorConfig.getFlavorName()
        
        // Flavor name should be a single word with first letter capitalized
        assertTrue("Flavor name should start with uppercase letter", 
            flavorName[0].isUpperCase())
        assertTrue("Flavor name should not contain spaces", 
            !flavorName.contains(" "))
    }
}
