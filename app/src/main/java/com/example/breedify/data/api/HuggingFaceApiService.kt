package com.example.breedify.data.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.breedify.BuildConfig
import com.example.breedify.screens.prediction.PredictionResult
import com.example.breedify.utils.CameraUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HuggingFaceApiService(private val context: Context) {
    companion object {
        private const val TAG = "HuggingFaceApiService"
        private val API_URL = "https://api-inference.huggingface.co/models/microsoft/resnet-50"
    
    // Alternate model URLs in case the primary one fails
    private val ALTERNATE_API_URLS = listOf(
        "https://api-inference.huggingface.co/models/skyau/dog-breed-classifier-vit",
        "https://api-inference.huggingface.co/models/google/vit-base-patch16-224"
    )
    }

    private val client = OkHttpClient.Builder().build()

    suspend fun predictBreed(imageUri: Uri): PredictionResult? {
        return withContext(Dispatchers.IO) {
            // Try primary URL first, then fall back to alternates if needed
            val urlsToTry = listOf(API_URL) + ALTERNATE_API_URLS
            var lastError: String? = null
            
            for (apiUrl in urlsToTry) {
                try {
                    Log.d(TAG, "Starting prediction with Hugging Face API for image: $imageUri using URL: $apiUrl")
                    
                    // Convert Uri to File
                    val imageFile = uriToFile(imageUri)
                    if (imageFile == null) {
                        Log.e(TAG, "Failed to convert Uri to File")
                        continue // Try next URL
                    }
                    
                    // Create request body with image file as binary data
                    val requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    
                    // Create request with API token
                    val request = Request.Builder()
                        .url(apiUrl)
                        .addHeader("Authorization", "Bearer ${BuildConfig.HUGGINGFACE_API_KEY}")
                        .addHeader("Content-Type", "image/jpeg")
                        .post(requestBody)
                        .build()
                    
                    Log.d(TAG, "Sending request to Hugging Face API: $apiUrl")
                    
                    // Execute request
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            val errorBody = response.body?.string()
                            Log.e(TAG, "API request failed for $apiUrl: ${response.code} - ${response.message}")
                            Log.e(TAG, "Error response: ${errorBody ?: "No error body"}")
                            lastError = "HTTP ${response.code}: ${response.message}"
                            return@use null // Continue to next URL
                        }
                        
                        val responseBody = response.body?.string()
                        if (responseBody == null) {
                            Log.e(TAG, "Empty response from API: $apiUrl")
                            lastError = "Empty response from API"
                            return@use null // Continue to next URL
                        }
                        
                        // Parse JSON response
                        Log.d(TAG, "Parsing API response from $apiUrl: $responseBody")
                        
                        try {
                            // Hugging Face Inference API returns an array of predictions
                            if (responseBody.startsWith("[")) {
                                // Parse as JSON array
                                val jsonArray = org.json.JSONArray(responseBody)
                                if (jsonArray.length() > 0) {
                                    val prediction = jsonArray.getJSONObject(0)
                                    val breedName = prediction.getString("label")
                                    val confidence = prediction.getDouble("score").toFloat()
                                    
                                    Log.d(TAG, "Prediction result from $apiUrl: $breedName with confidence $confidence")
                                    
                                    return@withContext PredictionResult(breedName, confidence)
                                }
                            } else {
                                // Try parsing as single object
                                val jsonObject = JSONObject(responseBody)
                                if (jsonObject.has("label")) {
                                    val breedName = jsonObject.getString("label")
                                    val confidence = jsonObject.getDouble("score").toFloat()
                                    
                                    Log.d(TAG, "Prediction result from $apiUrl: $breedName with confidence $confidence")
                                    
                                    return@withContext PredictionResult(breedName, confidence)
                                }
                            }
                            
                            Log.e(TAG, "Unexpected API response format from $apiUrl: $responseBody")
                            lastError = "Unexpected API response format"
                            return@use null // Continue to next URL
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing API response from $apiUrl: ${e.message}")
                            lastError = "Error parsing API response: ${e.message}"
                            return@use null // Continue to next URL
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during API prediction with $apiUrl: ${e.message}")
                    e.printStackTrace()
                    lastError = e.message
                    // Continue to next URL
                }
            }
            
            Log.e(TAG, "All API URLs failed. Last error: $lastError")
            null
        }
    }
    
    private fun uriToFile(uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
            
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            inputStream.close()
            return tempFile
        } catch (e: IOException) {
            Log.e(TAG, "Error converting Uri to File: ${e.message}")
            return null
        }
    }
}