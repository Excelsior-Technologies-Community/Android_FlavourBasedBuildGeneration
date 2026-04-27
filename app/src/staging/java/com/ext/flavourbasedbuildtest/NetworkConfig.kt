package com.ext.flavourbasedbuildtest

object NetworkConfig {
    fun getConnectivityTimeout(): Long = 30000L // 30 seconds for staging

    fun getReadTimeout(): Long = 30000L

    fun getWriteTimeout(): Long = 30000L

    fun shouldRetryOnFailure(): Boolean = true

    fun getMaxRetries(): Int = 3
}
