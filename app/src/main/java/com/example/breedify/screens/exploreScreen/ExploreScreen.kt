package com.example.breedify.screens.exploreScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.breedify.data.api.Breed
import com.example.breedify.data.repository.DogRepository
import com.example.breedify.components.DogBreedCard
import com.example.breedify.components.SkeletonDogBreedCard
import com.example.breedify.navigation.BreedifyBottomNavigation
import kotlinx.coroutines.launch

// Same colors as HomeScreen
object ExploreColors {
    val Background = Color(0xFFD2E6DB)
    val Primary = Color(0xFF4A90E2)
    val CardBackground = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF2D3748)
    val TextSecondary = Color(0xFF718096)
    val SearchBackground = Color(0xFFF7FAFC)
}

@Composable
fun ExploreScreen(
    onNavigate: (String) -> Unit = {},
    onBreedClick: (Breed) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var breeds by remember { mutableStateOf<List<Breed>>(emptyList()) }
    var displayedBreeds by remember { mutableStateOf<List<Breed>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    val breedsPerPage = 12
    
    val repository = remember { DogRepository() }
    val scope = rememberCoroutineScope()
    
    // Load initial breeds
    LaunchedEffect(Unit) {
        isLoading = true
        scope.launch {
            repository.getAllBreeds().fold(
                onSuccess = { allBreeds ->
                    breeds = allBreeds
                    displayedBreeds = allBreeds.take(breedsPerPage)
                    currentPage = 1
                },
                onFailure = { 
                    // Handle error - could show a snackbar or error state
                }
            )
            isLoading = false
        }
    }
    
    // Search functionality
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            scope.launch {
                repository.searchBreeds(searchText).fold(
                    onSuccess = { searchResults ->
                        displayedBreeds = searchResults
                        currentPage = 1
                    },
                    onFailure = { 
                        // Handle search error
                    }
                )
            }
        } else {
            displayedBreeds = breeds.take(breedsPerPage)
            currentPage = 1
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ExploreColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Header
        ExploreHeader()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Search bar
        ExploreSearchBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ExploreColors.Primary)
            }
        } else {
            // Breeds grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp) // Space for bottom nav
            ) {
                if (isLoading) {
                    items(8) { // Show 8 skeleton cards while loading
                        SkeletonDogBreedCard(isFocused = true)
                    }
                } else {
                    items(displayedBreeds) { breed ->
                        DogBreedCard(
                            breed = breed,
                            onClick = { onBreedClick(breed) },
                            isFocused = true
                        )
                    }
                    
                    // Show More button at the end of the list (spans both columns)
                    if (searchText.isEmpty() && displayedBreeds.size < breeds.size) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ShowMoreButton(
                                    isLoading = isLoadingMore,
                                    onClick = {
                                        isLoadingMore = true
                                        scope.launch {
                                            // Simulate loading delay for better UX
                                            kotlinx.coroutines.delay(500)
                                            val nextPageBreeds = breeds.drop(currentPage * breedsPerPage).take(breedsPerPage)
                                            displayedBreeds = displayedBreeds + nextPageBreeds
                                            currentPage++
                                            isLoadingMore = false
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
        
        // Bottom Navigation
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BreedifyBottomNavigation(
                currentRoute = "explore",
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun ExploreHeader() {
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
            color = ExploreColors.TextPrimary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "🐾",
            fontSize = 32.sp
        )
    }
}

@Composable
private fun ExploreSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = {
            Text(
                text = "Search breeds...",
                color = ExploreColors.TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = ExploreColors.TextSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ExploreColors.SearchBackground,
            unfocusedContainerColor = ExploreColors.SearchBackground,
            focusedBorderColor = ExploreColors.Primary,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

// Removed BreedExploreCard - now using DogBreedCard component


@Composable
private fun ShowMoreButton(
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLoading) ExploreColors.Primary.copy(alpha = 0.7f) else ExploreColors.Primary
        ),
        shape = RoundedCornerShape(24.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Loading...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "Show More",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExploreScreenPreview() {
    MaterialTheme {
        ExploreScreen()
    }
}