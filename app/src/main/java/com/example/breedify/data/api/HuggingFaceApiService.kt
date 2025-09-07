package com.example.breedify.data.api

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.breedify.BuildConfig
import com.example.breedify.data.model.PredictionResult
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
import java.util.concurrent.TimeUnit

class HuggingFaceApiService(private val context: Context) {
    companion object {
        private const val TAG = "HuggingFaceApiService"
        private val API_URL = "https://api-inference.huggingface.co/models/sakshammittal/Breedify-model"
    
    // Alternate model URLs in case the primary one fails
    private val ALTERNATE_API_URLS = listOf(
        "https://api-inference.huggingface.co/models/microsoft/resnet-50",
        "https://api-inference.huggingface.co/models/google/vit-base-patch16-224"
    )
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun predictBreed(imageUri: Uri): PredictionResult? {
        return withContext(Dispatchers.IO) {
            // Try primary URL first, then one alternate for faster fallback
            val urlsToTry = listOf(API_URL, ALTERNATE_API_URLS.first())
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
                    val apiKey = BuildConfig.HUGGINGFACE_API_KEY
                    Log.d(TAG, "API Key available: ${apiKey.isNotEmpty()} (length: ${apiKey.length})")
                    
                    if (apiKey.isEmpty()) {
                        Log.e(TAG, "HUGGINGFACE_API_KEY is empty!")
                        lastError = "API key not configured"
                        continue
                    }
                    
                    val request = Request.Builder()
                        .url(apiUrl)
                        .addHeader("Authorization", "Bearer $apiKey")
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
                    lastError = when (e) {
                        is java.net.SocketTimeoutException -> "Request timed out. The AI service might be busy."
                        is java.net.UnknownHostException -> "Network error. Please check your internet connection."
                        is java.net.ConnectException -> "Cannot connect to AI service. Please try again later."
                        else -> e.message ?: "Unknown error occurred"
                    }
                    // Continue to next URL
                }
            }
            
            Log.e(TAG, "All API URLs failed. Last error: $lastError")
            
            // Fallback: Return a mock prediction to allow users to test the app
            Log.d(TAG, "Using fallback mock prediction due to API failures")
            return@withContext createMockPrediction(lastError)
        }
    }
    
    private fun createMockPrediction(errorReason: String?): PredictionResult {
        // List of popular dog breeds for mock predictions
        val mockBreeds = listOf(
            "Golden Retriever" to 0.85f,
            "Labrador Retriever" to 0.82f,
            "German Shepherd" to 0.78f,
            "Bulldog" to 0.75f,
            "Poodle" to 0.73f,
            "Beagle" to 0.70f,
            "Rottweiler" to 0.68f,
            "Yorkshire Terrier" to 0.65f,
            "Dachshund" to 0.63f,
            "Siberian Husky" to 0.60f
        )
        
        // Select a random breed for the mock prediction
        val selectedBreed = mockBreeds.random()
        
        Log.d(TAG, "Mock prediction: ${selectedBreed.first} with confidence ${selectedBreed.second}")
        Log.d(TAG, "Note: This is a fallback prediction due to API error: $errorReason")
        
        return PredictionResult(
            breedName = "${selectedBreed.first} (Demo)",
            confidence = selectedBreed.second
        )
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