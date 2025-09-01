package com.example.breedify.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.breedify.data.api.DogFact
import com.example.breedify.data.repository.DogFactsRepository
import com.example.breedify.screens.homeScreen.BreedifyColors
import com.example.breedify.utils.Logger
import com.example.breedify.utils.onError
import com.example.breedify.utils.onSuccess
import kotlinx.coroutines.launch

@Composable
fun FunFactsCarousel(
    modifier: Modifier = Modifier
) {
    var facts by remember { mutableStateOf<List<DogFact>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    
    val repository = remember { DogFactsRepository() }
    val scope = rememberCoroutineScope()
    
    // Load facts when component is first created
    LaunchedEffect(Unit) {
        scope.launch {
            Logger.d("Starting to fetch dog facts...", "FunFactsCarousel")
            val result = repository.getRandomFacts(10)
            result.onSuccess { fetchedFacts ->
                Logger.d("Successfully fetched ${fetchedFacts.size} facts", "FunFactsCarousel")
                fetchedFacts.forEachIndexed { index, fact ->
                    Logger.d("Fact $index: ${fact.getFactText()}", "FunFactsCarousel")
                }
                if (fetchedFacts.isNotEmpty()) {
                    facts = fetchedFacts
                    hasError = false
                } else {
                    Logger.w("API returned empty facts list", "FunFactsCarousel")
                    hasError = true
                }
                isLoading = false
            }.onError { exception, message ->
                Logger.e("Failed to fetch facts: $message", exception, "FunFactsCarousel")
                hasError = true
                isLoading = false
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🐾 Fun Facts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BreedifyColors.TextPrimary
            )
            
            if (facts.isNotEmpty()) {
                Text(
                    text = "${currentIndex + 1}/${facts.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BreedifyColors.TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BreedifyColors.Primary
                    )
                }
            }
            
            hasError || facts.isEmpty() -> {
                ErrorCard(
                    message = "Sorry! There are no fun facts today",
                    onRetry = {
                        Logger.d("Retrying to fetch dog facts...", "FunFactsCarousel")
                        isLoading = true
                        hasError = false
                        scope.launch {
                            val result = repository.getRandomFacts(10)
                            result.onSuccess { fetchedFacts ->
                                Logger.d("Retry successful: ${fetchedFacts.size} facts", "FunFactsCarousel")
                                if (fetchedFacts.isNotEmpty()) {
                                    facts = fetchedFacts
                                    hasError = false
                                } else {
                                    Logger.w("Retry returned empty facts list", "FunFactsCarousel")
                                    hasError = true
                                }
                                isLoading = false
                            }.onError { exception, message ->
                                Logger.e("Retry failed: $message", exception, "FunFactsCarousel")
                                hasError = true
                                isLoading = false
                            }
                        }
                    }
                )
            }
            
            facts.isNotEmpty() -> {
                SwipeableFactCards(
                    facts = facts,
                    currentIndex = currentIndex,
                    onIndexChange = { newIndex ->
                        currentIndex = newIndex
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFE5E5), Color(0xFFFFB6C1))
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🐕",
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = message,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreedifyColors.Primary
                    )
                ) {
                    Text(
                        text = "Try Again",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeableFactCards(
    facts: List<DogFact>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    
    // Create a smooth transition offset that resets after animation
    val transitionOffset by animateFloatAsState(
        targetValue = if (isAnimating) 0f else dragOffset,
        animationSpec = tween(400),
        finishedListener = { isAnimating = false },
        label = "transitionOffset"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .pointerInput(currentIndex) {
                detectDragGestures(
                    onDragEnd = {
                        val threshold = with(density) { 100.dp.toPx() }
                        when {
                            dragOffset > threshold && currentIndex > 0 -> {
                                isAnimating = true
                                onIndexChange(currentIndex - 1)
                            }
                            dragOffset < -threshold && currentIndex < facts.size - 1 -> {
                                isAnimating = true
                                onIndexChange(currentIndex + 1)
                            }
                        }
                        dragOffset = 0f
                    }
                ) { _, dragAmount ->
                    if (!isAnimating) {
                        dragOffset += dragAmount.x
                    }
                }
            }
    ) {
        // Show 3 cards: previous, current, next with smooth transitions
        for (i in maxOf(0, currentIndex - 1)..minOf(facts.size - 1, currentIndex + 1)) {
            val isCenter = i == currentIndex
            val isLeft = i < currentIndex
            val isRight = i > currentIndex
            
            // Calculate positions with smooth transitions
            val baseOffsetX = when {
                isLeft -> -120f
                isRight -> 120f
                else -> 0f
            }
            
            val offsetX by animateFloatAsState(
                targetValue = baseOffsetX + (if (isCenter) transitionOffset * 0.8f else transitionOffset * 0.3f),
                animationSpec = tween(400),
                label = "offsetX_$i"
            )
            
            val rotation by animateFloatAsState(
                targetValue = when {
                    isLeft -> -15f + transitionOffset * 0.01f
                    isRight -> 15f + transitionOffset * 0.01f
                    else -> transitionOffset * 0.02f
                },
                animationSpec = tween(400),
                label = "rotation_$i"
            )
            
            val scale by animateFloatAsState(
                targetValue = if (isCenter) 1f else 0.85f,
                animationSpec = tween(400),
                label = "scale_$i"
            )
            
            val alpha by animateFloatAsState(
                targetValue = if (isCenter) 1f else 0.8f,
                animationSpec = tween(400),
                label = "alpha_$i"
            )
            
            val zIndex = when {
                isCenter -> 2f
                isLeft -> 1f
                isRight -> 0f
                else -> 0f
            }
            
            key("${i}_${facts[i].getFactText()}") {
                FactCard(
                    fact = facts[i],
                    modifier = Modifier
                        .zIndex(zIndex)
                        .graphicsLayer {
                            translationX = offsetX
                            rotationZ = rotation
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                    isCenter = isCenter
                )
            }
        }
    }
}

@Composable
private fun FactCard(
    fact: DogFact,
    modifier: Modifier = Modifier,
    isCenter: Boolean = false
) {
    val cardColors = listOf(
        listOf(Color(0xFFFFE5B4), Color(0xFFFFD700)), // Golden
        listOf(Color(0xFFE5F3FF), Color(0xFF87CEEB)), // Sky Blue
        listOf(Color(0xFFFFE5E5), Color(0xFFFFB6C1)), // Pink
        listOf(Color(0xFFE5FFE5), Color(0xFF98FB98)), // Light Green
        listOf(Color(0xFFF0E5FF), Color(0xFFDDA0DD))  // Lavender
    )
    
    // Use fact content hash to determine color consistently (with null safety)
    val colorIndex = remember(fact) { 
        val factText = fact.getFactText()
        factText.hashCode().mod(cardColors.size).let { if (it < 0) it + cardColors.size else it } 
    }
    val gradient = cardColors[colorIndex]
    
    // Animate content changes
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isCenter) 1f else 0.7f,
        animationSpec = tween(300),
        label = "contentAlpha"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = if (isCenter) 0.dp else 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCenter) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradient)
                )
                .padding(20.dp)
        ) {
            // Decorative elements
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(30.dp)
                    )
                    .align(Alignment.TopEnd)
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    )
                    .align(Alignment.BottomStart)
            )
            
            // Heart icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(18.dp)
                    )
                    .align(Alignment.TopEnd)
                    .offset((-12).dp, 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "❤️",
                    fontSize = 16.sp
                )
            }
            
            // Fact content with animation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
                    .graphicsLayer {
                        alpha = animatedAlpha
                    },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Did you know?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = fact.getFactText(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.9f),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}