package com.example.breedify.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DogApiService {
    
    @GET("v1/breeds")
    suspend fun getAllBreeds(
        @Header("x-api-key") apiKey: String
    ): Response<List<Breed>>
    
    @GET("v1/breeds/{breed_id}")
    suspend fun getBreedDetails(
        @Header("x-api-key") apiKey: String,
        @Path("breed_id") breedId: Int
    ): Response<Breed>
    
    @GET("v1/breeds/search")
    suspend fun searchBreeds(
        @Header("x-api-key") apiKey: String,
        @Query("q") query: String
    ): Response<List<Breed>>
    
    @GET("v1/breeds/{breed_id}/facts")
    suspend fun getBreedFacts(
        @Header("x-api-key") apiKey: String,
        @Path("breed_id") breedId: Int,
        @Query("limit") limit: Int = 5
    ): Response<List<BreedFact>>
}

data class Breed(
    val id: Int,
    val name: String,
    val bred_for: String? = null,
    val breed_group: String? = null,
    val life_span: String? = null,
    val temperament: String? = null,
    val origin: String? = null,
    val reference_image_id: String? = null,
    val image: BreedImage? = null,
    val weight: Weight? = null,
    val height: Height? = null
)

data class BreedImage(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String
)

data class Weight(
    val imperial: String,
    val metric: String
)

data class Height(
    val imperial: String,
    val metric: String
)

data class BreedFact(
    val id: Int,
    val fact: String
)