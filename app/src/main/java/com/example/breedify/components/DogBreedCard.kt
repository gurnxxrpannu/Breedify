package com.example.breedify.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.breedify.data.api.Breed
import android.util.Log

@Composable
fun DogBreedCard(
    breed: Breed,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCenter: Boolean = false,
    isFocused: Boolean = true
) {
    // Calculate dimensions based on focus state
    val cardWidth = if (isFocused) 165.dp else 130.dp
    val cardHeight = if (isFocused) 220.dp else 180.dp // Reduced height since we only show name and image
    val imageHeight = if (isFocused) 165.dp else 130.dp

    // 1. TAP ANIMATION using interaction source
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tap_scale"
    )

    // 2. 3D ROTATION (only for center card)
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_transition")
    val animatedRotationY by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation_y"
    )

    // 3. ENTRANCE ANIMATION
    var isVisible by remember { mutableStateOf(false) }
    val slideOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "entrance_slide"
    )
    val entranceAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "entrance_alpha"
    )

    // Trigger entrance animation
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                // Tap animation
                scaleX = animatedScale
                scaleY = animatedScale
                // 3D rotation for center card
                if (isCenter) {
                    rotationY = animatedRotationY
                }
                // Entrance animation
                translationY = slideOffset
                alpha = entranceAlpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    Log.d("DogBreedCard", "Card clicked for breed: ${breed.name}")
                    onClick()
                }
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White, // Changed to white background
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .background(
                            color = Color(0xFFA7A7A7),
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                ) {
                    if (breed.image?.url != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(breed.image.url)
                                .crossfade(true)
                                .build(),
                            contentDescription = breed.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback dog emoji
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🐕",
                                fontSize = if (isFocused) 48.sp else 36.sp
                            )
                        }
                    }
                }

                // Name Section - Only show breed name
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = breed.name,
                        style = TextStyle(
                            fontSize = if (isFocused) 16.sp else 14.sp,
                            fontWeight = FontWeight(600),
                            color = Color(0xFF2D3748), // Changed to dark text for white background
                            lineHeight = if (isFocused) 18.sp else 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// SKELETON LOADING ANIMATION
@Composable
fun SkeletonDogBreedCard(
    modifier: Modifier = Modifier,
    isFocused: Boolean = true
) {
    // Calculate dimensions based on focus state
    val cardWidth = if (isFocused) 165.dp else 130.dp
    val cardHeight = if (isFocused) 220.dp else 180.dp // Reduced height
    val imageHeight = if (isFocused) 165.dp else 130.dp

    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_transition")

    // Shimmer wave animation
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_wave"
    )

    // Pulse animation for skeleton elements
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Subtle scale animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = Color(0xFF4A90E2).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(2.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(15.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Image skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .background(
                            color = Color(0xFFE2E8F0).copy(alpha = pulseAlpha),
                            shape = RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .drawWithCache {
                            // Shimmer gradient effect for image
                            val shimmerColors = listOf(
                                Color.Transparent,
                                Color(0xFF4A90E2).copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.2f),
                                Color(0xFF4A90E2).copy(alpha = 0.1f),
                                Color.Transparent
                            )
                            val brush = Brush.linearGradient(
                                colors = shimmerColors,
                                start = Offset(shimmerTranslateAnim - 200f, 0f),
                                end = Offset(shimmerTranslateAnim + 200f, size.height)
                            )
                            onDrawBehind {
                                drawRoundRect(
                                    brush = brush,
                                    size = size,
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )
                            }
                        }
                )

                // Content skeleton - Only name skeleton
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Title skeleton - centered
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(if (isFocused) 18.sp.value.dp else 16.sp.value.dp),
                        alpha = pulseAlpha,
                        shimmerTranslateAnim = shimmerTranslateAnim
                    )
                }
            }
        }
    }
}

// Helper composable for skeleton elements
@Composable
private fun SkeletonBox(
    modifier: Modifier = Modifier,
    alpha: Float = 0.5f,
    shimmerTranslateAnim: Float = 0f,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFE2E8F0).copy(alpha = alpha),
                shape = shape
            )
            .drawWithCache {
                // Shimmer gradient effect
                val shimmerColors = listOf(
                    Color.Transparent,
                    Color(0xFF4A90E2).copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.2f),
                    Color(0xFF4A90E2).copy(alpha = 0.1f),
                    Color.Transparent
                )
                val brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(shimmerTranslateAnim - 200f, 0f),
                    end = Offset(shimmerTranslateAnim + 200f, size.height)
                )
                onDrawBehind {
                    drawRoundRect(
                        brush = brush,
                        size = size,
                        cornerRadius = CornerRadius(
                            when (shape) {
                                is RoundedCornerShape -> 4.dp.toPx()
                                else -> 0f
                            }
                        )
                    )
                }
            }
    )
}