package com.example.breedify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breedify.screens.welcomeScreen.WelcomeScreen
import com.example.breedify.screens.homeScreen.HomeScreen
import com.example.breedify.screens.exploreScreen.ExploreScreen
import com.example.breedify.screens.prediction.MLPredictionScreen
import com.example.breedify.screens.dogDetailScreen.DogDetailScreen
import com.example.breedify.screens.chatbotScreen.ChatbotScreen
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
                                        // TODO: Send to ML model for breed classification
                                    } else {
                                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
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
                                onBackClick = { currentScreen = "home" }
                            )
                        }
                        "camera" -> DogBreedIdentificationScreen(
                            onNavigate = { route -> currentScreen = route },
                            onTakePhoto = {
                                // TODO: Implement camera capture functionality
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
                        "chatbot" -> ChatbotScreen(
                            onNavigateBack = { currentScreen = "home" }
                        )
                        "prediction" -> capturedImageUri?.let { uri ->
                            MLPredictionScreen(
                                imageUri = uri,
                                onBackPressed = { currentScreen = "home" },
                                onPredictionComplete = { result ->
                                    Toast.makeText(context, "Breed identified: ${result.breedName}", Toast.LENGTH_LONG).show()
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
                            }
                        )
                    }
                }
            }
        }
    }
}


// Old MainContent removed - now using HomeScreen directly