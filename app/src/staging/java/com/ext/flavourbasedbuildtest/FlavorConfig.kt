package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Staging"
    fun getApiTimeout(): Long = 15000L // 15 seconds for staging
    fun isDebugEnabled(): Boolean = true
    fun getLogLevel(): String = "DEBUG"
}
