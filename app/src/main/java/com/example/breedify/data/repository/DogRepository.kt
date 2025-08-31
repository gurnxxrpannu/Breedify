package com.example.breedify.data.repository

import android.util.Log
import com.example.breedify.BuildConfig
import com.example.breedify.data.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DogRepository {
    
    companion object {
        private const val BASE_URL = "https://api.thedogapi.com/"
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
            Log.d("DogRepository", "Making API call to getAllBreeds with key: ${BuildConfig.DOG_API_KEY.take(10)}...")
            val response = api.getAllBreeds(BuildConfig.DOG_API_KEY)
            Log.d("DogRepository", "API response code: ${response.code()}")
            if (response.isSuccessful) {
                val breeds = response.body() ?: emptyList()
                Log.d("DogRepository", "Successfully fetched ${breeds.size} breeds")
                breeds.forEachIndexed { index, breed ->
                    Log.d("DogRepository", "Breed $index: ${breed.name}, image URL: ${breed.image?.url ?: "NO IMAGE"}")
                }
                Result.success(breeds)
            } else {
                Log.e("DogRepository", "Failed to fetch breeds: ${response.code()} - ${response.message()}")
                Log.e("DogRepository", "Error body: ${response.errorBody()?.string()}")
                Result.failure(Exception("Failed to fetch breeds: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("DogRepository", "Exception in getAllBreeds: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    suspend fun getBreedDetails(breedId: Int): Result<Breed> {
        return try {
            val response = api.getBreedDetails(BuildConfig.DOG_API_KEY, breedId)
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
            val response = api.searchBreeds(BuildConfig.DOG_API_KEY, query)
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
            val response = api.getBreedFacts(BuildConfig.DOG_API_KEY, breedId, limit)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch breed facts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRandomFacts(limit: Int = 1): Result<List<BreedFact>> {
        return try {
            val response = api.getRandomFacts(BuildConfig.DOG_API_KEY, limit)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch random facts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Favourites methods
    suspend fun getFavourites(subId: String? = null): Result<List<Favourite>> {
        return try {
            val response = api.getFavourites(BuildConfig.DOG_API_KEY, subId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch favourites: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addToFavourites(imageId: String, subId: String? = null): Result<CreateFavouriteResponse> {
        return try {
            val request = CreateFavouriteRequest(imageId, subId)
            val response = api.createFavourite(BuildConfig.DOG_API_KEY, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add to favourites: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeFromFavourites(favouriteId: Int): Result<Unit> {
        return try {
            val response = api.deleteFavourite(BuildConfig.DOG_API_KEY, favouriteId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to remove from favourites: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Voting methods
    suspend fun getVotes(subId: String? = null): Result<List<Vote>> {
        return try {
            val response = api.getVotes(BuildConfig.DOG_API_KEY, subId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch votes: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun voteOnImage(imageId: String, value: Int, subId: String? = null): Result<CreateVoteResponse> {
        return try {
            val request = CreateVoteRequest(imageId, subId, value)
            val response = api.createVote(BuildConfig.DOG_API_KEY, request = request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to vote on image: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteVote(voteId: Int): Result<Unit> {
        return try {
            val response = api.deleteVote(BuildConfig.DOG_API_KEY, voteId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete vote: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getImageDetails(imageId: String): Result<ImageDetails> {
        return try {
            val response = api.getImageDetails(BuildConfig.DOG_API_KEY, imageId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch image details: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}