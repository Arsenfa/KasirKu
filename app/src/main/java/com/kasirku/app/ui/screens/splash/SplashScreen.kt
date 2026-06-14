package com.kasirku.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.ui.theme.PlusJakartaSans
import com.kasirku.app.ui.theme.TealDark
import com.kasirku.app.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Animation states
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val versionAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Entrance sequence
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(400))
        }
        delay(250)
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(350))
        }
        delay(150)
        launch {
            taglineAlpha.animateTo(1f, animationSpec = tween(350))
        }
        delay(200)
        launch {
            versionAlpha.animateTo(1f, animationSpec = tween(300))
        }

        // Timeout to next screen
        delay(1800)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(TealPrimary, TealDark),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo icon
            Box(
                modifier = Modifier
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "KasirKu",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = Color.White,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.alpha(titleAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Aplikasi Kasir Pintar",
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Version number at bottom
        Text(
            text = "v1.0.0",
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.45f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(versionAlpha.value)
        )
    }
}
