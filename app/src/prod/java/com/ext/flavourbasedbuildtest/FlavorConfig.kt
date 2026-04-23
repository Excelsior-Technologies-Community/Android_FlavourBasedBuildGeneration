package com.ext.flavourbasedbuildtest

object FlavorConfig {
    fun getFlavorName(): String = "Production"
    fun getApiTimeout(): Long = 10000L // 10 seconds for prod
    fun isDebugEnabled(): Boolean = false
    fun getLogLevel(): String = "ERROR"
}
