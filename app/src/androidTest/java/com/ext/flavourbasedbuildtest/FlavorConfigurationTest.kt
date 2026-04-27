package com.ext.flavourbasedbuildtest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Functional tests for flavor-specific configurations
 * Tests BuildConfig values for each flavor
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class FlavorConfigurationTest {

    @Test
    fun testPackageNameStartsWithBase() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val packageName = appContext.packageName
        assertTrue("Package name should start with base package", 
            packageName.startsWith("com.ext.flavourbasedbuildtest"))
    }

    @Test
    fun testBuildConfigBaseUrlExists() {
        val baseUrl = BuildConfig.BASE_URL
        assertNotNull("BASE_URL should not be null", baseUrl)
        assertTrue("BASE_URL should be a valid URL", baseUrl.startsWith("http"))
    }

    @Test
    fun testBuildConfigAuthKeyExists() {
        val authKey = BuildConfig.AUTH_KEY
        assertNotNull("AUTH_KEY should not be null", authKey)
        assertTrue("AUTH_KEY should not be empty", authKey.isNotEmpty())
    }

    @Test
    fun testBuildConfigFlavorExists() {
        val flavor = BuildConfig.FLAVOR
        assertNotNull("FLAVOR should not be null", flavor)
        assertTrue("FLAVOR should be one of dev, staging, or prod", 
            flavor in listOf("dev", "staging", "prod"))
    }

    @Test
    fun testBuildConfigBuildTypeExists() {
        val buildType = BuildConfig.BUILD_TYPE
        assertNotNull("BUILD_TYPE should not be null", buildType)
        assertTrue("BUILD_TYPE should be debug or release", 
            buildType in listOf("debug", "release"))
    }

    @Test
    fun testBuildConfigFeatureFlagsExist() {
        assertNotNull("ENABLE_DEBUG_FEATURES should exist", BuildConfig.ENABLE_DEBUG_FEATURES)
        assertNotNull("ENABLE_ANALYTICS should exist", BuildConfig.ENABLE_ANALYTICS)
        assertNotNull("ENABLE_CRASH_REPORTING should exist", BuildConfig.ENABLE_CRASH_REPORTING)
    }

    @Test
    fun testBuildConfigAnalyticsIdExists() {
        val analyticsId = BuildConfig.ANALYTICS_ID
        assertNotNull("ANALYTICS_ID should not be null", analyticsId)
    }

    @Test
    fun testBuildConfigFirebaseProjectIdExists() {
        val firebaseProjectId = BuildConfig.FIREBASE_PROJECT_ID
        assertNotNull("FIREBASE_PROJECT_ID should not be null", firebaseProjectId)
    }

    @Test
    fun testFlavorConfigValues() {
        val flavorName = FlavorConfig.getFlavorName()
        val apiTimeout = FlavorConfig.getApiTimeout()
        val debugEnabled = FlavorConfig.isDebugEnabled()
        val logLevel = FlavorConfig.getLogLevel()

        assertNotNull("Flavor name should not be null", flavorName)
        assertTrue("API timeout should be positive", apiTimeout > 0)
        assertNotNull("Debug enabled should not be null", debugEnabled)
        assertNotNull("Log level should not be null", logLevel)
        assertTrue("Log level should be valid", 
            logLevel in listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR"))
    }

    @Test
    fun testNetworkConfigValues() {
        val connectivityTimeout = NetworkConfig.getConnectivityTimeout()
        val readTimeout = NetworkConfig.getReadTimeout()
        val writeTimeout = NetworkConfig.getWriteTimeout()
        val retryOnFailure = NetworkConfig.shouldRetryOnFailure()
        val maxRetries = NetworkConfig.getMaxRetries()

        assertTrue("Connectivity timeout should be positive", connectivityTimeout > 0)
        assertTrue("Read timeout should be positive", readTimeout > 0)
        assertTrue("Write timeout should be positive", writeTimeout > 0)
        assertNotNull("Retry on failure should not be null", retryOnFailure)
        assertTrue("Max retries should be non-negative", maxRetries >= 0)
    }

    @Test
    fun testFlavorSpecificConfiguration() {
        val flavor = BuildConfig.FLAVOR
        val flavorName = FlavorConfig.getFlavorName()

        when (flavor) {
            "dev" -> {
                assertEquals("Dev flavor should have Development name", "Development", flavorName)
                assertTrue("Dev should have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Dev should have VERBOSE log level", "VERBOSE", FlavorConfig.getLogLevel())
                assertTrue("Dev should have retry enabled", NetworkConfig.shouldRetryOnFailure())
                assertEquals("Dev should have 5 max retries", 5, NetworkConfig.getMaxRetries())
            }
            "staging" -> {
                assertEquals("Staging flavor should have Staging name", "Staging", flavorName)
                assertTrue("Staging should have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Staging should have DEBUG log level", "DEBUG", FlavorConfig.getLogLevel())
                assertTrue("Staging should have retry enabled", NetworkConfig.shouldRetryOnFailure())
                assertEquals("Staging should have 3 max retries", 3, NetworkConfig.getMaxRetries())
            }
            "prod" -> {
                assertEquals("Prod flavor should have Production name", "Production", flavorName)
                assertFalse("Prod should not have debug enabled", FlavorConfig.isDebugEnabled())
                assertEquals("Prod should have ERROR log level", "ERROR", FlavorConfig.getLogLevel())
                assertFalse("Prod should not have retry enabled", NetworkConfig.shouldRetryOnFailure())
                assertEquals("Prod should have 0 max retries", 0, NetworkConfig.getMaxRetries())
            }
        }
    }

    @Test
    fun testNetworkTimeoutsIncreaseFromProdToDev() {
        val flavor = BuildConfig.FLAVOR
        val connectivityTimeout = NetworkConfig.getConnectivityTimeout()

        when (flavor) {
            "dev" -> {
                assertEquals("Dev should have 60s connectivity timeout", 60000L, connectivityTimeout)
            }
            "staging" -> {
                assertEquals("Staging should have 30s connectivity timeout", 30000L, connectivityTimeout)
            }
            "prod" -> {
                assertEquals("Prod should have 15s connectivity timeout", 15000L, connectivityTimeout)
            }
        }
    }
}
