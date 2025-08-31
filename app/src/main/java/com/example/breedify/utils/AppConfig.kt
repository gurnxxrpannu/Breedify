package com.example.breedify.utils

import com.example.breedify.BuildConfig

/**
 * Application configuration and feature flags
 */
object AppConfig {
    // App Information
    const val APP_NAME = "Breedify"
    const val APP_VERSION = BuildConfig.VERSION_NAME
    
    // Feature Flags
    val ENABLE_LOGGING = BuildConfig.DEBUG
    val ENABLE_ANALYTICS = !BuildConfig.DEBUG
    val ENABLE_CRASH_REPORTING = !BuildConfig.DEBUG
    
    // API Configuration
    const val API_TIMEOUT_SECONDS = 30L
    const val MAX_RETRY_ATTEMPTS = 3
    
    // UI Configuration
    const val SPLASH_SCREEN_DURATION_MS = 2000L
    const val ANIMATION_DURATION_SHORT_MS = 150L
    const val ANIMATION_DURATION_MEDIUM_MS = 300L
    const val ANIMATION_DURATION_LONG_MS = 500L
    
    // Cache Configuration
    const val IMAGE_CACHE_SIZE_MB = 50
    const val MAX_CACHED_IMAGES = 100
    
    // Security & Performance
    val ENABLE_CERTIFICATE_PINNING = !BuildConfig.DEBUG
    val ENABLE_ROOT_DETECTION = !BuildConfig.DEBUG
    val ENABLE_STRICT_MODE = BuildConfig.DEBUG
    val MEMORY_LEAK_DETECTION = BuildConfig.DEBUG
    
    // Validation
    fun isApiKeyConfigured(): Boolean {
        return BuildConfig.GEMINI_API_KEY.isNotEmpty() && 
               BuildConfig.GEMINI_API_KEY != "your_actual_gemini_api_key_here"
    }
    
    fun isHuggingFaceConfigured(): Boolean {
        return BuildConfig.HUGGINGFACE_API_KEY.isNotEmpty() && 
               BuildConfig.HUGGINGFACE_API_KEY.startsWith("hf_")
    }
    
    fun isDogApiConfigured(): Boolean {
        return BuildConfig.DOG_API_KEY.isNotEmpty()
    }
}