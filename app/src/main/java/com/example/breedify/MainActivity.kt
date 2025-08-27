package com.example.breedify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breedify.screens.welcomeScreen.WelcomeScreen
import com.example.breedify.screens.homeScreen.HomeScreen
import com.example.breedify.screens.exploreScreen.ExploreScreen
import com.example.breedify.ui.theme.BreedifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreedifyTheme {
                var showWelcomeScreen by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf("home") }
                
                if (showWelcomeScreen) {
                    WelcomeScreen(
                        onGetStarted = { showWelcomeScreen = false }
                    )
                } else {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigate = { route -> currentScreen = route }
                        )
                        "explore" -> ExploreScreen(
                            onNavigate = { route -> currentScreen = route }
                        )
                     //   "camera" -> CameraScreen(
                     //       onNavigate = { route -> currentScreen = route }
                      //  )
                      //  "profile" -> ProfileScreen(
                      //      onNavigate = { route -> currentScreen = route }
                       // )
                        else -> HomeScreen(
                            onNavigate = { route -> currentScreen = route }
                        )
                    }
                }
            }
        }
    }
}



// Old MainContent removed - now using HomeScreen directly