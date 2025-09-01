package com.example.breedify.data.api

import android.graphics.Bitmap
import com.example.breedify.BuildConfig
import com.example.breedify.utils.Constants
import com.example.breedify.utils.Logger
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service for handling Gemini AI API interactions
 */
class GeminiApiService {
    private val generativeModel = GenerativeModel(
        modelName = Constants.GEMINI_MODEL_NAME,
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    
    companion object {
        private const val TAG = "GeminiApiService"
    }

    /**
     * Identifies dog breed from an image using Gemini AI
     */
    suspend fun identifyDogBreed(bitmap: Bitmap): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("Starting dog breed identification", TAG)
                
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
                val result = response.text ?: "Unable to identify breed"
                
                Logger.d("Dog breed identification completed successfully", TAG)
                Result.success(result)
            } catch (e: Exception) {
                Logger.e("Error identifying dog breed", e, TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Answers questions about a dog based on an image
     */
    suspend fun askAboutDog(bitmap: Bitmap, question: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("Processing dog-related question with image", TAG)
                
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
                val result = response.text ?: "Unable to provide answer"
                
                Logger.d("Dog question answered successfully", TAG)
                Result.success(result)
            } catch (e: Exception) {
                Logger.e("Error answering dog question", e, TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Gets comprehensive information about a specific dog breed
     */
    suspend fun getBreedInformation(breedName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("Fetching breed information for: $breedName", TAG)
                
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
                val result = response.text ?: "Unable to get breed information"
                
                Logger.d("Breed information retrieved successfully", TAG)
                Result.success(result)
            } catch (e: Exception) {
                Logger.e("Error getting breed information", e, TAG)
                Result.failure(e)
            }
        }
    }

    /**
     * Generates conversational responses for the chatbot
     */
    suspend fun generateChatResponse(message: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("Generating chat response for message", TAG)
                
                if (message.length > Constants.MAX_MESSAGE_LENGTH) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Message too long. Maximum ${Constants.MAX_MESSAGE_LENGTH} characters allowed.")
                    )
                }
                
                val prompt = """
                    You are Breedify Assistant, a helpful AI assistant specialized in dogs and dog breeds. 
                    Please respond to the following message in a friendly and informative way.
                    Focus on providing helpful information about dogs, dog breeds, care, training, health, or any dog-related topics.
                    
                    User message: $message
                    
                    Please provide a helpful response. If the question is not dog-related, politely redirect the conversation back to dog topics.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val result = response.text ?: "I'm sorry, I couldn't generate a response."
                
                Logger.d("Chat response generated successfully", TAG)
                Result.success(result)
            } catch (e: Exception) {
                Logger.e("Error generating chat response", e, TAG)
                Result.failure(e)
            }
        }
    }
}