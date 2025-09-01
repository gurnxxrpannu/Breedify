package com.example.breedify.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

data class DogFact(
    val id: String,
    val fact: String,
    val breed_id: Int? = null,
    val title: String? = null
) {
    // Get the actual fact content
    fun getFactText(): String {
        return fact
    }
}

data class DogFactsResponse(
    val data: List<DogFact>
)

interface DogFactsApiService {
    @GET("facts")
    suspend fun getRandomFacts(
        @Header("x-api-key") apiKey: String,
        @Query("limit") limit: Int = 5
    ): Response<List<DogFact>>
    
    @GET("breeds/{breed_id}/facts")
    suspend fun getBreedFacts(
        @Header("x-api-key") apiKey: String,
        @Path("breed_id") breedId: String,
        @Query("limit") limit: Int = 5
    ): Response<List<DogFact>>
}