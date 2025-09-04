package com.example.breedify.data.repository

import android.graphics.Bitmap
import com.example.breedify.data.api.GeminiApiService
import com.example.breedify.utils.AppConfig

class GeminiRepository(
    private val geminiApiService: GeminiApiService = GeminiApiService()
) {
    suspend fun identifyDogBreed(bitmap: Bitmap): Result<String> {
        if (!AppConfig.isApiKeyConfigured()) {
            return Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your API key to local.properties file.")
            )
        }
        return geminiApiService.identifyDogBreed(bitmap)
    }

    suspend fun askAboutDog(bitmap: Bitmap, question: String): Result<String> {
        if (!AppConfig.isApiKeyConfigured()) {
            return Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your API key to local.properties file.")
            )
        }
        return geminiApiService.askAboutDog(bitmap, question)
    }

    suspend fun getBreedInformation(breedName: String): Result<String> {
        if (!AppConfig.isApiKeyConfigured()) {
            return Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add your API key to local.properties file.")
            )
        }
        return geminiApiService.getBreedInformation(breedName)
    }

    suspend fun generateResponse(message: String): String {
        if (!AppConfig.isApiKeyConfigured()) {
            return "Gemini AI is not configured. Please add your API key to use this feature."
        }
        return geminiApiService.generateChatResponse(message).getOrElse { 
            "I'm sorry, I couldn't process your request. Please try asking about a specific dog breed or dog-related topic."
        }
    }
}