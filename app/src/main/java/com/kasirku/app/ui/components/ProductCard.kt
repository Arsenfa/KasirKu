package com.kasirku.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.data.model.Product
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.PlusJakartaSans
import com.kasirku.app.ui.theme.TealDark
import com.kasirku.app.ui.theme.TealLight
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.KasirViewModel

/** Generate a consistent hue-based gradient for a product category. */
private fun categoryGradient(category: String): List<Color> {
    val hue = ((category.hashCode() and 0x7FFFFFFF) % 360).toFloat()
    return listOf(
        Color.hsl(hue, 0.50f, 0.72f, 0.30f),
        Color.hsl(hue, 0.60f, 0.55f, 0.18f)
    )
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutOfStock = product.stock <= 0
    val isLowStock = !isOutOfStock && product.isLowStock()
    val interactionSource = remember { MutableInteractionSource() }

    val gradient = remember(product.category) { categoryGradient(product.category) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isOutOfStock) Modifier
                else Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = TealPrimary),
                    onClick = onClick
                )
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = if (isOutOfStock) 0.dp else 2.dp,
        tonalElevation = 0.dp
    ) {
        Column {
            // ── Image / Avatar area ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        if (isOutOfStock)
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surfaceContainer
                                )
                            )
                        else
                            Brush.linearGradient(
                                colors = gradient
                            )
                    )
            ) {
                // Product initials — centered
                Text(
                    text = product.name.take(2).uppercase(),
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = if (isOutOfStock)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
                    else
                        TealPrimary.copy(alpha = 0.75f),
                    modifier = Modifier.align(Alignment.Center)
                )

                // Out-of-stock badge
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DangerRed)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Habis",
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                // Add button — only when in stock
                if (!isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(TealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Tambah",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // ── Info area ──
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 10.dp)
            ) {
                Text(
                    text = product.name,
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = KasirViewModel.formatRupiah(product.price),
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isOutOfStock)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else TealPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Stock indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isOutOfStock -> DangerRed
                                    isLowStock -> WarningAmber
                                    else -> Color(0xFF22C55E)
                                }
                            )
                    )
                    Text(
                        text = when {
                            isOutOfStock -> "Stok habis"
                            isLowStock -> "Stok: ${product.stock}"
                            else -> "Stok: ${product.stock}"
                        },
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = when {
                            isOutOfStock -> DangerRed
                            isLowStock -> WarningAmber
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
