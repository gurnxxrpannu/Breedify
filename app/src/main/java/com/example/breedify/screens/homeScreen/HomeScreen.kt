package com.example.breedify.screens.homeScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.breedify.R
import com.example.breedify.data.api.Breed
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.navigation.BreedifyBottomNavigation
import kotlinx.coroutines.launch

// Color scheme for Breedify - Inspired by the uploaded design
object BreedifyColors {
    val Background = Color(0xFFD4E6D4) // Soft mint green background like the design
    val Primary = Color(0xFF4A90E2) // Soft blue for accents
    val Secondary = Color(0xFF5CB85C) // Green accent color from the design
    val CardBackground = Color(0xFFFFFFFF) // Pure white for cards
    val TextPrimary = Color(0xFF2D3748) // Dark gray for main text
    val TextSecondary = Color(0xFF6B7280) // Medium gray for secondary text
    val SearchBackground = Color(0xFFF8F9FA) // Very light background for search
    val ChatbotPrimary = Color(0xFF5CB85C) // Green for chatbot matching the theme
}

// Remove old DogBreed data class - now using Breed from API

@Composable
fun HomeScreen(
    onUploadPhoto: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    onBreedClick: (Breed) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var showChatbot by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var trendingBreeds by remember { mutableStateOf<List<Breed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load trending breeds on screen load
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            repository.getAllBreeds().fold(
                onSuccess = { breeds ->
                    // Take first 10 breeds as "trending"
                    trendingBreeds = breeds.take(10)
                },
                onFailure = { 
                    // Handle error - could show fallback data
                }
            )
            isLoading = false
        }
    }
    
    Scaffold(
        bottomBar = {
            BreedifyBottomNavigation(
                currentRoute = "home",
                onNavigate = onNavigate
            )
        },
        containerColor = BreedifyColors.Background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BreedifyColors.Background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(paddingValues)
            ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Header with title and subtitle inspired by the design
            HeaderSection()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search bar
            SearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Trending Breeds section
            TrendingBreedsSection(
                breeds = trendingBreeds,
                isLoading = isLoading,
                onBreedClick = onBreedClick
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Identify a Breed section
            IdentifyBreedSection(
                onUploadPhoto = onUploadPhoto,
                onOpenCamera = onOpenCamera
            )
            
                Spacer(modifier = Modifier.height(32.dp)) // Space for bottom navigation
            }
            
            // Floating chatbot button
            FloatingChatbotButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = { showChatbot = true }
            )
            
            // Chatbot dialog
            if (showChatbot) {
                ChatbotDialog(onDismiss = { showChatbot = false })
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
    ) {
        // Top row with app icon and profile (inspired by the design)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App grid icon (inspired by the design)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚏",
                    fontSize = 20.sp,
                    color = BreedifyColors.TextPrimary
                )
            }
            
            // Profile icon (inspired by the design)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        BreedifyColors.Secondary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main title and subtitle (inspired by the design)
        Text(
            text = "Breedify",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary,
        )
        
        Text(
            text = "Your favorite dog breeds",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = BreedifyColors.TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = {
            Text(
                text = "Search",
                color = BreedifyColors.TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = BreedifyColors.TextSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(25.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
private fun TrendingBreedsSection(
    breeds: List<Breed>,
    isLoading: Boolean,
    onBreedClick: (Breed) -> Unit
) {
    Column {
        Text(
            text = "Recommended",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            if (isLoading) {
                items(5) { // Show 5 skeleton cards while loading
                    com.example.breedify.components.SkeletonDogBreedCard(isFocused = true)
                }
            } else {
                items(breeds) { breed ->
                    com.example.breedify.components.DogBreedCard(
                        breed = breed,
                        onClick = { onBreedClick(breed) },
                        isFocused = true
                    )
                }
            }
        }
    }
}

@Composable
private fun BreedCard(
    breed: Breed,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BreedifyColors.CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder for breed image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BreedifyColors.SearchBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐕",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = breed.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = BreedifyColors.TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun IdentifyBreedSection(
    onUploadPhoto: () -> Unit,
    onOpenCamera: () -> Unit
) {
    Column {
        Text(
            text = "New Breeds",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Find your best friend and learn about them",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = BreedifyColors.TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Single card layout inspired by the design
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onUploadPhoto() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Identify Breed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BreedifyColors.TextPrimary
                    )
                    Text(
                        text = "Upload or take a photo",
                        fontSize = 14.sp,
                        color = BreedifyColors.TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Phone icon
                        Icon(
                            imageVector = Icons.Default.Search, // Using search as placeholder
                            contentDescription = "Phone",
                            tint = BreedifyColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        // Email icon  
                        Icon(
                            imageVector = Icons.Default.Search, // Using search as placeholder
                            contentDescription = "Email",
                            tint = BreedifyColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Dog illustration placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            BreedifyColors.Secondary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🐕",
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IdentifyButtonWithEmoji(
    text: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BreedifyColors.Primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FloatingChatbotButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(24.dp),
        containerColor = BreedifyColors.ChatbotPrimary,
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Text(
            text = "🐾",
            fontSize = 24.sp
        )
    }
}

@Composable
private fun ChatbotDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = BreedifyColors.CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PawPal Assistant 🐾",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BreedifyColors.TextPrimary
                    )
                    
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = BreedifyColors.Primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            BreedifyColors.SearchBackground,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hi! I'm PawPal 🐾\nAsk me anything about dog breeds!",
                        textAlign = TextAlign.Center,
                        color = BreedifyColors.TextSecondary,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Ask about dog breeds...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

// Sample data for trending breeds - now using Breed from API
private fun getSampleBreeds(): List<Breed> {
    return listOf(
        Breed(id = 1, name = "Golden Retriever"),
        Breed(id = 2, name = "Labrador"),
        Breed(id = 3, name = "German Shepherd"),
        Breed(id = 4, name = "Bulldog"),
        Breed(id = 5, name = "Poodle"),
        Breed(id = 6, name = "Beagle")
    )
}