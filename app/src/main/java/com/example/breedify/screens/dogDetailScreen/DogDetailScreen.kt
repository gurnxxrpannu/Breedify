package com.example.breedify.screens.dogDetailScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
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
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.screens.homeScreen.BreedifyColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogDetailScreen(
    breed: Breed,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }
    var breedDetails by remember { mutableStateOf(breed) }
    var isLoading by remember { mutableStateOf(false) }
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load detailed breed information
    LaunchedEffect(breed.id) {
        isLoading = true
        scope.launch {
            repository.getBreedDetails(breed.id).fold(
                onSuccess = { details ->
                    // Preserve the original image if the detailed response doesn't have one
                    breedDetails = if (details.image == null && breed.image != null) {
                        details.copy(image = breed.image)
                    } else {
                        details
                    }
                },
                onFailure = { 
                    // Keep original breed data if detailed fetch fails
                }
            )
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BreedifyColors.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = BreedifyColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = BreedifyColors.Background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            // Hero Image Section
            HeroImageSection(
                breed = breedDetails,
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Content Section
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Breed Name and Location
                BreedHeaderSection(breed = breedDetails)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quick Stats
                QuickStatsSection(breed = breedDetails)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // About Section
                AboutSection(breed = breedDetails)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Characteristics Section
                CharacteristicsSection(breed = breedDetails)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HeroImageSection(
    breed: Breed,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(horizontal = 20.dp)
    ) {
        // Main Image Card
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5E6B8) // Warm beige like the design
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background decorative elements (inspired by the design)
                Canvas(modifier = Modifier.fillMaxSize()) { 
                    // Add some decorative circles like in the design
                    drawCircle(
                        color = Color(0xFFE6D49A).copy(alpha = 0.3f),
                        radius = 120.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            size.width * 0.7f,
                            size.height * 0.3f
                        )
                    )
                    drawCircle(
                        color = Color(0xFFD4C589).copy(alpha = 0.2f),
                        radius = 60.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            size.width * 0.2f,
                            size.height * 0.2f
                        )
                    )
                }
                
                // Dog Image
                if (breed.image?.url != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(breed.image.url)
                            .crossfade(true)
                            .build(),
                        contentDescription = breed.name,
                        modifier = Modifier
                            .size(280.dp)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback illustration
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐕",
                            fontSize = 120.sp
                        )
                    }
                }
            }
        }
        
        // Favorite Button (top right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-16).dp, y = 16.dp)
                .size(48.dp)
                .background(Color.White, CircleShape)
                .clickable { onFavoriteClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Red else BreedifyColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BreedHeaderSection(breed: Breed) {
    Column {
        // Breed Name
        Text(
            text = breed.name,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Origin with location icon
        if (breed.origin != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Origin",
                    tint = BreedifyColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Origin: ${breed.origin}",
                    fontSize = 14.sp,
                    color = BreedifyColors.TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Breed Group
        if (breed.breed_group != null) {
            Text(
                text = breed.breed_group,
                fontSize = 14.sp,
                color = BreedifyColors.Primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun QuickStatsSection(breed: Breed) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Weight
            if (breed.weight?.metric != null) {
                StatItem(
                    title = "Weight",
                    value = "${breed.weight.metric} kg",
                    icon = "⚖️"
                )
            }
            
            // Height
            if (breed.height?.metric != null) {
                StatItem(
                    title = "Height",
                    value = "${breed.height.metric} cm",
                    icon = "📏"
                )
            }
            
            // Life Span
            if (breed.life_span != null) {
                StatItem(
                    title = "Life Span",
                    value = breed.life_span,
                    icon = "🎂"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = BreedifyColors.TextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = BreedifyColors.TextPrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AboutSection(breed: Breed) {
    Column {
        Text(
            text = "About",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (breed.bred_for != null) {
                    Text(
                        text = "Originally bred for ${breed.bred_for.lowercase()}. ${breed.name}s are known for their unique characteristics and make wonderful companions.",
                        fontSize = 14.sp,
                        color = BreedifyColors.TextPrimary,
                        lineHeight = 20.sp
                    )
                } else {
                    Text(
                        text = "${breed.name}s are wonderful dogs with unique characteristics that make them great companions for the right family.",
                        fontSize = 14.sp,
                        color = BreedifyColors.TextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacteristicsSection(breed: Breed) {
    if (breed.temperament != null) {
        Column {
            Text(
                text = "Temperament",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BreedifyColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Split temperament into individual traits
            val traits = breed.temperament.split(", ")
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(((traits.size / 2 + traits.size % 2) * 48).dp)
            ) {
                items(traits.size) { index ->
                    TraitChip(trait = traits[index])
                }
            }
        }
    }
}

@Composable
private fun TraitChip(trait: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BreedifyColors.Secondary.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = trait.trim(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = BreedifyColors.Secondary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}