package com.ext.flavourbasedbuildtest

import org.junit.Test
import org.junit.Assert.*

/**
 * Function-wise unit tests for FlavorConfig
 * Note: These tests will run against the flavor-specific implementation
 * based on which build variant is being tested
 */
class FlavorConfigTest {

    // ========== getFlavorName() Tests ==========
    
    @Test
    fun testGetFlavorName_FunctionExists() {
        val flavorName = FlavorConfig.getFlavorName()
        assertNotNull("getFlavorName() should return a value", flavorName)
    }

    @Test
    fun testGetFlavorName_ReturnsNonEmptyString() {
        val flavorName = FlavorConfig.getFlavorName()
        assertTrue("getFlavorName() should return non-empty string", flavorName.isNotEmpty())
    }

    @Test
    fun testGetFlavorName_ReturnsValidFlavorName() {
        val flavorName = FlavorConfig.getFlavorName()
        val validNames = listOf("Development", "Staging", "Production")
        assertTrue("getFlavorName() should return valid flavor name", 
            flavorName in validNames)
    }

    @Test
    fun testGetFlavorName_StartsWithUppercase() {
        val flavorName = FlavorConfig.getFlavorName()
        assertTrue("getFlavorName() should start with uppercase letter", 
            flavorName[0].isUpperCase())
    }

    @Test
    fun testGetFlavorName_NoSpaces() {
        val flavorName = FlavorConfig.getFlavorName()
        assertFalse("getFlavorName() should not contain spaces", 
            flavorName.contains(" "))
    }

    // ========== getApiTimeout() Tests ==========

    @Test
    fun testGetApiTimeout_FunctionExists() {
        val timeout = FlavorConfig.getApiTimeout()
        assertNotNull("getApiTimeout() should return a value", timeout)
    }

    @Test
    fun testGetApiTimeout_ReturnsPositiveValue() {
        val timeout = FlavorConfig.getApiTimeout()
        assertTrue("getApiTimeout() should return positive value", timeout > 0)
    }

    @Test
    fun testGetApiTimeout_MinimumFiveSeconds() {
        val timeout = FlavorConfig.getApiTimeout()
        assertTrue("getApiTimeout() should be at least 5 seconds (5000ms)", timeout >= 5000)
    }

    @Test
    fun testGetApiTimeout_MaximumOneMinute() {
        val timeout = FlavorConfig.getApiTimeout()
        assertTrue("getApiTimeout() should not exceed 1 minute (60000ms)", timeout <= 60000)
    }

    @Test
    fun testGetApiTimeout_ReturnsLongType() {
        val timeout = FlavorConfig.getApiTimeout()
        assertTrue("getApiTimeout() should return Long type", timeout is Long)
    }

    // ========== isDebugEnabled() Tests ==========

    @Test
    fun testIsDebugEnabled_FunctionExists() {
        val debugEnabled = FlavorConfig.isDebugEnabled()
        assertNotNull("isDebugEnabled() should return a value", debugEnabled)
    }

    @Test
    fun testIsDebugEnabled_ReturnsBoolean() {
        val debugEnabled = FlavorConfig.isDebugEnabled()
        assertTrue("isDebugEnabled() should return Boolean type", debugEnabled is Boolean)
    }

    @Test
    fun testIsDebugEnabled_ConsistentWithLogLevel() {
        val debugEnabled = FlavorConfig.isDebugEnabled()
        val logLevel = FlavorConfig.getLogLevel()
        
        if (debugEnabled) {
            assertTrue("When debug enabled, log level should be VERBOSE or DEBUG", 
                logLevel in listOf("VERBOSE", "DEBUG"))
        } else {
            assertTrue("When debug disabled, log level should be INFO, WARN, or ERROR", 
                logLevel in listOf("INFO", "WARN", "ERROR"))
        }
    }

    // ========== getLogLevel() Tests ==========

    @Test
    fun testGetLogLevel_FunctionExists() {
        val logLevel = FlavorConfig.getLogLevel()
        assertNotNull("getLogLevel() should return a value", logLevel)
    }

    @Test
    fun testGetLogLevel_ReturnsNonEmptyString() {
        val logLevel = FlavorConfig.getLogLevel()
        assertTrue("getLogLevel() should return non-empty string", logLevel.isNotEmpty())
    }

    @Test
    fun testGetLogLevel_ReturnsValidLogLevel() {
        val logLevel = FlavorConfig.getLogLevel()
        val validLogLevels = listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR")
        assertTrue("getLogLevel() should return valid log level", 
            logLevel in validLogLevels)
    }

    @Test
    fun testGetLogLevel_AllUppercase() {
        val logLevel = FlavorConfig.getLogLevel()
        assertEquals("getLogLevel() should return uppercase string", 
            logLevel, logLevel.uppercase())
    }

    // ========== Cross-Function Consistency Tests ==========

    @Test
    fun testAllFunctionsReturnConsistentConfiguration() {
        val flavorName = FlavorConfig.getFlavorName()
        val apiTimeout = FlavorConfig.getApiTimeout()
        val debugEnabled = FlavorConfig.isDebugEnabled()
        val logLevel = FlavorConfig.getLogLevel()

        // All values should be non-null
        assertNotNull("Flavor name should not be null", flavorName)
        assertNotNull("API timeout should not be null", apiTimeout)
        assertNotNull("Debug enabled should not be null", debugEnabled)
        assertNotNull("Log level should not be null", logLevel)

        // All values should be valid
        assertTrue("Flavor name should be valid", flavorName in listOf("Development", "Staging", "Production"))
        assertTrue("API timeout should be positive", apiTimeout > 0)
        assertTrue("Log level should be valid", logLevel in listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR"))
    }

    @Test
    fun testFlavorSpecificConfiguration() {
        val flavorName = FlavorConfig.getFlavorName()
        
        when (flavorName) {
            "Development" -> {
                assertEquals("Dev should have 30s timeout", 30000L, FlavorConfig.getApiTimeout())
                assertTrue("Dev should have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Dev should have VERBOSE log level", "VERBOSE", FlavorConfig.getLogLevel())
            }
            "Staging" -> {
                assertEquals("Staging should have 15s timeout", 15000L, FlavorConfig.getApiTimeout())
                assertTrue("Staging should have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Staging should have DEBUG log level", "DEBUG", FlavorConfig.getLogLevel())
            }
            "Production" -> {
                assertEquals("Prod should have 10s timeout", 10000L, FlavorConfig.getApiTimeout())
                assertFalse("Prod should not have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Prod should have ERROR log level", "ERROR", FlavorConfig.getLogLevel())
            }
        }
    }
}
