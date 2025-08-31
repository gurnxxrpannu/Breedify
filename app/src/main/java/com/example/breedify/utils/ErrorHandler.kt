package com.example.breedify.utils

import android.content.Context
import android.widget.Toast
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handling for the application
 */
object ErrorHandler {
    
    /**
     * Handle and display appropriate error messages to users
     */
    fun handleError(
        context: Context,
        throwable: Throwable,
        showToast: Boolean = true
    ): String {
        val errorMessage = when (throwable) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "Request timed out. Please try again."
            is IOException -> "Network error occurred. Please try again."
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Invalid request. Please try again."
                    401 -> "Authentication failed. Please check your API keys."
                    403 -> "Access denied. Please check your permissions."
                    404 -> "Resource not found."
                    429 -> "Too many requests. Please wait and try again."
                    500 -> "Server error. Please try again later."
                    else -> "Network error (${throwable.code()}). Please try again."
                }
            }
            is SecurityException -> "Permission denied. Please grant required permissions."
            else -> "An unexpected error occurred. Please try again."
        }
        
        if (showToast) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
        
        // Log error for debugging (only in debug builds)
        if (AppConfig.ENABLE_LOGGING) {
            Logger.e("Error occurred: ${throwable.message}", throwable)
        }
        
        return errorMessage
    }
    
    /**
     * Handle API-specific errors
     */
    fun handleApiError(
        context: Context,
        throwable: Throwable,
        apiName: String = "API"
    ): String {
        val baseMessage = handleError(context, throwable, false)
        val apiMessage = "$apiName error: $baseMessage"
        
        Toast.makeText(context, apiMessage, Toast.LENGTH_LONG).show()
        return apiMessage
    }
    
    /**
     * Handle ML model errors
     */
    fun handleMLError(
        context: Context,
        throwable: Throwable
    ): String {
        val errorMessage = when (throwable) {
            is IllegalArgumentException -> "Invalid image format. Please try a different image."
            is OutOfMemoryError -> "Image too large. Please try a smaller image."
            else -> "Failed to process image. Please try again."
        }
        
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        
        if (AppConfig.ENABLE_LOGGING) {
            Logger.e("ML Error: ${throwable.message}", throwable)
        }
        
        return errorMessage
    }
}