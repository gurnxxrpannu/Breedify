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
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.breedify.screens.welcomeScreen.WelcomeScreen
import com.example.breedify.screens.homeScreen.HomeScreen
import com.example.breedify.screens.exploreScreen.ExploreScreen
import com.example.breedify.screens.prediction.MLPredictionScreen
import com.example.breedify.screens.prediction.GeminiPredictionScreen
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
        if (uri != null) {
            handleSelectedFile(uri)
        } else {
            // User cancelled the picker
            onFileSelectedCallback?.invoke(null)
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            handleSelectedFile(tempCameraUri!!)
        } else {
            onFileSelectedCallback?.invoke(null)
        }
    }
    
    private var onFileSelectedCallback: ((Uri?) -> Unit)? = null
    private var tempCameraUri: Uri? = null
    
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
    
    private fun openFilePicker(onFileSelected: (Uri?) -> Unit) {
        onFileSelectedCallback = onFileSelected
        filePickerLauncher.launch("image/*")
    }
    
    private fun openCamera(onFileSelected: (Uri?) -> Unit) {
        try {
            onFileSelectedCallback = onFileSelected
            val imageFile = CameraUtils.createImageFile(this)
            tempCameraUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                imageFile
            )
            cameraLauncher.launch(tempCameraUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening camera: ${e.message}", Toast.LENGTH_SHORT).show()
            onFileSelected(null)
        }
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
                var previousScreen by remember { mutableStateOf("home") }
                val context = LocalContext.current
                
                if (showWelcomeScreen) {
                    WelcomeScreen(
                        onGetStarted = { showWelcomeScreen = false }
                    )
                } else {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigate = { route -> 
                                previousScreen = currentScreen
                                currentScreen = route 
                            },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                previousScreen = currentScreen
                                currentScreen = "dog_detail"
                            },
                            onOpenCamera = { 
                                previousScreen = currentScreen
                                currentScreen = "camera" 
                            },
                            onUploadPhoto = {
                                openFilePicker { uri ->
                                    if (uri != null) {
                                        capturedImageUri = uri
                                        // Process image for ML classification
                                        val processedBitmap = CameraUtils.processImageForML(uri, context)
                                        if (processedBitmap != null) {
                                            Toast.makeText(context, "Image uploaded and processed for ML classification!", Toast.LENGTH_LONG).show()
                                            // Show options for ML or Gemini prediction
                                            val options = arrayOf("Use ML Model", "Use Gemini AI")
                                            android.app.AlertDialog.Builder(context)
                                                .setTitle("Choose Prediction Method")
                                                .setItems(options) { _, which ->
                                                    previousScreen = currentScreen
                                                    currentScreen = if (which == 0) "prediction" else "gemini_prediction"
                                                }
                                                .setNegativeButton("Cancel", null)
                                                .show()
                                        } else {
                                            Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    // If uri is null, user cancelled - do nothing
                                }
                            },
                            onChatbotClick = { 
                                previousScreen = currentScreen
                                currentScreen = "chatbot" 
                            }
                        )
                        "explore" -> ExploreScreen(
                            onNavigate = { route -> 
                                previousScreen = currentScreen
                                currentScreen = route 
                            },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                previousScreen = currentScreen
                                currentScreen = "dog_detail"
                            },
                            onChatbotClick = { 
                                previousScreen = currentScreen
                                currentScreen = "chatbot" 
                            }
                        )
                        "dog_detail" -> selectedBreed?.let { breed ->
                            DogDetailScreen(
                                breed = breed,
                                onBackClick = { 
                                    // Go back to the previous screen
                                    currentScreen = previousScreen
                                }
                            )
                        }
                        "camera" -> {
                            // Reset image when entering camera screen from a different screen
                            LaunchedEffect(currentScreen) {
                                if (previousScreen != "camera") {
                                    capturedImageUri = null
                                }
                                previousScreen = currentScreen
                            }
                            
                            DogBreedIdentificationScreen(
                                onNavigate = { route -> 
                                    previousScreen = currentScreen
                                    currentScreen = route 
                                },
                                onTakePhoto = { onResult ->
                                    openCamera { uri ->
                                        capturedImageUri = uri
                                        onResult(uri != null)
                                    }
                                },
                                onUploadPhoto = { onResult ->
                                    openFilePicker { uri ->
                                        capturedImageUri = uri
                                        onResult(uri != null)
                                    }
                                },
                                onChatbotClick = { 
                                    previousScreen = currentScreen
                                    currentScreen = "chatbot"
                                },
                                onGeminiAnalysis = { onResult ->
                                    // Show options for camera or gallery for Gemini analysis
                                    val options = arrayOf("Take Photo", "Choose from Gallery")
                                    android.app.AlertDialog.Builder(context)
                                        .setTitle("Select Image for Gemini Analysis")
                                        .setItems(options) { _, which ->
                                            if (which == 0) {
                                                // Take photo
                                                openCamera { uri ->
                                                    capturedImageUri = uri
                                                    onResult(uri != null)
                                                }
                                            } else {
                                                // Choose from gallery
                                                openFilePicker { uri ->
                                                    capturedImageUri = uri
                                                    onResult(uri != null)
                                                }
                                            }
                                        }
                                        .setNegativeButton("Cancel") { _, _ ->
                                            onResult(false)
                                        }
                                        .show()
                                }
                            )
                        }
                        "favorites" -> FavoritesScreen(
                            onNavigate = { route -> 
                                previousScreen = currentScreen
                                currentScreen = route 
                            },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                previousScreen = currentScreen
                                currentScreen = "dog_detail"
                            },
                            onChatbotClick = { 
                                previousScreen = currentScreen
                                currentScreen = "chatbot" 
                            }
                        )
                        "chatbot" -> ChatbotScreen(
                            onNavigateBack = { currentScreen = previousScreen }
                        )
                        "prediction" -> capturedImageUri?.let { uri ->
                            MLPredictionScreen(
                                imageUri = uri,
                                onBackPressed = { 
                                    // Go back to camera screen when coming from prediction
                                    currentScreen = if (previousScreen == "camera") "camera" else previousScreen
                                },
                                onPredictionComplete = { result ->
                                    Toast.makeText(context, "Breed identified: ${result.breedName}", Toast.LENGTH_LONG).show()
                                },
                                onBreedFound = { breed ->
                                    selectedBreed = breed
                                    previousScreen = currentScreen
                                    currentScreen = "dog_detail"
                                }
                            )
                        }
                        "gemini_prediction" -> capturedImageUri?.let { uri ->
                            GeminiPredictionScreen(
                                imageUri = uri,
                                onBackPressed = { 
                                    // Always go back to camera screen when coming from Gemini prediction
                                    currentScreen = "camera"
                                },
                                onPredictionComplete = { result ->
                                    Toast.makeText(context, "AI identified: ${result.breedName}", Toast.LENGTH_LONG).show()
                                },
                                onBreedFound = { breed ->
                                    selectedBreed = breed
                                    previousScreen = currentScreen
                                    currentScreen = "dog_detail"
                                }
                            )
                        }

                        else -> HomeScreen(
                            onNavigate = { route -> 
                                previousScreen = currentScreen
                                currentScreen = route 
                            },
                            onBreedClick = { breed ->
                                selectedBreed = breed
                                previousScreen = currentScreen
                                currentScreen = "dog_detail"
                            },
                            onOpenCamera = { 
                                previousScreen = currentScreen
                                currentScreen = "camera" 
                            },
                            onUploadPhoto = {
                                openFilePicker { uri ->
                                    capturedImageUri = uri
                                    previousScreen = currentScreen
                                    currentScreen = "prediction"
                                }
                            },
                            onChatbotClick = { 
                                previousScreen = currentScreen
                                currentScreen = "chatbot" 
                            }
                        )
                    }
                }
            }
        }
    }
}


// Old MainContent removed - now using HomeScreen directly