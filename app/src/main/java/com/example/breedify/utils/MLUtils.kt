package com.example.breedify.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.example.breedify.screens.prediction.PredictionResult

class MLUtils(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val modelFileName = "dog_breed_model.tflite" // Place your .tflite file in assets folder
    private val inputSize = 299
    private val pixelSize = 3 // RGB
    private val imageMean = 127.5f
    private val imageStd = 127.5f
    
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
        loadModel()
    }
    
    private fun loadModel() {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, modelFileName)
            interpreter = Interpreter(modelBuffer)
        } catch (e: Exception) {
            e.printStackTrace()
            // Model file not found or couldn't be loaded
        }
    }
    
    suspend fun predictBreed(imageUri: Uri): PredictionResult? {
        return try {
            // Preprocess the image
            val bitmap = CameraUtils.processImageForML(imageUri, context)
                ?: return null
            
            val inputBuffer = preprocessImage(bitmap)
            val outputArray = Array(1) { FloatArray(breedLabels.size) }
            
            // Run inference
            interpreter?.run(inputBuffer, outputArray)
            
            // Process results
            val predictions = outputArray[0]
            val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
            val confidence = predictions[maxIndex]
            val breedName = breedLabels.getOrNull(maxIndex) ?: "Unknown Breed"
            
            PredictionResult(breedName, confidence)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                
                // Extract RGB values and normalize
                val r = ((value shr 16 and 0xFF) - imageMean) / imageStd
                val g = ((value shr 8 and 0xFF) - imageMean) / imageStd
                val b = ((value and 0xFF) - imageMean) / imageStd
                
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
        }
        
        return inputBuffer
    }
    
    fun close() {
        interpreter?.close()
    }
}
