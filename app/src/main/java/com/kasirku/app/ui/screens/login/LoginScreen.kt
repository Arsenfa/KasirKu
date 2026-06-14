package com.kasirku.app.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.data.model.Cashier
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealDark
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun LoginScreen(viewModel: KasirViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var selectedUser by remember { mutableStateOf<Cashier?>(null) }
    var pin by remember { mutableStateOf("") }
    var isLoggingIn by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    // Reset PIN when user selection changes
    LaunchedEffect(selectedUser) {
        pin = ""
    }

    // Auto-login when PIN reaches 4 digits
    LaunchedEffect(pin) {
        if (pin.length == 4 && selectedUser != null && !isLoggingIn) {
            isLoggingIn = true
            viewModel.login(selectedUser!!, pin)
            // Reset after a short delay so user can retry
            kotlinx.coroutines.delay(500)
            isLoggingIn = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // ===== HEADER / BRANDING =====
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KasirKu",
                style = MaterialTheme.typography.headlineLarge,
                color = TealPrimary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sistem Kasir Pintar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ===== USER SELECTION =====
            Text(
                text = "Pilih Akun",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User Cards — using regular Column+Row (NOT LazyVerticalGrid)
            // This fixes the rendering issue inside verticalScroll
            if (users.isEmpty()) {
                // Loading state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = TealPrimary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Memuat data...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                // Build user cards in 2-column rows
                val rows = users.chunked(2)
                rows.forEach { rowUsers ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowUsers.forEach { user ->
                            UserCard(
                                user = user,
                                isSelected = selectedUser?.id == user.id,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedUser = user
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill empty space if odd number of users
                        if (rowUsers.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== SELECTED USER INDICATOR =====
            AnimatedVisibility(
                visible = selectedUser != null,
                enter = fadeIn() + scaleIn(initialScale = 0.9f)
            ) {
                selectedUser?.let { user ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (user.isAdmin()) WarningAmber.copy(alpha = 0.2f)
                                        else TealPrimary.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (user.isAdmin()) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (user.isAdmin()) WarningAmber else TealPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    user.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (user.isAdmin()) "Owner / Admin" else "Kasir",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== PIN INPUT =====
            Text(
                text = "Masukkan PIN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PIN Dots — bigger, more visible
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    val isFilled = index < pin.length
                    val scale by animateFloatAsState(
                        targetValue = if (isFilled) 1.2f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isFilled) TealPrimary else MaterialTheme.colorScheme.outlineVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .then(
                                if (isFilled) Modifier.background(dotColor)
                                else Modifier.border(2.5.dp, dotColor, CircleShape)
                            )
                    )
                }
            }

            // Error Message
            AnimatedVisibility(
                visible = loginError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(loginError ?: "", color = DangerRed, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== NUMERIC KEYPAD =====
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "del")
            )

            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .then(
                                    if (key.isNotEmpty()) {
                                        Modifier
                                            .shadow(
                                                elevation = if (key == "del") 0.dp else 2.dp,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                when (key) {
                                                    "del" -> { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                                                    else -> { if (pin.length < 4) pin += key }
                                                }
                                            }
                                            .background(
                                                if (key == "del") MaterialTheme.colorScheme.surfaceContainerLow
                                                else MaterialTheme.colorScheme.surfaceContainerLowest
                                            )
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "del") {
                                Icon(
                                    Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else if (key.isNotEmpty()) {
                                Text(
                                    text = key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== LOGIN BUTTON =====
            Button(
                onClick = {
                    selectedUser?.let { user ->
                        viewModel.login(user, pin)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedUser != null && pin.length == 4,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 1.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (selectedUser != null && pin.length == 4) {
                    Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = when {
                        selectedUser == null -> "Pilih akun terlebih dahulu"
                        pin.length < 4 -> "Masukkan PIN (${pin.length}/4)"
                        else -> "Masuk"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun UserCard(
    user: Cashier,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAdmin = user.isAdmin()
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) TealPrimary else MaterialTheme.colorScheme.outlineVariant
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TealPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceContainerLowest
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAdmin) WarningAmber.copy(alpha = 0.15f)
                        else TealPrimary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = if (isAdmin) WarningAmber else TealPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Role badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (isAdmin) WarningAmber.copy(alpha = 0.15f) else TealPrimary.copy(alpha = 0.1f),
            ) {
                Text(
                    text = if (isAdmin) "Admin" else "Kasir",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAdmin) WarningAmber else TealPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}
