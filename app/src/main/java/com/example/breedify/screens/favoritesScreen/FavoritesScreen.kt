package com.example.breedify.screens.favoritesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import coil.request.ImageRequest
import com.example.breedify.data.api.Breed
import com.example.breedify.data.api.BreedImage
import com.example.breedify.data.api.Favourite
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.navigation.BreedifyBottomNavigation
import com.example.breedify.screens.homeScreen.BreedifyColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigate: (String) -> Unit,
    onBreedClick: (Breed) -> Unit,
    onChatbotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var favorites by remember { mutableStateOf<List<Favourite>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load favorites when screen is first displayed
    LaunchedEffect(Unit) {
        loadFavorites(repository) { result ->
            result.fold(
                onSuccess = { favs ->
                    favorites = favs
                    isLoading = false
                    errorMessage = null
                },
                onFailure = { error ->
                    errorMessage = error.message
                    isLoading = false
                }
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Favorites",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BreedifyColors.TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BreedifyColors.Background
                )
            )
        },
        bottomBar = {
            BreedifyBottomNavigation(
                currentRoute = "favorites",
                onNavigate = onNavigate,
                onChatbotClick = onChatbotClick
            )
        },
        containerColor = BreedifyColors.Background
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = BreedifyColors.Primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading your favorites...",
                                color = BreedifyColors.TextSecondary
                            )
                        }
                    }
                }
                
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "💔",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Oops! Something went wrong",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
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
                                onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        loadFavorites(repository) { result ->
                                            result.fold(
                                                onSuccess = { favs ->
                                                    favorites = favs
                                                    isLoading = false
                                                },
                                                onFailure = { error ->
                                                    errorMessage = error.message
                                                    isLoading = false
                                                }
                                            )
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BreedifyColors.Primary
                                )
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
                
                favorites.isEmpty() -> {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "💝",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No favorites yet!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BreedifyColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Start exploring dog breeds and tap the ❤️ icon to add them to your favorites.",
                                fontSize = 14.sp,
                                color = BreedifyColors.TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { onNavigate("home") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BreedifyColors.Primary
                                )
                            ) {
                                Text("Explore Breeds")
                            }
                        }
                    }
                }
                
                else -> {
                    // Display favorites grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favorites) { favorite ->
                            FavoriteBreedCard(
                                favorite = favorite,
                                onClick = {
                                    // Get breed details from image ID
                                    scope.launch {
                                        repository.getImageDetails(favorite.image_id).fold(
                                            onSuccess = { imageDetails ->
                                                if (imageDetails.breeds.isNotEmpty()) {
                                                    val breed = imageDetails.breeds.first().copy(
                                                        image = favorite.image
                                                    )
                                                    onBreedClick(breed)
                                                } else {
                                                    // Fallback if no breed info available
                                                    val breed = Breed(
                                                        id = 0,
                                                        name = "Unknown Breed",
                                                        image = favorite.image
                                                    )
                                                    onBreedClick(breed)
                                                }
                                            },
                                            onFailure = {
                                                // Fallback on error
                                                val breed = Breed(
                                                    id = 0,
                                                    name = "Unknown Breed",
                                                    image = favorite.image
                                                )
                                                onBreedClick(breed)
                                            }
                                        )
                                    }
                                },
                                onRemoveFromFavorites = {
                                    scope.launch {
                                        repository.removeFromFavourites(favorite.id).fold(
                                            onSuccess = {
                                                // Refresh favorites list
                                                loadFavorites(repository) { result ->
                                                    result.fold(
                                                        onSuccess = { favs -> favorites = favs },
                                                        onFailure = { /* Handle error silently */ }
                                                    )
                                                }
                                            },
                                            onFailure = { /* Handle error silently */ }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteBreedCard(
    favorite: Favourite,
    onClick: () -> Unit,
    onRemoveFromFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Dog Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(favorite.image.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Favorite dog",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Heart button (top right)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .clickable { onRemoveFromFavorites() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Gradient overlay at bottom for better text readability
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
            )
            
            // Image info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Favorite Dog",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Added to favorites",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private suspend fun loadFavorites(
    repository: DogRepository,
    onResult: (Result<List<Favourite>>) -> Unit
) {
    val result = repository.getFavourites()
    onResult(result)
}
