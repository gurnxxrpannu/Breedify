package com.example.breedify

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.breedify.screens.camera.CameraScreen
import com.example.breedify.ui.theme.BreedifyTheme
import com.example.breedify.utils.CameraUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreedifyTheme {
                var showWelcomeScreen by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf("home") }
                var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
                val context = LocalContext.current
                
                if (showWelcomeScreen) {
                    WelcomeScreen(
                        onGetStarted = { showWelcomeScreen = false }
                    )
                } else {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigate = { route -> currentScreen = route },
                            onOpenCamera = { currentScreen = "camera" },
                            onUploadPhoto = {
                                // TODO: Implement photo upload from gallery
                                Toast.makeText(context, "Photo upload coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        "explore" -> ExploreScreen(
                            onNavigate = { route -> currentScreen = route }
                        )
                        "camera" -> CameraScreen(
                            onImageCaptured = { uri ->
                                capturedImageUri = uri
                                // Process image for ML classification
                                val processedBitmap = CameraUtils.processImageForML(uri, context)
                                if (processedBitmap != null) {
                                    Toast.makeText(context, "Image captured and processed for ML classification!", Toast.LENGTH_LONG).show()
                                    // TODO: Send to ML model for breed classification
                                } else {
                                    Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                                }
                                currentScreen = "home"
                            },
                            onBackPressed = { currentScreen = "home" }
                        )
                        else -> HomeScreen(
                            onNavigate = { route -> currentScreen = route },
                            onOpenCamera = { currentScreen = "camera" },
                            onUploadPhoto = {
                                Toast.makeText(context, "Photo upload coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}


// Old MainContent removed - now using HomeScreen directly