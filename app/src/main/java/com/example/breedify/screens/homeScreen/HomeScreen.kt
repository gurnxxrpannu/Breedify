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

// Color scheme for Breedify
object BreedifyColors {
    val Background = Color(0xFFD2E6DB) // Same as welcome screen background
    val Primary = Color(0xFF4A90E2) // Soft blue
    val Secondary = Color(0xFFFFB347) // Light orange accent
    val CardBackground = Color(0xFFFFFFFF) // Pure white for cards
    val TextPrimary = Color(0xFF2D3748) // Dark gray for main text
    val TextSecondary = Color(0xFF718096) // Medium gray for secondary text
    val SearchBackground = Color(0xFFF7FAFC) // Light gray for search bar
    val ChatbotPrimary = Color(0xFF48BB78) // Green for chatbot
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
            Spacer(modifier = Modifier.height(100.dp))
            
            // Header with title and paw icon
            HeaderSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
    ) {
        Text(
            text = "Breedify",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "🐾",
            fontSize = 32.sp
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
                text = "Enter breed name...",
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
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BreedifyColors.SearchBackground,
            unfocusedContainerColor = BreedifyColors.SearchBackground,
            focusedBorderColor = BreedifyColors.Primary,
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
            text = "Trending Breeds",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
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
            text = "Identify a Breed",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = BreedifyColors.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IdentifyButtonWithEmoji(
                text = "Upload a Photo",
                emoji = "🖼️",
                onClick = onUploadPhoto,
                modifier = Modifier.weight(1f)
            )
            
            IdentifyButtonWithEmoji(
                text = "Open Camera",
                emoji = "📷",
                onClick = onOpenCamera,
                modifier = Modifier.weight(1f)
            )
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