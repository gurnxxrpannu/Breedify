package com.example.breedify.data.api

import android.graphics.Bitmap
import com.example.breedify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiApiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun identifyDogBreed(bitmap: Bitmap): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze this image and identify the dog breed. Please provide:
                    1. The most likely breed name
                    2. Confidence level (as a percentage)
                    3. Key identifying features you observed
                    4. Brief description of the breed's characteristics
                    
                    Format your response as:
                    Breed: [Breed Name]
                    Confidence: [X]%
                    Features: [Key features observed]
                    About: [Brief breed description]
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                Result.success(response.text ?: "Unable to identify breed")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun askAboutDog(bitmap: Bitmap, question: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Looking at this dog image, please answer the following question:
                    $question
                    
                    Please provide a helpful and informative response based on what you can observe in the image.
                """.trimIndent()

                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                Result.success(response.text ?: "Unable to provide answer")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBreedInformation(breedName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Provide comprehensive information about the $breedName dog breed including:
                    1. Origin and history
                    2. Physical characteristics
                    3. Temperament and personality
                    4. Exercise and care requirements
                    5. Health considerations
                    6. Suitability for different living situations
                    
                    Please format the response in a clear, organized manner.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                Result.success(response.text ?: "Unable to get breed information")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}