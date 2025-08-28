package com.example.breedify.screens.cameraScreen

import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.breedify.R
import com.example.breedify.navigation.BreedifyBottomNavigation
import com.example.breedify.screens.homeScreen.BreedifyColors

// Upload states
enum class UploadState {
    IDLE,
    UPLOADING,
    PROCESSING,
    COMPLETED
}

@Composable
fun DogBreedIdentificationScreen(
    onNavigate: (String) -> Unit,
    onTakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit,
    onChatbotClick: () -> Unit = {}
) {
    var uploadState by remember { mutableStateOf(UploadState.IDLE) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadedFileName by remember { mutableStateOf("") }
    var predictedBreed by remember { mutableStateOf("") }
    var confidenceScore by remember { mutableStateOf(0) }

    // Simulate upload process
    LaunchedEffect(uploadState) {
        if (uploadState == UploadState.UPLOADING) {
            // Simulate upload progress
            for (i in 0..100) {
                uploadProgress = i / 100f
                delay(30) // Simulate upload time
            }
            uploadState = UploadState.PROCESSING
            delay(1500) // Simulate processing time
            uploadState = UploadState.COMPLETED
            predictedBreed = "Golden Retriever"
            confidenceScore = 92
        }
    }

    Scaffold(
        bottomBar = {
            BreedifyBottomNavigation(
                currentRoute = "camera",
                onNavigate = onNavigate,
                onChatbotClick = onChatbotClick
            )
        },
        containerColor = BreedifyColors.Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BreedifyColors.Background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
        item {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(
                    text = "Spotted a dog?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = BreedifyColors.TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Unsure about its breed? We've got you covered with our Ml Model",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BreedifyColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        item {
            // Image Upload Section with States
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BreedifyColors.CardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                when (uploadState) {
                    UploadState.IDLE -> {
                        // Initial state - Tap to upload
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "☁️",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Tap to upload photo",
                                style = MaterialTheme.typography.titleMedium,
                                color = BreedifyColors.Primary,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "PNG, JPG or PDF (max. 800x400px)",
                                style = MaterialTheme.typography.bodySmall,
                                color = BreedifyColors.TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "OR",
                                style = MaterialTheme.typography.bodySmall,
                                color = BreedifyColors.TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    UploadState.UPLOADING -> {
                        // Uploading state with progress
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📄",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "${(uploadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineMedium,
                                color = BreedifyColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = uploadProgress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = BreedifyColors.Primary,
                                trackColor = BreedifyColors.Primary.copy(alpha = 0.2f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Uploading Document...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BreedifyColors.TextPrimary,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = uploadedFileName.ifEmpty { "myDCard.jpg" },
                                style = MaterialTheme.typography.bodySmall,
                                color = BreedifyColors.TextSecondary
                            )
                        }
                    }

                    UploadState.PROCESSING -> {
                        // Processing state
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Looking for dog breed...",
                                style = MaterialTheme.typography.titleMedium,
                                color = BreedifyColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = BreedifyColors.Secondary,
                                trackColor = BreedifyColors.Secondary.copy(alpha = 0.2f)
                            )
                        }
                    }

                    UploadState.COMPLETED -> {
                        // Upload complete state
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        BreedifyColors.Secondary.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✅",
                                    fontSize = 32.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Upload Complete",
                                style = MaterialTheme.typography.titleMedium,
                                color = BreedifyColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = uploadedFileName.ifEmpty { "myDCard.jpg" },
                                style = MaterialTheme.typography.bodySmall,
                                color = BreedifyColors.TextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { 
                                    uploadState = UploadState.IDLE
                                    uploadProgress = 0f
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("🗑️ Clear Upload")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload buttons - only show when idle
            if (uploadState == UploadState.IDLE) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onTakePhoto()
                            uploadedFileName = "camera_photo.jpg"
                            uploadState = UploadState.UPLOADING
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BreedifyColors.Primary
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.god_camera_icon),
                            contentDescription = "Camera",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open camera")
                    }

                    OutlinedButton(
                        onClick = {
                            onUploadPhoto()
                            uploadedFileName = "gallery_photo.jpg"
                            uploadState = UploadState.UPLOADING
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gallery_icon),
                            contentDescription = "Gallery",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gallery")
                    }
                }
            }
        }

        if (uploadState == UploadState.COMPLETED && predictedBreed.isNotEmpty()) {
            item {
                // Prediction Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Prediction Result",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = predictedBreed,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$confidenceScore% accurate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onNavigate("dog_detail") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Details")
                        }
                    }
                }
            }

            item {
                // Gemini Option
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Our model might be wrong sometimes. Want a second opinion?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { onNavigate("gemini_prediction") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ask Gemini")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🔍")
                        }
                    }
                }
            }
        }

        item {
            // Gemini AI Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BreedifyColors.CardBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need a Second Opinion?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BreedifyColors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Our model can be wrong sometimes. Get more accurate results using advanced AI technology for breed identification.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BreedifyColors.TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = { /* TODO: Implement Gemini functionality */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BreedifyColors.Primary
                        )
                    ) {
                        Text(
                            text = "Check Breed with Gemini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            // ML Model Features Carousel Section
            Column {
                Text(
                    text = "Our Model Features",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BreedifyColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ModelFeaturesCarousel()
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        }
    }
}

data class ModelFeature(
    val icon: String,
    val title: String,
    val description: String
)

@Composable
fun ModelFeaturesCarousel() {
    val features = listOf(
        ModelFeature(
            icon = "🐾",
            title = "150+ Dog Breeds",
            description = "Our advanced ML model can identify over 150 different dog breeds with remarkable precision and accuracy."
        ),
        ModelFeature(
            icon = "⚡",
            title = "Lightning Fast",
            description = "Get instant results in under 2 seconds! Perfect for quick breed identification on the go."
        ),
        ModelFeature(
            icon = "✅",
            title = "High Accuracy",
            description = "Trained on millions of dog images, achieving over 90% accuracy in real-world testing scenarios."
        ),
        ModelFeature(
            icon = "📶",
            title = "Works Offline",
            description = "No internet needed! Our model runs entirely on your device, ensuring privacy and consistency."
        ),
        ModelFeature(
            icon = "🎯",
            title = "Breed Details",
            description = "Get comprehensive information about each breed including characteristics and temperament."
        ),
        ModelFeature(
            icon = "🔄",
            title = "Continuous Learning",
            description = "Our model is regularly updated with new data to improve accuracy and add more breeds."
        )
    )

    val pagerState = rememberPagerState(pageCount = { features.size })
    var lastUserInteraction by remember { mutableStateOf(System.currentTimeMillis()) }

    // Track user interaction
    LaunchedEffect(pagerState.isScrollInProgress) {
        if (pagerState.isScrollInProgress) {
            lastUserInteraction = System.currentTimeMillis()
        }
    }

    // Auto-scroll effect - simplified logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500) // Wait 2.5 seconds
            
            // Check if user hasn't interacted recently and pager is not currently scrolling
            val timeSinceInteraction = System.currentTimeMillis() - lastUserInteraction
            if (timeSinceInteraction >= 2500 && !pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % features.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 48.dp),
            pageSpacing = 16.dp
        ) { page ->
            FeatureCard(feature = features[page])
        }

        // Page indicators
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(features.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .background(
                            color = if (isSelected) BreedifyColors.Primary else BreedifyColors.Primary.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
                if (index < features.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: ModelFeature) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BreedifyColors.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Big feature icon
            Text(
                text = feature.icon,
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Feature title
            Text(
                text = feature.title,
                style = MaterialTheme.typography.headlineSmall,
                color = BreedifyColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Feature description
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodyLarge,
                color = BreedifyColors.TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun FeatureItem(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 24.sp,
                modifier = Modifier.size(40.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DogBreedIdentificationScreenPreview() {
    MaterialTheme {
        DogBreedIdentificationScreen(
            onNavigate = { },
            onTakePhoto = { },
            onUploadPhoto = { }
        )
    }
}