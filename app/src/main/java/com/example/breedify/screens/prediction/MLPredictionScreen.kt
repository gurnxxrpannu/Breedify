package com.example.breedify.screens.prediction

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.breedify.components.PredictionLoadingAnimation
import com.example.breedify.screens.homeScreen.BreedifyColors
import com.example.breedify.utils.MLUtils
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.data.api.Breed
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.breedify.data.model.PredictionResult

// Helper function to normalize breed names for better matching
private fun normalizeBreedName(breedName: String): String {
    return breedName
        .lowercase()
        .replace("_", " ")
        .replace("-", " ")
        .replace(",", " ") // Handle comma-separated values like "pug,pug dog"
        .split(" ")
        .filter { it.isNotBlank() && it != "dog" } // Remove "dog" suffix and empty strings
        .distinct() // Remove duplicates like "pug,pug dog" → ["pug"]
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}

// Helper function to extract possible breed names from ML result
private fun extractBreedVariations(breedName: String): List<String> {
    val normalized = normalizeBreedName(breedName)
    val variations = mutableListOf<String>()
    
    // Add the normalized version
    variations.add(normalized)
    
    // Add individual words (for cases like "Golden Retriever" → ["Golden", "Retriever"])
    normalized.split(" ").forEach { word ->
        if (word.length > 2) { // Only add meaningful words
            variations.add(word)
        }
    }
    
    // Add original name
    variations.add(breedName)
    
    return variations.distinct()
}

