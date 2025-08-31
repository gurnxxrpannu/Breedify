package com.example.breedify.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.breedify.BuildConfig
import com.example.breedify.data.api.HuggingFaceApiService
import com.example.breedify.screens.prediction.PredictionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Utility class to test the Hugging Face API integration.
 * This can be used to verify that the API connection works correctly.
 */
class HuggingFaceApiTest(private val context: Context) {
    companion object {
        private const val TAG = "HuggingFaceApiTest"
    }
    
    private val huggingFaceApiService = HuggingFaceApiService(context)
    
    /**
     * Test the basic connection to the Hugging Face API without sending an image.
     * This helps diagnose network connectivity issues.
     * @param callback Callback to receive the test result
     */
    suspend fun testApiConnection(callback: (Boolean, String) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Testing Hugging Face API connection")
                
                // Make a simple request to check if the API is accessible
                val response = OkHttpClient().newCall(
                    Request.Builder()
                        .url("https://api-inference.huggingface.co/models/sakshammittal/Breedify-Dog-Breed-Identification")
                        .addHeader("Authorization", "Bearer ${BuildConfig.HUGGINGFACE_API_KEY}")
                        .build()
                ).execute()
                
                val isSuccessful = response.isSuccessful
                val responseBody = if (isSuccessful) response.body?.string() else "No response body"
                val message = if (isSuccessful) {
                    "API connection successful: ${response.code}\nResponse: $responseBody"
                } else {
                    "API connection failed: ${response.code} - ${response.message}\nResponse: $responseBody"
                }
                
                Log.d(TAG, message)
                withContext(Dispatchers.Main) {
                    callback(isSuccessful, message)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "API connection test failed: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(false, "API connection test failed: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Test the Hugging Face API with a local image file.
     * @param imageUri The URI of the image to test with
     * @param callback Callback to receive the test result
     */
    suspend fun testApiWithImage(imageUri: Uri, callback: (Boolean, String, PredictionResult?) -> Unit) {
        try {
            Log.d(TAG, "Starting API test with image: $imageUri")
            
            val result = huggingFaceApiService.predictBreed(imageUri)
            
            if (result != null) {
                Log.d(TAG, "API test successful: ${result.breedName} with confidence ${result.confidence}")
                callback(true, "API test successful: ${result.breedName} with confidence ${result.confidence}", result)
            } else {
                Log.e(TAG, "API test failed: No prediction result returned")
                callback(false, "API test failed: No prediction result returned", null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "API test failed with exception: ${e.message}")
            e.printStackTrace()
            callback(false, "API test failed with exception: ${e.message}", null)
        }
    }
}