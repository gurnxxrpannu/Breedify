package com.example.breedify

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.breedify.screens.welcomeScreen.WelcomeScreen
import com.example.breedify.screens.homeScreen.HomeScreen
import com.example.breedify.screens.exploreScreen.ExploreScreen
import com.example.breedify.screens.prediction.MLPredictionScreen
import com.example.breedify.screens.dogDetailScreen.DogDetailScreen
import com.example.breedify.screens.chatbotScreen.ChatbotScreen
import com.example.breedify.screens.favoritesScreen.FavoritesScreen
import com.example.breedify.data.api.Breed
import com.example.breedify.screens.cameraScreen.DogBreedIdentificationScreen
import com.example.breedify.ui.theme.BreedifyTheme
import com.example.breedify.utils.CameraUtils

class MainActivity : ComponentActivity() {
    
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            handleSelectedFile(selectedUri)
        }
    }
    
    private var onFileSelectedCallback: ((Uri) -> Unit)? = null
    
    private fun handleSelectedFile(uri: Uri) {
        try {
            // Copy the selected file to the camera storage directory
            val copiedUri = CameraUtils.copyFileToBreedifyDirectory(uri, this)
            if (copiedUri != null) {
                onFileSelectedCallback?.invoke(copiedUri)
                Toast.makeText(this, "File uploaded successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to upload file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error uploading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openFilePicker(onFileSelected: (Uri) -> Unit) {
        onFileSelectedCallback = onFileSelected
        filePickerLauncher.launch("image/*")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreedifyTheme {
                var showWelcomeScreen by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf("home") }
                var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
                var selectedBreed by remember { mutableStateOf<Breed?>(null) }
                val context = LocalContext.current
                
                if (showWelcomeScreen) {
                    WelcomeScreen(
                        onGetStarted = { showWelcomeScreen = false }
                    )
                } else {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigate = { route -> currentScreen = route },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                currentScreen = "dog_detail"
                            },
                            onOpenCamera = { currentScreen = "camera" },
                            onUploadPhoto = {
                                openFilePicker { uri ->
                                    capturedImageUri = uri
                                    // Process image for ML classification
                                    val processedBitmap = CameraUtils.processImageForML(uri, context)
                                    if (processedBitmap != null) {
                                        Toast.makeText(context, "Image uploaded and processed for ML classification!", Toast.LENGTH_LONG).show()
                                        currentScreen = "prediction"
                                    } else {
                                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onChatbotClick = { currentScreen = "chatbot" }
                        )
                        "explore" -> ExploreScreen(
                            onNavigate = { route -> currentScreen = route },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                currentScreen = "dog_detail"
                            },
                            onChatbotClick = { currentScreen = "chatbot" }
                        )
                        "dog_detail" -> selectedBreed?.let { breed ->
                            DogDetailScreen(
                                breed = breed,
                                onBackClick = { 
                                    // Go back to the previous screen (could be home, explore, favorites, or prediction)
                                    currentScreen = when {
                                        capturedImageUri != null -> "prediction" // If we came from ML prediction
                                        else -> "home" // Default to home
                                    }
                                }
                            )
                        }
                        "camera" -> DogBreedIdentificationScreen(
                            onNavigate = { route -> currentScreen = route },
                            onTakePhoto = {
                                Toast.makeText(context, "Camera functionality coming soon!", Toast.LENGTH_SHORT).show()
                            },
                            onUploadPhoto = {
                                openFilePicker { uri ->
                                    capturedImageUri = uri
                                    currentScreen = "prediction"
                                }
                            },
                            onChatbotClick = { currentScreen = "chatbot" }
                        )
                        "favorites" -> FavoritesScreen(
                            onNavigate = { route -> currentScreen = route },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                currentScreen = "dog_detail"
                            },
                            onChatbotClick = { currentScreen = "chatbot" }
                        )
                        "chatbot" -> ChatbotScreen(
                            onNavigateBack = { currentScreen = "home" }
                        )
                        "prediction" -> capturedImageUri?.let { uri ->
                            MLPredictionScreen(
                                imageUri = uri,
                                onBackPressed = { currentScreen = "home" },
                                onPredictionComplete = { result ->
                                    Toast.makeText(context, "Breed identified: ${result.breedName}", Toast.LENGTH_LONG).show()
                                },
                                onBreedFound = { breed ->
                                    selectedBreed = breed
                                    currentScreen = "dog_detail"
                                }
                            )
                        }

                        else -> HomeScreen(
                            onNavigate = { route -> currentScreen = route },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                currentScreen = "dog_detail"
                            },
                            onOpenCamera = { currentScreen = "camera" },
                            onUploadPhoto = {
                                openFilePicker { uri ->
                                    capturedImageUri = uri
                                    currentScreen = "prediction"
                                }
                            },
                            onChatbotClick = { currentScreen = "chatbot" }
                        )
                    }
                }
            }
        }
    }
}


// Old MainContent removed - now using HomeScreen directly