@Composable
fun MLPredictionScreen(
    imageUri: Uri,
    onBackPressed: () -> Unit,
    onPredictionComplete: (PredictionResult) -> Unit,
    onBreedFound: (Breed) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Initializing...") }
    var predictionProgress by remember { mutableIntStateOf(0) }
    var predictionResult by remember { mutableStateOf<PredictionResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { DogRepository() }
    
    // Start prediction when screen loads
    LaunchedEffect(imageUri) {
        scope.launch {
            try {
                // Phase 1: Initial setup and connection
                statusMessage = "Connecting to AI service..."
                delay(500L)
                
                statusMessage = "Preparing image for analysis..."
                delay(400L)
                
                statusMessage = "Note: First request may take longer as AI model loads..."
                delay(800L)
                
                // Phase 2: Start ML prediction with progress counter
                statusMessage = "Analyzing with Breedify AI model..."
                
                // Start the actual API call in background
                val mlUtils = MLUtils(context)
                var apiResult: PredictionResult? = null
                var apiError: Exception? = null
                
                // Launch API call in parallel with progress animation
                val apiJob = scope.launch {
                    try {
                        apiResult = mlUtils.predictBreed(imageUri)
                    } catch (e: Exception) {
                        apiError = e
                        // Log the error for debugging
                        android.util.Log.e("MLPredictionScreen", "API error: ${e.message}", e)
                    }
                }
                
                // Phase 3: Realistic AI prediction with variable speed
                
                // Phase 3a: Slow initial processing (1-60%) - Model is analyzing
                for (i in 1..60) {
                    predictionProgress = i
                    statusMessage = when {
                        i < 20 -> "Analyzing image features..."
                        i < 40 -> "Extracting visual patterns..."
                        else -> "Comparing with breed database..."
                    }
                    delay(80L) // Slower for initial analysis
                }
                
                // Simulate model finding the prediction at around 60-75%
                val predictionFoundAt = Random.nextInt(60, 75)
                
                // Phase 3b: Medium speed until prediction is "found" (60-75%)
                for (i in 61..predictionFoundAt) {
                    predictionProgress = i
                    statusMessage = "Identifying breed characteristics..."
                    delay(60L) // Medium speed
                }
                
                // Wait for API to complete if it hasn't already
                apiJob.join()
                
                // Check API result
                if (apiError != null) {
                    throw apiError!!
                }
                
                if (apiResult == null) {
                    throw Exception("Failed to get prediction from API")
                }
                
                // Phase 3c: Fast completion (75-100%) - Model is confident
                statusMessage = "Finalizing prediction..."
                for (i in (predictionFoundAt + 1)..100) {
                    predictionProgress = i
                    delay(20L) // Much faster once prediction is found
                }
                
                // Complete prediction
                predictionResult = apiResult
                onPredictionComplete(apiResult!!)
                isLoading = false
                
            } catch (e: Exception) {
                errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "The AI service is taking longer than usual. Please check your internet connection and try again."
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Network error. Please check your internet connection."
                    e.message?.contains("connection", ignoreCase = true) == true -> 
                        "Unable to connect to the AI service. Please try again later."
                    e.message?.contains("API", ignoreCase = true) == true -> 
                        "AI service temporarily unavailable. Please try again in a few minutes."
                    else -> "An unexpected error occurred: ${e.message ?: "Unknown error"}"
                }
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BreedifyColors.Background)
            .padding(20.dp)
    ) {
        // Top bar with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.9f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BreedifyColors.TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "Breed Identification",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BreedifyColors.TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Image preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Status/Result section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> {
                        // Loading state with progress bar and status text
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PredictionLoadingAnimation(
                                statusMessage = "Analyzing breed...",
                                subMessage = "",
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            if (predictionProgress > 0) {
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Progress bar
                                LinearProgressIndicator(
                                    progress = { predictionProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = statusMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BreedifyColors.TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = statusMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BreedifyColors.TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    errorMessage != null -> {
                        // Error state
                        Text(
                            text = "❌",
                            fontSize = 48.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Prediction Failed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BreedifyColors.TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = errorMessage!!,
                            fontSize = 14.sp,
                            color = BreedifyColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBackPressed,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back")
                            }
                            
                            Button(
                                onClick = {
                                    // Retry prediction by resetting state
                                    errorMessage = null
                                    predictionResult = null
                                    isLoading = true
                                    statusMessage = "Retrying..."
                                    predictionProgress = 0
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BreedifyColors.Primary
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                    
                    predictionResult != null -> {
                        // Success state
                        Text(
                            text = "Breed Identified!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BreedifyColors.TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Breed result card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BreedifyColors.Secondary.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = predictionResult!!.breedName,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BreedifyColors.TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Confidence: ${(predictionResult!!.confidence * 100).toInt()}%",
                                    fontSize = 16.sp,
                                    color = BreedifyColors.TextSecondary
                                )
                            }
                        }
                        
                        // Show demo mode notification if applicable
                        if (predictionResult!!.breedName.contains("(Demo)")) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ℹ️",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    
                                    Text(
                                        text = "Demo Mode: AI service unavailable. This is a sample prediction.",
                                        fontSize = 12.sp,
                                        color = BreedifyColors.TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBackPressed,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back to Home")
                            }
                            
                            Button(
                                onClick = {
                                    // Search for breed and navigate to details using improved matching
                                    scope.launch {
                                        predictionResult?.let { result ->
                                            val breedVariations = extractBreedVariations(result.breedName)
                                            var foundBreed: Breed? = null
                                            
                                            // Try each variation until we find a match
                                            for (variation in breedVariations) {
                                                repository.searchBreeds(variation).fold(
                                                    onSuccess = { breeds ->
                                                        if (breeds.isNotEmpty()) {
                                                            foundBreed = breeds.first()
                                                            return@fold // Exit the fold early
                                                        }
                                                    },
                                                    onFailure = { /* Handle error silently */ }
                                                )
                                                
                                                // If we found a breed, break out of the loop
                                                if (foundBreed != null) break
                                            }
                                            
                                            // Navigate to breed detail if we found a match
                                            foundBreed?.let { onBreedFound(it) }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BreedifyColors.Secondary
                                )
                            ) {
                                Text("Learn More")
                            }
                        }
                    }
                }
            }
        }
    }
}
