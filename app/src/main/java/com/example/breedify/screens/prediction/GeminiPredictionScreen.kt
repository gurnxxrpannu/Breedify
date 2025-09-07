package com.example.breedify.screens.prediction

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.breedify.data.api.Breed
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.data.repository.GeminiRepository
import com.example.breedify.screens.homeScreen.BreedifyColors
import com.example.breedify.utils.CameraUtils
import com.example.breedify.utils.Logger
import kotlinx.coroutines.launch
import com.example.breedify.BuildConfig
import com.example.breedify.data.model.PredictionResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiPredictionScreen(
    imageUri: Uri,
    onBackPressed: () -> Unit,
    onPredictionComplete: (PredictionResult) -> Unit,
    onBreedFound: (Breed) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiRepository = remember { GeminiRepository() }
    val dogRepository = remember { DogRepository() }
    
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var predictionResult by remember { mutableStateOf<PredictionResult?>(null) }
    var breedInfo by remember { mutableStateOf<String?>(null) }
    
    // Helper function to parse breed name from Gemini response
    fun parseBreedName(result: String): String {
        // Try to extract breed name from the formatted response
        val breedLine = result.lines().find { it.startsWith("Breed:", ignoreCase = true) }
            ?: return result.lines().firstOrNull()?.takeIf { it.isNotBlank() } ?: "Unknown Breed"
            
        return breedLine.substringAfter(":").trim()
    }
    
    // Helper function to parse confidence from Gemini response
    fun parseConfidence(result: String): Float {
        // Try to extract confidence percentage from the response
        val confidenceLine = result.lines().find { 
            it.startsWith("Confidence:", ignoreCase = true) || 
            it.contains("%", ignoreCase = true)
        } ?: return 0.7f  // Default confidence if not found
        
        // Extract the first number from the confidence line
        val percentage = "\\d+\\.?\\d*".toRegex()
            .find(confidenceLine)?.value?.toFloatOrNull()
            ?.coerceIn(0f, 100f)  // Ensure it's a valid percentage
            
        return (percentage ?: 70f) / 100f  // Default to 70% if parsing fails
    }
    
    // Function to analyze image using Gemini API
    suspend fun analyzeImage() {
        try {
            isLoading = true
            error = null
            
            // Process image for Gemini API
            val bitmap = CameraUtils.processImageForGemini(imageUri, context)
                ?: throw Exception("Could not process image")
            
            // Get prediction from Gemini
            val result = geminiRepository.identifyDogBreed(bitmap).getOrThrow()
            
            // Parse the result
            val breedName = parseBreedName(result)
            val confidence = parseConfidence(result)
            
            // Create prediction result
            val resultObj = PredictionResult(
                breedName = breedName,
                confidence = confidence
            )
            predictionResult = resultObj
            onPredictionComplete(resultObj)
            
            // Try to find breed in our database
            try {
                dogRepository.searchBreeds(breedName).onSuccess { breeds ->
                    if (breeds.isNotEmpty()) {
                        onBreedFound(breeds[0])
                    } else {
                        // If breed not found, try to get info from Gemini
                        coroutineScope.launch {
                            val info = geminiRepository.getBreedInformation(breedName).getOrNull()
                            breedInfo = info
                        }
                    }
                }.onFailure { e ->
                    Logger.e("Error searching breeds", e, "GeminiPredictionScreen")
                    breedInfo = "Error searching for breed information."
                }
            } catch (e: Exception) {
                Logger.e("Error getting breed info", e, "GeminiPredictionScreen")
                breedInfo = "Could not find detailed information about this breed in our database."
            }
            
        } catch (e: Exception) {
            error = e.message ?: "An unknown error occurred"
            Logger.e("Prediction error", e, "GeminiPredictionScreen")
        } finally {
            isLoading = false
        }
    }
    
    // Launch the analysis when the screen is first displayed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            analyzeImage()
        }
    }
    
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Breed Analysis",
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the captured image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Captured image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Loading/Error/Prediction content
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = BreedifyColors.Primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Analyzing image with Gemini AI...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            error ?: "An unknown error occurred",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { coroutineScope.launch { analyzeImage() } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BreedifyColors.Primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Try Again")
                        }
                    }
                }
                
                predictionResult != null -> {
                    val result = predictionResult!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Prediction result
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "AI Analysis Result",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BreedifyColors.Primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Breed name
                                Text(
                                    "Breed: ${result.breedName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                // Confidence
                                Text(
                                    "Confidence: ${(result.confidence * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                // Additional info if available
                                val currentBreedInfo = breedInfo
                                if (currentBreedInfo != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        currentBreedInfo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Note: This breed was not found in our database. The information shown is from the AI analysis.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                            }
                        }

                        // Raw response (for debugging) 
                        if (BuildConfig.DEBUG) {
                            Text(
                                "Debug: AI Analysis completed successfully",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
