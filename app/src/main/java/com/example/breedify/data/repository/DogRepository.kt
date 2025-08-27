package com.example.breedify.data.repository

import com.example.breedify.data.api.Breed
import com.example.breedify.data.api.BreedFact
import com.example.breedify.data.api.DogApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DogRepository {
    
    companion object {
        private const val BASE_URL = "https://api.thedogapi.com/"
        private const val API_KEY = "live_D6iOYBSXrgscP6pHHbU9VY9Z60qKdTGOK0Hw67vZTohQyl7WzkBWhUNwweWzpHE6"
    }
    
    private val api: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
    
    suspend fun getAllBreeds(): Result<List<Breed>> {
        return try {
            val response = api.getAllBreeds(API_KEY)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch breeds: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBreedDetails(breedId: Int): Result<Breed> {
        return try {
            val response = api.getBreedDetails(API_KEY, breedId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch breed details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchBreeds(query: String): Result<List<Breed>> {
        return try {
            val response = api.searchBreeds(API_KEY, query)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to search breeds: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBreedFacts(breedId: Int, limit: Int = 5): Result<List<BreedFact>> {
        return try {
            val response = api.getBreedFacts(API_KEY, breedId, limit)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch breed facts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}