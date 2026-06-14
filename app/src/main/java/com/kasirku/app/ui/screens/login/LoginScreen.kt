package com.kasirku.app.ui.screens.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.kasirku.app.ui.theme.PlusJakartaSans
import com.kasirku.app.ui.theme.TealDark
import com.kasirku.app.ui.theme.TealLight
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.KasirViewModel

// ============================================================
// Helpers
// ============================================================

/** Generate a stable, saturated background color from a name string. */
private fun avatarBgColor(name: String): Color {
    val hue = ((name.hashCode() and 0x7FFFFFFF) % 360).toFloat()
    return Color.hsl(hue, 0.55f, 0.50f)
}

private enum class LoginPhase { PICK_USER, ENTER_PIN }

// ============================================================
// Main screen
// ============================================================

@Composable
fun LoginScreen(viewModel: KasirViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var selectedUser by remember { mutableStateOf<Cashier?>(null) }
    var pin by remember { mutableStateOf("") }
    var phase by remember { mutableStateOf(LoginPhase.PICK_USER) }
    val haptic = LocalHapticFeedback.current

    // Reset PIN when going back to pick-user
    LaunchedEffect(phase) {
        if (phase == LoginPhase.PICK_USER) pin = ""
    }

    // Auto-login when PIN reaches 4 digits
    LaunchedEffect(pin) {
        if (pin.length == 4 && selectedUser != null) {
            viewModel.login(selectedUser!!, pin)
            kotlinx.coroutines.delay(400)
            if (viewModel.loginError.value != null) pin = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                if (targetState == LoginPhase.ENTER_PIN) {
                    (slideInHorizontally { it } + fadeIn(tween(280))) togetherWith
                        (slideOutHorizontally { -it } + fadeOut(tween(200)))
                } else {
                    (slideInHorizontally { -it } + fadeIn(tween(280))) togetherWith
                        (slideOutHorizontally { it } + fadeOut(tween(200)))
                }.using(SizeTransform(clip = false))
            },
            label = "LoginPhase"
        ) { currentPhase ->
            when (currentPhase) {
                LoginPhase.PICK_USER -> PickUserPhase(
                    users = users,
                    onUserSelected = { user ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedUser = user
                        phase = LoginPhase.ENTER_PIN
                    }
                )
                LoginPhase.ENTER_PIN -> selectedUser?.let { user ->
                    EnterPinPhase(
                        user = user,
                        pin = pin,
                        loginError = loginError,
                        onBack = { phase = LoginPhase.PICK_USER },
                        onKey = { key ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            when (key) {
                                "del" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                else -> if (pin.length < 4) pin += key
                            }
                        }
                    )
                }
            }
        }
    }
}

// ============================================================
// Phase 1 — Pick User
// ============================================================

@Composable
private fun PickUserPhase(
    users: List<Cashier>,
    onUserSelected: (Cashier) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // — Brand header —
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(TealPrimary, TealDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "KasirKu",
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = TealPrimary,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Pilih akun untuk masuk",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // — User grid —
        if (users.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = TealPrimary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Memuat akun...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(users) { user ->
                    UserPickCard(user = user, onClick = { onUserSelected(user) })
                }
            }
        }
    }
}

@Composable
private fun UserPickCard(
    user: Cashier,
    onClick: () -> Unit
) {
    val isAdmin = user.isAdmin()
    val interactionSource = remember { MutableInteractionSource() }
    val bgColor = if (isAdmin) WarningAmber else avatarBgColor(user.name)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = bgColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(2).uppercase(),
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.name,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Role badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(bgColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isAdmin) "Admin" else "Kasir",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = if (isAdmin) WarningAmber else bgColor,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

// ============================================================
// Phase 2 — Enter PIN
// ============================================================

@Composable
private fun EnterPinPhase(
    user: Cashier,
    pin: String,
    loginError: String?,
    onBack: () -> Unit,
    onKey: (String) -> Unit
) {
    val bgColor = if (user.isAdmin()) WarningAmber else avatarBgColor(user.name)
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // — Top bar —
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onBack()
            }) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Masuk sebagai",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // — User identity —
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(2).uppercase(),
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.name,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = if (user.isAdmin()) "Owner / Admin" else "Kasir",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(36.dp))

        // — PIN dots —
        PinDots(pin = pin, hasError = loginError != null)

        // — Error message —
        AnimatedVisibility(
            visible = loginError != null,
            enter = fadeIn() + slideInVertically { -8 },
            exit = fadeOut()
        ) {
            Text(
                text = loginError ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = DangerRed,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // — Keypad —
        NumericKeypad(onKey = onKey, modifier = Modifier.padding(horizontal = 40.dp))

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ============================================================
// PIN Dots
// ============================================================

@Composable
private fun PinDots(pin: String, hasError: Boolean) {
    val errorColor = DangerRed
    val filledColor = TealPrimary

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val isFilled = index < pin.length
            val scale by animateFloatAsState(
                targetValue = if (isFilled) 1.15f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "pinDotScale$index"
            )
            val dotColor by animateColorAsState(
                targetValue = when {
                    hasError -> errorColor
                    isFilled -> filledColor
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                label = "pinDotColor$index"
            )

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .then(
                        if (isFilled) Modifier.background(dotColor)
                        else Modifier.border(2.dp, dotColor, CircleShape)
                    )
            )
        }
    }
}

// ============================================================
// Numeric Keypad — flat, clean, professional
// ============================================================

@Composable
private fun NumericKeypad(
    onKey: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "del")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        onClick = { if (key.isNotEmpty()) onKey(key) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDel = key == "del"
    val isEmpty = key.isEmpty()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(60.dp)
            .then(
                if (!isEmpty) Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isDel)
                            MaterialTheme.colorScheme.surfaceContainerLow
                        else
                            MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(
                            bounded = true,
                            color = if (isDel) DangerRed else TealPrimary
                        ),
                        onClick = onClick
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    )
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isDel) {
            Icon(
                Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Hapus",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        } else if (!isEmpty) {
            Text(
                text = key,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
