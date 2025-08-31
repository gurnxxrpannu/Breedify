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
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.random.Random

data class PredictionResult(
    val breedName: String,
    val confidence: Float
)

@Composable
fun MLPredictionScreen(
    imageUri: Uri,
    onBackPressed: () -> Unit,
    onPredictionComplete: (PredictionResult) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Initializing...") }
    var predictionProgress by remember { mutableIntStateOf(0) }
    var predictionResult by remember { mutableStateOf<PredictionResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
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
                        // Loading state with progress counter and dog paw animation
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PredictionLoadingAnimation(
                                statusMessage = "Analyzing breed...",
                                subMessage = "AI model is processing your image",
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            if (predictionProgress > 0) {
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Progress counter
                                Text(
                                    text = "$predictionProgress%",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 48.sp
                                    ),
                                    color = BreedifyColors.Primary
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = statusMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BreedifyColors.TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Progress bar
                                LinearProgressIndicator(
                                    progress = predictionProgress / 100f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = BreedifyColors.Primary,
                                    trackColor = BreedifyColors.Primary.copy(alpha = 0.2f)
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
                                onClick = { /* TODO: Navigate to breed details */ },
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
