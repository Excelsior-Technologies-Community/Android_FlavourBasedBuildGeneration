package com.ext.flavourbasedbuildtest

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for NetworkConfig
 * Note: These tests will run against the flavor-specific implementation
 * based on which build variant is being tested
 */
class NetworkConfigTest {

    @Test
    fun testGetConnectivityTimeoutReturnsPositiveValue() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        assertTrue("Connectivity timeout should be positive", timeout > 0)
    }

    @Test
    fun testGetReadTimeoutReturnsPositiveValue() {
        val timeout = NetworkConfig.getReadTimeout()
        assertTrue("Read timeout should be positive", timeout > 0)
    }

    @Test
    fun testGetWriteTimeoutReturnsPositiveValue() {
        val timeout = NetworkConfig.getWriteTimeout()
        assertTrue("Write timeout should be positive", timeout > 0)
    }

    @Test
    fun testAllTimeoutsAreEqual() {
        val connectivity = NetworkConfig.getConnectivityTimeout()
        val read = NetworkConfig.getReadTimeout()
        val write = NetworkConfig.getWriteTimeout()
        
        assertEquals("All timeouts should be equal for consistency", connectivity, read)
        assertEquals("All timeouts should be equal for consistency", read, write)
    }

    @Test
    fun testShouldRetryOnFailureReturnsBoolean() {
        val retry = NetworkConfig.shouldRetryOnFailure()
        assertNotNull("Retry on failure should return a boolean", retry)
    }

    @Test
    fun testGetMaxRetriesReturnsNonNegative() {
        val maxRetries = NetworkConfig.getMaxRetries()
        assertTrue("Max retries should be non-negative", maxRetries >= 0)
    }

    @Test
    fun testRetryConfigurationConsistency() {
        val retryOnFailure = NetworkConfig.shouldRetryOnFailure()
        val maxRetries = NetworkConfig.getMaxRetries()
        
        if (retryOnFailure) {
            assertTrue("If retry is enabled, max retries should be greater than 0", maxRetries > 0)
        } else {
            assertEquals("If retry is disabled, max retries should be 0", 0, maxRetries)
        }
    }

    @Test
    fun testTimeoutsAreInReasonableRange() {
        val timeout = NetworkConfig.getConnectivityTimeout()
        
        // Timeouts should be between 5 seconds and 2 minutes
        assertTrue("Timeout should be at least 5 seconds", timeout >= 5000)
        assertTrue("Timeout should not exceed 2 minutes", timeout <= 120000)
    }
}
