package com.example.breedify.screens.welcomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.breedify.R

// Colors matching the exact design
object WelcomeScreenColors {
    val BackgroundGreen = Color(0xFFD2E6DB) // Canvas hex color #d2e6db
    val CardBackground = Color(0xFFE8F0EA) // Very light green for card
    val ButtonBackground = Color(0xFFFFFFFF) // White button
    val ButtonText = Color(0xFF4A9B5E) // Green text on button
    val TitleText = Color(0xFF2D2D2D) // Dark gray for title
    val SubtitleText = Color(0xFF666666) // Medium gray for subtitle
}

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WelcomeScreenColors.BackgroundGreen)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Top section with app name and tagline
            WelcomeTextSection()

            // Middle section with dog illustration (no background card)
            DogIllustrationCard()

            // Bottom section with button
            GetStartedButton(onClick = onGetStarted)
        }
    }
}

@Composable
private fun WelcomeTextSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Breedify",
            fontSize = 60.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WelcomeScreenColors.TitleText,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Snap. Detect. Connect \n Know your dog in a click",
            fontSize = 16.sp,
            color = WelcomeScreenColors.SubtitleText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun DogIllustrationCard() {
    // Remove the background card to eliminate white borders
    Image(
        painter = painterResource(id = R.drawable.welcomescreen_dogsticker),
        contentDescription = "Dog illustration",
        modifier = Modifier.size(280.dp)
    )
}



@Composable
private fun GetStartedButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = WelcomeScreenColors.ButtonBackground
        ),
        shape = RoundedCornerShape(25.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp
        )
    ) {
        Text(
            text = "Let's Start",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = WelcomeScreenColors.ButtonText
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}