package com.example.breedify.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.breedify.data.api.HuggingFaceApiService
import com.example.breedify.screens.prediction.PredictionResult

class MLUtils(private val context: Context) {
    
    companion object {
        private const val TAG = "MLUtils"
    }
    
    private val huggingFaceApiService = HuggingFaceApiService(context)
    
    // Dog breed labels - update this list to match your model's output classes
    private val breedLabels = listOf(
        "Afghan Hound", "African Hunting Dog", "Airedale Terrier", "American Staffordshire Terrier",
        "Appenzeller Sennenhund", "Australian Terrier", "Basenji", "Basset Hound", "Beagle",
        "Bedlington Terrier", "Bernese Mountain Dog", "Black and Tan Coonhound", "Blenheim Spaniel",
        "Bloodhound", "Bluetick Coonhound", "Border Collie", "Border Terrier", "Borzoi",
        "Boston Terrier", "Bouvier des Flandres", "Boxer", "Brabancon Griffon", "Briard",
        "Brittany Spaniel", "Bull Mastiff", "Bull Terrier", "Bulldog", "Cairn Terrier",
        "Cardigan Welsh Corgi", "Chesapeake Bay Retriever", "Chihuahua", "Chinese Crested",
        "Chinese Shar-Pei", "Chow Chow", "Clumber Spaniel", "Cocker Spaniel", "Collie",
        "Curly-Coated Retriever", "Dachshund", "Dalmatian", "Dandie Dinmont Terrier",
        "Dingo", "Doberman Pinscher", "English Foxhound", "English Setter", "English Springer Spaniel",
        "EntleBucher", "Eskimo Dog", "French Bulldog", "German Shepherd", "German Short-Haired Pointer",
        "Giant Schnauzer", "Golden Retriever", "Gordon Setter", "Great Dane", "Great Pyrenees",
        "Greater Swiss Mountain Dog", "Groenendael", "Ibizan Hound", "Irish Setter", "Irish Terrier",
        "Irish Water Spaniel", "Irish Wolfhound", "Italian Greyhound", "Japanese Spaniel",
        "Keeshond", "Kerry Blue Terrier", "Komondor", "Kuvasz", "Labrador Retriever",
        "Lakeland Terrier", "Leonberger", "Lhasa Apso", "Malamute", "Malinois", "Maltese",
        "Mexican Hairless", "Miniature Pinscher", "Miniature Poodle", "Miniature Schnauzer",
        "Newfoundland", "Norfolk Terrier", "Norwegian Elkhound", "Norwich Terrier",
        "Old English Sheepdog", "Otterhound", "Papillon", "Pekinese", "Pembroke Welsh Corgi",
        "Pomeranian", "Poodle", "Pug", "Redbone Coonhound", "Rhodesian Ridgeback",
        "Rottweiler", "Saint Bernard", "Saluki", "Samoyed", "Schipperke", "Scottish Deerhound",
        "Scottish Terrier", "Sealyham Terrier", "Shetland Sheepdog", "Shih Tzu", "Siberian Husky",
        "Silky Terrier", "Soft-Coated Wheaten Terrier", "Standard Poodle", "Standard Schnauzer",
        "Staffordshire Bull Terrier", "Sussex Spaniel", "Tibetan Mastiff", "Tibetan Terrier",
        "Toy Poodle", "Toy Terrier", "Vizsla", "Walker Hound", "Weimaraner", "Welsh Springer Spaniel",
        "West Highland White Terrier", "Whippet", "Wire-Haired Fox Terrier", "Yorkshire Terrier"
    )
    
    init {
        Log.d(TAG, "Initializing MLUtils with Hugging Face API service")
    }
    
    suspend fun predictBreed(imageUri: Uri): PredictionResult? {
        return try {
            Log.d(TAG, "Starting prediction for image: $imageUri using Hugging Face API")
            
            // Verify the image URI is valid
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                inputStream?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Invalid image URI: $imageUri", e)
                throw Exception("Invalid image URI: $imageUri")
            }
            
            // Use Hugging Face API service to predict breed
            val result = huggingFaceApiService.predictBreed(imageUri)
            
            if (result == null) {
                Log.e(TAG, "Failed to get prediction from Hugging Face API")
                throw Exception("Failed to get prediction from API")
            }
            
            Log.d(TAG, "Prediction successful: ${result.breedName} with confidence ${result.confidence}")
            return result
            
        } catch (e: Exception) {
            Log.e(TAG, "Prediction failed: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    
    fun close() {
        // No resources to close with API-based approach
    }
}
