package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Development"
    fun getApiTimeout(): Long = 30000L // 30 seconds for dev
    fun isDebugEnabled(): Boolean = true
    fun getLogLevel(): String = "VERBOSE"
}
