package com.ext.flavourbasedbuildtest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Function-wise unit tests for NetworkConfig
 * Note: These tests will run against the flavor-specific implementation
 * based on which build variant is being tested
 */
class NetworkConfigTest {
    // ========== getConnectivityTimeout() Tests ==========

    @Test
    fun testGetConnectivityTimeout_FunctionExists() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertNotNull("getConnectivityTimeout() should return a value", timeout)
    }

    @Test
    fun testGetConnectivityTimeout_ReturnsPositiveValue() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertTrue("getConnectivityTimeout() should return positive value", timeout > 0)
    }

    @Test
    fun testGetConnectivityTimeout_MinimumFiveSeconds() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertTrue("getConnectivityTimeout() should be at least 5 seconds (5000ms)", timeout >= 5000)
    }

    @Test
    fun testGetConnectivityTimeout_MaximumTwoMinutes() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertTrue("getConnectivityTimeout() should not exceed 2 minutes (120000ms)", timeout <= 120000)
    }

    @Test
    fun testGetConnectivityTimeout_ReturnsLongType() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertTrue("getConnectivityTimeout() should return Long type", timeout is Long)
    }

    // ========== getReadTimeout() Tests ==========

    @Test
    fun testGetReadTimeout_FunctionExists() {
        val timeout = NetworkConfig.getReadTimeout()
        assertNotNull("getReadTimeout() should return a value", timeout)
    }

    @Test
    fun testGetReadTimeout_ReturnsPositiveValue() {
        val timeout = NetworkConfig.getReadTimeout()
        assertTrue("getReadTimeout() should return positive value", timeout > 0)
    }

    @Test
    fun testGetReadTimeout_MinimumFiveSeconds() {
        val timeout = NetworkConfig.getReadTimeout()
        assertTrue("getReadTimeout() should be at least 5 seconds (5000ms)", timeout >= 5000)
    }

    @Test
    fun testGetReadTimeout_MaximumTwoMinutes() {
        val timeout = NetworkConfig.getReadTimeout()
        assertTrue("getReadTimeout() should not exceed 2 minutes (120000ms)", timeout <= 120000)
    }

    @Test
    fun testGetReadTimeout_ReturnsLongType() {
        val timeout = NetworkConfig.getReadTimeout()
        assertTrue("getReadTimeout() should return Long type", timeout is Long)
    }

    // ========== getWriteTimeout() Tests ==========

    @Test
    fun testGetWriteTimeout_FunctionExists() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertNotNull("getWriteTimeout() should return a value", timeout)
    }

    @Test
    fun testGetWriteTimeout_ReturnsPositiveValue() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertTrue("getWriteTimeout() should return positive value", timeout > 0)
    }

    @Test
    fun testGetWriteTimeout_MinimumFiveSeconds() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertTrue("getWriteTimeout() should be at least 5 seconds (5000ms)", timeout >= 5000)
    }

    @Test
    fun testGetWriteTimeout_MaximumTwoMinutes() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertTrue("getWriteTimeout() should not exceed 2 minutes (120000ms)", timeout <= 120000)
    }

    @Test
    fun testGetWriteTimeout_ReturnsLongType() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertTrue("getWriteTimeout() should return Long type", timeout is Long)
    }

    // ========== shouldRetryOnFailure() Tests ==========

    @Test
    fun testShouldRetryOnFailure_FunctionExists() {
        val retry = NetworkConfig.shouldRetryOnFailure()
        assertNotNull("shouldRetryOnFailure() should return a value", retry)
    }

    @Test
    fun testShouldRetryOnFailure_ReturnsBoolean() {
        val retry = NetworkConfig.shouldRetryOnFailure()
        assertTrue("shouldRetryOnFailure() should return Boolean type", retry is Boolean)
    }

    @Test
    fun testShouldRetryOnFailure_ConsistentWithMaxRetries() {
        val retryOnFailure = NetworkConfig.shouldRetryOnFailure()
        val maxRetries = NetworkConfig.getMaxRetries()

        if (retryOnFailure) {
            assertTrue("When retry enabled, max retries should be > 0", maxRetries > 0)
        } else {
            assertEquals("When retry disabled, max retries should be 0", 0, maxRetries)
        }
    }

    // ========== getMaxRetries() Tests ==========

    @Test
    fun testGetMaxRetries_FunctionExists() {
        val maxRetries = NetworkConfig.getMaxRetries()
        assertNotNull("getMaxRetries() should return a value", maxRetries)
    }

    @Test
    fun testGetMaxRetries_ReturnsNonNegative() {
        val maxRetries = NetworkConfig.getMaxRetries()
        assertTrue("getMaxRetries() should return non-negative value", maxRetries >= 0)
    }

    @Test
    fun testGetMaxRetries_ReturnsIntType() {
        val maxRetries = NetworkConfig.getMaxRetries()
        assertTrue("getMaxRetries() should return Int type", maxRetries is Int)
    }

    @Test
    fun testGetMaxRetries_MaximumTen() {
        val maxRetries = NetworkConfig.getMaxRetries()
        assertTrue("getMaxRetries() should not exceed 10", maxRetries <= 10)
    }

    // ========== Cross-Function Consistency Tests ==========

    @Test
    fun testAllTimeoutsAreEqual() {
        val connectivity = NetworkConfig.getConnectivityTimeout()
        val read = NetworkConfig.getReadTimeout()
        val write = NetworkConfig.getWriteTimeout()

        assertEquals("All timeout functions should return same value", connectivity, read)
        assertEquals("All timeout functions should return same value", read, write)
    }

    @Test
    fun testAllFunctionsReturnConsistentConfiguration() {
        val connectivityTimeout = NetworkConfig.getConnectivityTimeout()
        val readTimeout = NetworkConfig.getReadTimeout()
        val writeTimeout = NetworkConfig.getWriteTimeout()
        val retryOnFailure = NetworkConfig.shouldRetryOnFailure()
        val maxRetries = NetworkConfig.getMaxRetries()

        // All values should be non-null
        assertNotNull("Connectivity timeout should not be null", connectivityTimeout)
        assertNotNull("Read timeout should not be null", readTimeout)
        assertNotNull("Write timeout should not be null", writeTimeout)
        assertNotNull("Retry on failure should not be null", retryOnFailure)
        assertNotNull("Max retries should not be null", maxRetries)

        // All values should be valid
        assertTrue("Connectivity timeout should be positive", connectivityTimeout > 0)
        assertTrue("Read timeout should be positive", readTimeout > 0)
        assertTrue("Write timeout should be positive", writeTimeout > 0)
        assertTrue("Max retries should be non-negative", maxRetries >= 0)
    }

    @Test
    fun testFlavorSpecificConfiguration() {
        val connectivityTimeout = NetworkConfig.getConnectivityTimeout()
        val retryOnFailure = NetworkConfig.shouldRetryOnFailure()
        val maxRetries = NetworkConfig.getMaxRetries()

        when (connectivityTimeout) {
            60000L -> {
                // Dev configuration
                assertTrue("Dev should have retry enabled", retryOnFailure)
                assertEquals("Dev should have 5 max retries", 5, maxRetries)
            }
            30000L -> {
                // Staging configuration
                assertTrue("Staging should have retry enabled", retryOnFailure)
                assertEquals("Staging should have 3 max retries", 3, maxRetries)
            }
            15000L -> {
                // Prod configuration
                assertFalse("Prod should not have retry enabled", retryOnFailure)
                assertEquals("Prod should have 0 max retries", 0, maxRetries)
            }
        }
    }
}
