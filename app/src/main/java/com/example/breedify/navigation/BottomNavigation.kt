package com.example.breedify.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val emoji: String
) {
    object Home : BottomNavItem("home", "Home", "🏠")
    object Explore : BottomNavItem("explore", "Explore", "🔍")
    object Camera : BottomNavItem("camera", "Camera", "📷")
    object Profile : BottomNavItem("profile", "Profile", "👤")
}

@Composable
fun BreedifyBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Camera,
        BottomNavItem.Profile
    )
    
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF4A90E2)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Text(
                        text = item.emoji,
                        fontSize = 24.sp
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4A90E2),
                    selectedTextColor = Color(0xFF4A90E2),
                    unselectedIconColor = Color(0xFF718096),
                    unselectedTextColor = Color(0xFF718096),
                    indicatorColor = Color(0xFFE3F2FD)
                )
            )
        }
    }
}