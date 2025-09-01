package com.example.breedify.data.repository

import com.example.breedify.BuildConfig
import com.example.breedify.data.api.DogFact
import com.example.breedify.data.api.DogFactsApiService
import com.example.breedify.utils.ApiResult
import com.example.breedify.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DogFactsRepository {
    
    companion object {
        private const val BASE_URL = "https://some-random-api.com/animal/"
    }
    
    private val apiService: DogFactsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogFactsApiService::class.java)
    }
    
    suspend fun getRandomFacts(limit: Int = 5): ApiResult<List<DogFact>> = withContext(Dispatchers.IO) {
        Logger.d("Returning hardcoded dog facts", "DogFactsRepository")
        
        val hardcodedFacts = listOf(
            DogFact(
                id = "fact_1",
                fact = "Dogs have been human companions for over 15,000 years, making them one of the first domesticated animals.",
                breed_id = null,
                title = "History"
            ),
            DogFact(
                id = "fact_2",
                fact = "A dog's sense of smell is 10,000 to 100,000 times stronger than humans, with over 300 million olfactory receptors.",
                breed_id = null,
                title = "Senses"
            ),
            DogFact(
                id = "fact_3",
                fact = "Dogs can learn over 150 words and can count up to four or five, showing remarkable intelligence.",
                breed_id = null,
                title = "Intelligence"
            ),
            DogFact(
                id = "fact_4",
                fact = "The average dog can run about 19 miles per hour at full speed, with some breeds reaching up to 45 mph.",
                breed_id = null,
                title = "Speed"
            ),
            DogFact(
                id = "fact_5",
                fact = "Dogs have three eyelids - an upper lid, lower lid, and a third lid called the nictitating membrane for protection.",
                breed_id = null,
                title = "Anatomy"
            ),
            DogFact(
                id = "fact_6",
                fact = "Puppies are born deaf and blind, but their hearing becomes so sharp they can hear sounds at frequencies twice as high as humans.",
                breed_id = null,
                title = "Development"
            ),
            DogFact(
                id = "fact_7",
                fact = "Dogs sweat through their paw pads and cool down primarily by panting, not through their skin like humans.",
                breed_id = null,
                title = "Physiology"
            ),
            DogFact(
                id = "fact_8",
                fact = "A dog's mouth exerts 150-200 pounds of pressure per square inch, with some larger breeds reaching 450 PSI.",
                breed_id = null,
                title = "Bite Force"
            ),
            DogFact(
                id = "fact_9",
                fact = "Dogs can be trained to detect diseases like cancer, diabetes, and seizures through scent detection.",
                breed_id = null,
                title = "Medical Detection"
            ),
            DogFact(
                id = "fact_10",
                fact = "The Basenji dog breed is known as the 'barkless dog' because it doesn't bark traditionally but makes unique yodel-like sounds.",
                breed_id = null,
                title = "Unique Traits"
            ),
            DogFact(
                id = "fact_11",
                fact = "Dogs dream just like humans and often move their legs and make sounds while dreaming about familiar activities.",
                breed_id = null,
                title = "Sleep"
            ),
            DogFact(
                id = "fact_12",
                fact = "A dog's nose print is unique, just like a human's fingerprint, and can be used for identification.",
                breed_id = null,
                title = "Identification"
            )
        )
        
        val selectedFacts = hardcodedFacts.shuffled().take(limit)
        Logger.d("Returning ${selectedFacts.size} hardcoded facts", "DogFactsRepository")
        
        ApiResult.Success(selectedFacts)
    }
    
    suspend fun getBreedFacts(breedId: String, limit: Int = 5): ApiResult<List<DogFact>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getBreedFacts(
                apiKey = BuildConfig.DOG_API_KEY,
                breedId = breedId,
                limit = limit
            )
            
            if (response.isSuccessful) {
                val facts = response.body() ?: emptyList()
                ApiResult.Success(facts)
            } else {
                ApiResult.Error(Exception("Failed to fetch breed facts: ${response.message()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e, "Network error: ${e.message}")
        }
    }
}