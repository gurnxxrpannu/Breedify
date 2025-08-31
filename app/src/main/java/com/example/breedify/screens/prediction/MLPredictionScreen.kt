package com.example.breedify.screens.prediction

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

data class PredictionResult(
    val breedName: String,
    val confidence: Float
)

// Helper function to normalize breed names for better matching
private fun normalizeBreedName(breedName: String): String {
    return breedName
        .lowercase()
        .replace("_", " ")
        .replace("-", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
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
                statusMessage = "Connecting to Hugging Face API..."
                delay(800L)
                
                statusMessage = "Preparing image for analysis..."
                delay(600L)
                
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
                
                // Search for breed in background (no UI loading)
                println("🐕 DEBUG: Starting background breed search...")
                
                // First, let's test if we can get all breeds to see what's available
                println("🐕 DEBUG: Testing Dog API connection...")
                repository.getAllBreeds().fold(
                    onSuccess = { allBreeds ->
                        println("🐕 DEBUG: Dog API working! Found ${allBreeds.size} total breeds")
                        // Show first few breed names for reference
                        allBreeds.take(5).forEach { breed ->
                            println("🐕 DEBUG: Sample breed: '${breed.name}'")
                        }
                    },
                    onFailure = { error ->
                        println("🐕 DEBUG: Dog API connection failed: ${error.message}")
                    }
                )
                
                try {
                    val normalizedBreedName = normalizeBreedName(apiResult!!.breedName)
                    println("🐕 DEBUG: Original breed name: '${apiResult!!.breedName}'")
                    println("🐕 DEBUG: Normalized breed name: '$normalizedBreedName'")
                    
                    repository.searchBreeds(normalizedBreedName).fold(
                        onSuccess = { breeds ->
                            println("🐕 DEBUG: Search results for '$normalizedBreedName': ${breeds.size} breeds found")
                            breeds.forEach { breed ->
                                println("🐕 DEBUG: Found breed: '${breed.name}' (ID: ${breed.id})")
                            }
                            
                            if (breeds.isNotEmpty()) {
                                // Found matching breed, navigate to detail screen
                                val matchedBreed = breeds.first()
                                println("🐕 DEBUG: Navigating to breed detail for: '${matchedBreed.name}'")
                                delay(500L) // Small delay for smooth transition
                                onBreedFound(matchedBreed)
                            } else {
                                println("🐕 DEBUG: No breeds found for normalized name, trying original name...")
                                // Try searching with original name if normalized didn't work
                                repository.searchBreeds(apiResult!!.breedName).fold(
                                    onSuccess = { fallbackBreeds ->
                                        println("🐕 DEBUG: Fallback search results for '${apiResult!!.breedName}': ${fallbackBreeds.size} breeds found")
                                        if (fallbackBreeds.isNotEmpty()) {
                                            val matchedBreed = fallbackBreeds.first()
                                            println("🐕 DEBUG: Navigating to breed detail for: '${matchedBreed.name}' (fallback)")
                                            delay(500L)
                                            onBreedFound(matchedBreed)
                                        } else {
                                            println("🐕 DEBUG: No match found in fallback search, staying on current screen")
                                            // No match found, show prediction result
                                        }
                                    },
                                    onFailure = { error ->
                                        println("🐕 DEBUG: Fallback search failed: ${error.message}")
                                        // Show prediction result on error
                                    }
                                )
                            }
                        },
                        onFailure = { error ->
                            println("🐕 DEBUG: Primary search failed: ${error.message}")
                            // Show prediction result on error
                        }
                    )
                } catch (e: Exception) {
                    println("🐕 DEBUG: Exception during breed search: ${e.message}")
                    // Show prediction result on error
                }
                
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
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
                    imageVector = Icons.Default.ArrowBack,
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
                                    progress = predictionProgress / 100f,
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
                        
                        Button(
                            onClick = onBackPressed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BreedifyColors.Primary
                            )
                        ) {
                            Text("Try Again")
                        }
                    }
                    
                    predictionResult != null -> {
                        // Success state
                        Text(
                            text = "🐕",
                            fontSize = 48.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
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
                                    println("🐕 DEBUG: Learn More button clicked")
                                    // Search for breed and navigate to details
                                    scope.launch {
                                        predictionResult?.let { result ->
                                            println("🐕 DEBUG: Manual search for breed: '${result.breedName}'")
                                            val normalizedName = normalizeBreedName(result.breedName)
                                            println("🐕 DEBUG: Manual normalized name: '$normalizedName'")
                                            
                                            repository.searchBreeds(normalizedName).fold(
                                                onSuccess = { breeds ->
                                                    println("🐕 DEBUG: Manual search found ${breeds.size} breeds")
                                                    if (breeds.isNotEmpty()) {
                                                        println("🐕 DEBUG: Manual navigation to: '${breeds.first().name}'")
                                                        onBreedFound(breeds.first())
                                                    } else {
                                                        // Try with original name
                                                        repository.searchBreeds(result.breedName).fold(
                                                            onSuccess = { fallbackBreeds ->
                                                                println("🐕 DEBUG: Manual fallback found ${fallbackBreeds.size} breeds")
                                                                if (fallbackBreeds.isNotEmpty()) {
                                                                    println("🐕 DEBUG: Manual fallback navigation to: '${fallbackBreeds.first().name}'")
                                                                    onBreedFound(fallbackBreeds.first())
                                                                }
                                                            },
                                                            onFailure = { error ->
                                                                println("🐕 DEBUG: Manual fallback failed: ${error.message}")
                                                            }
                                                        )
                                                    }
                                                },
                                                onFailure = { error ->
                                                    println("🐕 DEBUG: Manual search failed: ${error.message}")
                                                }
                                            )
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
