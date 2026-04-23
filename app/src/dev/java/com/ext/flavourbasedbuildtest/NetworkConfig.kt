package com.ext.flavourbasedbuildtest

object NetworkConfig {
    fun getConnectivityTimeout(): Long = 60000L // 60 seconds for dev
    fun getReadTimeout(): Long = 60000L
    fun getWriteTimeout(): Long = 60000L
    fun shouldRetryOnFailure(): Boolean = true
    fun getMaxRetries(): Int = 5
}
