package com.kasirku.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.data.model.Cashier
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun LoginScreen(viewModel: KasirViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    var selectedUser by remember { mutableStateOf<Cashier?>(null) }
    var pin by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    // Reset PIN when user selection changes
    LaunchedEffect(selectedUser) {
        pin = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "KasirKu",
            style = MaterialTheme.typography.headlineLarge,
            color = TealPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pilih Akun",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // User Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(((users.size + 1) / 2 * 120).dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                val isSelected = selectedUser?.id == user.id
                val isAdmin = user.isAdmin()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedUser = user }
                        .then(
                            if (isSelected) Modifier.border(2.dp, TealPrimary, RoundedCornerShape(12.dp))
                            else Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TealPrimary.copy(alpha = 0.08f)
                        else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isAdmin) WarningAmber.copy(alpha = 0.15f)
                                    else TealPrimary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = if (isAdmin) WarningAmber else TealPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isAdmin) "Owner / Admin" else "Kasir",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isAdmin) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PIN Display
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .then(
                            if (index < pin.length) Modifier.background(TealPrimary)
                            else Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                )
            }
        }

        // Error Message
        if (loginError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginError ?: "",
                color = DangerRed,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Numeric Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "del")
        )

        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .then(
                                if (key.isNotEmpty()) {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            when (key) {
                                                "del" -> { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
                                                else -> { if (pin.length < 4) pin += key }
                                            }
                                        }
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "del") {
                            Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface)
                        } else if (key.isNotEmpty()) {
                            Text(
                                text = key,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                selectedUser?.let { user ->
                    viewModel.login(user, pin)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = selectedUser != null && pin.length == 4,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Masuk", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
