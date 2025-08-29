package com.example.breedify.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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
    
    @GET("v1/facts")
    suspend fun getRandomFacts(
        @Header("x-api-key") apiKey: String,
        @Query("limit") limit: Int = 1
    ): Response<List<BreedFact>>
    
    // Favourites endpoints
    @GET("v1/favourites")
    suspend fun getFavourites(
        @Header("x-api-key") apiKey: String,
        @Query("sub_id") subId: String? = null,
        @Query("limit") limit: Int = 100
    ): Response<List<Favourite>>
    
    @POST("v1/favourites")
    suspend fun createFavourite(
        @Header("x-api-key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: CreateFavouriteRequest
    ): Response<CreateFavouriteResponse>
    
    @GET("v1/favourites/{favourite_id}")
    suspend fun getFavourite(
        @Header("x-api-key") apiKey: String,
        @Path("favourite_id") favouriteId: Int
    ): Response<Favourite>
    
    @DELETE("v1/favourites/{favourite_id}")
    suspend fun deleteFavourite(
        @Header("x-api-key") apiKey: String,
        @Path("favourite_id") favouriteId: Int
    ): Response<Unit>
    
    // Votes endpoints
    @GET("v1/votes")
    suspend fun getVotes(
        @Header("x-api-key") apiKey: String,
        @Query("sub_id") subId: String? = null,
        @Query("limit") limit: Int = 100
    ): Response<List<Vote>>
    
    @POST("v1/votes")
    suspend fun createVote(
        @Header("x-api-key") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: CreateVoteRequest
    ): Response<CreateVoteResponse>
    
    @GET("v1/votes/{vote_id}")
    suspend fun getVote(
        @Header("x-api-key") apiKey: String,
        @Path("vote_id") voteId: Int
    ): Response<Vote>
    
    @DELETE("v1/votes/{vote_id}")
    suspend fun deleteVote(
        @Header("x-api-key") apiKey: String,
        @Path("vote_id") voteId: Int
    ): Response<Unit>
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

data class Favourite(
    val id: Int,
    val user_id: String,
    val image_id: String,
    val sub_id: String? = null,
    val created_at: String,
    val image: BreedImage
)

data class CreateFavouriteRequest(
    val image_id: String,
    val sub_id: String? = null
)

data class CreateFavouriteResponse(
    val message: String,
    val id: Int
)

data class Vote(
    val id: Int,
    val user_id: String,
    val image_id: String,
    val sub_id: String? = null,
    val created_at: String,
    val value: Int, // 1 for upvote, 0 for downvote
    val image: BreedImage
)

data class CreateVoteRequest(
    val image_id: String,
    val sub_id: String? = null,
    val value: Int // 1 for upvote, 0 for downvote
)

data class CreateVoteResponse(
    val message: String,
    val id: Int
)