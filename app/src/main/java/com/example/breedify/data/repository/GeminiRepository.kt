package com.example.breedify.data.repository

import android.graphics.Bitmap
import com.example.breedify.data.api.GeminiApiService

class GeminiRepository(
    private val geminiApiService: GeminiApiService = GeminiApiService()
) {
    suspend fun identifyDogBreed(bitmap: Bitmap): Result<String> {
        return geminiApiService.identifyDogBreed(bitmap)
    }

    suspend fun askAboutDog(bitmap: Bitmap, question: String): Result<String> {
        return geminiApiService.askAboutDog(bitmap, question)
    }

    suspend fun getBreedInformation(breedName: String): Result<String> {
        return geminiApiService.getBreedInformation(breedName)
    }

    suspend fun generateResponse(message: String): String {
        return getBreedInformation(message).getOrElse { 
            "I'm sorry, I couldn't process your request. Please try asking about a specific dog breed or dog-related topic."
        }
    }
}