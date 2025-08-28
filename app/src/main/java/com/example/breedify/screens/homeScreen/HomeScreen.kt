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
import com.example.breedify.data.api.BreedFact
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
    onNavigate: (String) -> Unit = {},
    onChatbotClick: () -> Unit = {}
) {
    var showChatbot by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var trendingBreeds by remember { mutableStateOf<List<Breed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load favorite breeds on screen load
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            repository.getAllBreeds().fold(
                onSuccess = { breeds ->
                    // Filter for popular/favorite dog breeds
                    val favoriteBreedNames = listOf(
                        "Golden Retriever", "Labrador Retriever", "German Shepherd", 
                        "French Bulldog", "Bulldog", "Poodle", "Beagle", "Rottweiler",
                        "Yorkshire Terrier", "Dachshund", "Siberian Husky", "Boxer"
                    )
                    
                    val favoriteBreeds = breeds.filter { breed ->
                        favoriteBreedNames.any { favName ->
                            breed.name.contains(favName, ignoreCase = true)
                        }
                    }.take(10) // Limit to 10 breeds
                    
                    // If we don't find enough favorite breeds, supplement with first breeds
                    trendingBreeds = if (favoriteBreeds.size >= 8) {
                        favoriteBreeds
                    } else {
                        favoriteBreeds + breeds.take(10 - favoriteBreeds.size)
                    }
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
                onNavigate = onNavigate,
                onChatbotClick = onChatbotClick
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
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header with title and subtitle inspired by the design
            HeaderSection()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search bar
            SearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Favorite Breeds section
            FavoriteBreedsSection(
                breeds = trendingBreeds,
                isLoading = isLoading,
                onBreedClick = onBreedClick
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Dog Facts section
            DogFactsSection()
            
                Spacer(modifier = Modifier.height(32.dp)) // Space for bottom navigation
            }
            

            
            // Chatbot dialog
            if (showChatbot) {
                ChatbotDialog(onDismiss = { showChatbot = false })
            }
            
            // Image source selection dialog
            if (showImageSourceDialog) {
                ImageSourceDialog(
                    onDismiss = { showImageSourceDialog = false },
                    onCameraSelected = {
                        showImageSourceDialog = false
                        onOpenCamera()
                    },
                    onGallerySelected = {
                        showImageSourceDialog = false
                        onUploadPhoto()
                    }
                )
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
                    )
                    .clickable { /* Handle profile click */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Main title and subtitle (inspired by the design)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Breedify",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = BreedifyColors.TextPrimary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🐾",
                fontSize = 36.sp
            )
        }
        
        Text(
            text = "Snap. Detect. Connect — Know your dog in a click",
            fontSize = 14.sp,
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
private fun FavoriteBreedsSection(
    breeds: List<Breed>,
    isLoading: Boolean,
    onBreedClick: (Breed) -> Unit
) {
    Column {
        Text(
            text = "Today's Most Searched Dog Breeds",
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
private fun DogFactsSection() {
    var currentFact by remember { mutableStateOf<BreedFact?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load initial fact
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            repository.getRandomFacts(1).fold(
                onSuccess = { facts ->
                    if (facts.isNotEmpty()) {
                        currentFact = facts.first()
                    }
                },
                onFailure = { 
                    // Handle error - could show fallback fact
                    currentFact = BreedFact(0, "Dogs have been human companions for over 15,000 years!")
                }
            )
            isLoading = false
        }
    }
    
    fun refreshFact() {
        if (isRefreshing) return
        isRefreshing = true
        scope.launch {
            try {
                repository.getRandomFacts(1).fold(
                    onSuccess = { facts ->
                        if (facts.isNotEmpty()) {
                            currentFact = facts.first()
                        } else {
                            // Fallback to a random hardcoded fact if API returns empty
                            val fallbackFacts = listOf(
                                "Dogs have been human companions for over 15,000 years!",
                                "A dog's sense of smell is 10,000 to 100,000 times stronger than humans!",
                                "Dogs can learn over 150 words and can count up to four or five!",
                                "The average dog can run about 19 miles per hour at full speed!",
                                "Dogs have three eyelids - an upper lid, lower lid, and a third lid for protection!"
                            )
                            currentFact = BreedFact(0, fallbackFacts.random())
                        }
                    },
                    onFailure = { 
                        // Use fallback facts on API failure
                        val fallbackFacts = listOf(
                            "Dogs have been human companions for over 15,000 years!",
                            "A dog's sense of smell is 10,000 to 100,000 times stronger than humans!",
                            "Dogs can learn over 150 words and can count up to four or five!",
                            "The average dog can run about 19 miles per hour at full speed!",
                            "Dogs have three eyelids - an upper lid, lower lid, and a third lid for protection!"
                        )
                        currentFact = BreedFact(0, fallbackFacts.random())
                    }
                )
            } catch (e: Exception) {
                // Handle any unexpected errors
                currentFact = BreedFact(0, "Dogs are amazing creatures with incredible loyalty and intelligence!")
            } finally {
                isRefreshing = false
            }
        }
    }
    
    Column {
        Text(
            text = "Fun Dog Facts",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BreedifyColors.TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Discover amazing facts about our furry friends",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = BreedifyColors.TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Fact card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable { refreshFact() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = BreedifyColors.CardBackground
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fact icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            BreedifyColors.Primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💡",
                        fontSize = 32.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isLoading) {
                    // Loading state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                BreedifyColors.SearchBackground,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BreedifyColors.Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Fact content
                    Text(
                        text = currentFact?.fact ?: "Loading amazing dog facts...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = BreedifyColors.TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Refresh button
                Button(
                    onClick = { refreshFact() },
                    enabled = !isRefreshing,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BreedifyColors.Primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (isRefreshing) "Getting New Fact..." else "🔄 Get New Fact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Tap anywhere on the card or button to get a new fact!",
                    fontSize = 12.sp,
                    color = BreedifyColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
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

@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = BreedifyColors.CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Select Image Source",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BreedifyColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Camera option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { onCameraSelected() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BreedifyColors.Primary.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    BreedifyColors.Primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📷",
                                fontSize = 24.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Camera",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BreedifyColors.TextPrimary
                            )
                            Text(
                                text = "Take a new photo",
                                fontSize = 14.sp,
                                color = BreedifyColors.TextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Gallery option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { onGallerySelected() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BreedifyColors.Secondary.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    BreedifyColors.Secondary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📁",
                                fontSize = 24.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Gallery",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BreedifyColors.TextPrimary
                            )
                            Text(
                                text = "Choose from files",
                                fontSize = 14.sp,
                                color = BreedifyColors.TextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Cancel",
                        color = BreedifyColors.TextSecondary
                    )
                }
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