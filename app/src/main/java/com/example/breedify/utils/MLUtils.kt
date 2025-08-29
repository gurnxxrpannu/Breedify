package com.example.breedify.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp
import com.example.breedify.screens.prediction.PredictionResult

class MLUtils(private val context: Context) {
    
    companion object {
        private const val TAG = "MLUtils"
    }
    
    private var interpreter: Interpreter? = null
    private val modelFileName = "dog_breed_model.tflite" // Place your .tflite file in assets folder
    private val inputSize = 299
    private val pixelSize = 3 // RGB
    // Updated normalization values for better model performance
    private val imageMean = 0.0f
    private val imageStd = 1.0f
    
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
        // Load model with regular interpreter only
        loadModel()
    }
    
    
    private fun loadModel() {
        try {
            Log.d(TAG, "Loading model with regular interpreter: $modelFileName")
            
            // Check if model file exists in assets
            val assetManager = context.assets
            val modelExists = try {
                assetManager.open(modelFileName).use { 
                    Log.d(TAG, "Model file found in assets: $modelFileName")
                    true 
                }
            } catch (e: Exception) {
                Log.e(TAG, "Model file not found in assets: $modelFileName", e)
                false
            }
            
            if (!modelExists) {
                throw Exception("Model file '$modelFileName' not found in assets folder")
            }
            
            val modelBuffer = FileUtil.loadMappedFile(context, modelFileName)
            Log.d(TAG, "Model buffer size: ${modelBuffer.capacity()} bytes")
            
            // Create interpreter options for CPU-only execution
            val options = Interpreter.Options()
            options.setNumThreads(4) // Use more CPU threads for better performance
            options.setUseNNAPI(false) // Disable NNAPI to ensure CPU-only execution
            Log.d(TAG, "Using CPU-only execution")
            
            interpreter = Interpreter(modelBuffer, options)
            Log.d(TAG, "Model loaded successfully with regular interpreter")
            
            // Validate model input/output shapes
            val inputTensor = interpreter!!.getInputTensor(0)
            val outputTensor = interpreter!!.getOutputTensor(0)
            Log.d(TAG, "Input shape: ${inputTensor.shape().contentToString()}")
            Log.d(TAG, "Output shape: ${outputTensor.shape().contentToString()}")
            Log.d(TAG, "Expected output classes: ${breedLabels.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model with regular interpreter: ${e.message}", e)
            e.printStackTrace()
            interpreter = null
        }
    }
    
    suspend fun predictBreed(imageUri: Uri): PredictionResult? {
        return try {
            Log.d(TAG, "Starting prediction for image: $imageUri")
            
            // Preprocess the image
            val bitmap = CameraUtils.processImageForML(imageUri, context)
            if (bitmap == null) {
                Log.e(TAG, "Failed to process image from URI")
                throw Exception("Failed to process image")
            }
            
            Log.d(TAG, "Bitmap processed successfully: ${bitmap.width}x${bitmap.height}")
            
            // Check if interpreter is loaded
            if (interpreter == null) {
                Log.w(TAG, "Interpreter is null, attempting to reload model")
                loadModel()
                if (interpreter == null) {
                    Log.e(TAG, "Failed to load ML model")
                    throw Exception("Failed to load ML model")
                }
            }
            
            // Use regular interpreter
            return predictWithInterpreter(bitmap)
            
        } catch (e: Exception) {
            Log.e(TAG, "Prediction failed: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    
    private fun predictWithInterpreter(bitmap: Bitmap): PredictionResult? {
        return try {
            Log.d(TAG, "Running prediction with regular interpreter")
            
            val inputBuffer = preprocessImage(bitmap)
            
            // Get actual output tensor shape from the model
            val outputTensor = interpreter!!.getOutputTensor(0)
            val outputShape = outputTensor.shape()
            Log.d(TAG, "Actual output tensor shape: ${outputShape.contentToString()}")
            
            // Create output array based on actual model output shape
            val outputSize = if (outputShape.size > 1) outputShape[1] else breedLabels.size
            val outputArray = Array(1) { FloatArray(outputSize) }
            
            Log.d(TAG, "Running inference with output size: $outputSize")
            interpreter!!.run(inputBuffer, outputArray)
            
            Log.d(TAG, "Processing prediction results")
            val predictions = outputArray[0]
            
            // Log first few predictions for debugging
            Log.d(TAG, "Raw predictions (first 5): ${predictions.take(5).joinToString()}")
            
            // Apply softmax to get proper probabilities
            val expValues = predictions.map { exp(it.toDouble()).toFloat() }
            val sumExp = expValues.sum()
            val softmaxPredictions = expValues.map { it / sumExp }
            
            val maxIndex = softmaxPredictions.indices.maxByOrNull { softmaxPredictions[it] } ?: 0
            val confidence = softmaxPredictions[maxIndex]
            
            // Handle case where model output size doesn't match breed labels
            val breedName = if (maxIndex < breedLabels.size) {
                breedLabels[maxIndex]
            } else {
                "Breed_$maxIndex" // Fallback name if index exceeds breed list
            }
            
            Log.d(TAG, "Interpreter prediction: $breedName (index: $maxIndex) with confidence: $confidence")
            
            if (confidence > 0.01f) {
                PredictionResult(breedName, confidence)
            } else {
                Log.w(TAG, "Low confidence prediction: $confidence")
                PredictionResult("Unknown Breed", 0.0f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Interpreter prediction failed: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * pixelSize)
        inputBuffer.order(ByteOrder.nativeOrder())
        
        // Ensure bitmap is the correct size
        val resizedBitmap = if (bitmap.width != inputSize || bitmap.height != inputSize) {
            Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        } else {
            bitmap
        }
        
        val intValues = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)
        
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                
                // Extract RGB values and normalize to [0, 1] range
                val r = ((value shr 16 and 0xFF).toFloat()) / 255.0f
                val g = ((value shr 8 and 0xFF).toFloat()) / 255.0f
                val b = ((value and 0xFF).toFloat()) / 255.0f
                
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
        }
        
        inputBuffer.rewind() // Important: rewind buffer before inference
        return inputBuffer
    }
    
    
    fun close() {
        interpreter?.close()
    }
}
