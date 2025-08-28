package com.example.breedify.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breedify.R
import kotlin.math.sqrt

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val emoji: String? = null,
    @DrawableRes val drawableRes: Int? = null
) {
    object Home : BottomNavItem("home", "Home", drawableRes = R.drawable.dog_home_icon)
    object Explore : BottomNavItem("explore", "Explore", emoji = "🦴")
    object Camera : BottomNavItem("camera", "Camera", drawableRes = R.drawable.god_camera_icon)
    object Profile : BottomNavItem("profile", "Profile", emoji = "🐾")
}

// Colors for the new bottom navigation design
object BottomNavColors {
    val Background = Color.White
    val Selected = Color(0xFFFF6B35) // Orange color from the design
    val Unselected = Color(0xFF9CA3AF) // Light gray
    val PawButton = Color(0xFFFF6B35) // Orange for the paw button
    val Shadow = Color.Black.copy(alpha = 0.1f)
}

// Custom shape for bottom navigation with proper semicircular cutout
class BottomNavCutoutShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val cutoutRadius = with(density) { 40.dp.toPx() }
            val cutoutCenter = width / 2f
            val cornerRadius = with(density) { 25.dp.toPx() }
            
            // Start from top-left with rounded corner
            moveTo(cornerRadius, 0f)
            
            // Top edge to cutout start
            lineTo(cutoutCenter - cutoutRadius, 0f)
            
            // Create semicircular cutout going downward into the bar
            // Left side of semicircle
            quadraticBezierTo(
                cutoutCenter - cutoutRadius, cutoutRadius * 0.6f,
                cutoutCenter - cutoutRadius * 0.7f, cutoutRadius * 0.8f
            )
            
            // Bottom of semicircle
            quadraticBezierTo(
                cutoutCenter, cutoutRadius,
                cutoutCenter + cutoutRadius * 0.7f, cutoutRadius * 0.8f
            )
            
            // Right side of semicircle
            quadraticBezierTo(
                cutoutCenter + cutoutRadius, cutoutRadius * 0.6f,
                cutoutCenter + cutoutRadius, 0f
            )
            
            // Continue top edge to top-right corner
            lineTo(width - cornerRadius, 0f)
            quadraticBezierTo(width, 0f, width, cornerRadius)
            
            // Right edge
            lineTo(width, height)
            
            // Bottom edge
            lineTo(0f, height)
            
            // Left edge
            lineTo(0f, cornerRadius)
            quadraticBezierTo(0f, 0f, cornerRadius, 0f)
            
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun BreedifyBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onChatbotClick: () -> Unit = {}
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Camera,
        BottomNavItem.Profile
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Main bottom navigation bar with cutout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .shadow(8.dp, BottomNavCutoutShape())
                .background(BottomNavColors.Background, BottomNavCutoutShape())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left items (Home, Explore)
                items.take(2).forEach { item ->
                    BottomNavItemView(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
                
                // Space for center paw button (cutout area)
                Spacer(modifier = Modifier.width(80.dp))
                
                // Right items (Camera, Profile)
                items.drop(2).forEach { item ->
                    BottomNavItemView(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
        
        // Center floating paw button (sits in the cutout)
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .shadow(8.dp, CircleShape)
                .background(BottomNavColors.PawButton, CircleShape)
                .clickable { onChatbotClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🐾",
                fontSize = 28.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        // Use drawable icon if available, otherwise use emoji
        if (item.drawableRes != null) {
            // Make camera icon bigger than other icons
            val iconSize = when (item.route) {
                "camera" -> if (isSelected) 45.dp else 28.dp // Bigger camera icon
                else -> if (isSelected) 28.dp else 24.dp // Normal size for other icons
            }
            
            Image(
                painter = painterResource(id = item.drawableRes),
                contentDescription = item.title,
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer {
                        // Add slight scaling effect for selected state
                        scaleX = if (isSelected) 1.1f else 1.0f
                        scaleY = if (isSelected) 1.1f else 1.0f
                        alpha = if (isSelected) 1.0f else 0.7f
                    }
                // No colorFilter - let the icons show in their original colors
            )
        } else if (item.emoji != null) {
            Text(
                text = item.emoji,
                fontSize = if (isSelected) 26.sp else 22.sp,
                color = if (isSelected) BottomNavColors.Selected else BottomNavColors.Unselected
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) BottomNavColors.Selected else BottomNavColors.Unselected
        )
    }
}