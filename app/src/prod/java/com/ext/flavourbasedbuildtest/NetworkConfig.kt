package com.ext.flavourbasedbuildtest

object NetworkConfig {
    fun getConnectivityTimeout(): Long = 15000L // 15 seconds for prod
    fun getReadTimeout(): Long = 15000L
    fun getWriteTimeout(): Long = 15000L
    fun shouldRetryOnFailure(): Boolean = false
    fun getMaxRetries(): Int = 0
}
