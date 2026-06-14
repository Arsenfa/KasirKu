package com.kasirku.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.data.model.Product
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.PriceDisplayStyle
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutOfStock = product.stock <= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isOutOfStock) Modifier else Modifier.clickable(onClick = onClick)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isOutOfStock) 0.dp else 2.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isOutOfStock) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else TealPrimary
                )

                // Out of stock overlay badge
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(DangerRed, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Habis",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = KasirViewModel.formatRupiah(product.price),
                    style = PriceDisplayStyle.copy(fontSize = MaterialTheme.typography.labelLarge.fontSize),
                    color = if (isOutOfStock) MaterialTheme.colorScheme.onSurfaceVariant else TealPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isOutOfStock) "Stok habis" else "Stok: ${product.stock}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOutOfStock) DangerRed
                          else if (product.isLowStock()) MaterialTheme.colorScheme.error
                          else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
