package com.kasirku.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun AdminHubScreen(viewModel: KasirViewModel) {
    val cashier by viewModel.currentCashier.collectAsState()
    val todaySales by viewModel.todaySales.collectAsState()
    val todayCount by viewModel.todayTransactionCount.collectAsState()
    val productCount by viewModel.productCount.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Panel Admin",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Halo, ${cashier?.name ?: "Admin"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sales Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Penjualan Hari Ini", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        KasirViewModel.formatRupiah(todaySales),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("$todayCount transaksi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Stock Alert Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (lowStockCount > 0) DangerRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Stok Menipis", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "$lowStockCount",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (lowStockCount > 0) DangerRed else SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text("$productCount produk aktif", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Low Stock Alert
        if (lowStockProducts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Peringatan Stok Menipis!",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    lowStockProducts.take(3).forEach { product ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(product.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "Sisa: ${product.stock}",
                                style = MaterialTheme.typography.labelMedium,
                                color = DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (lowStockProducts.size > 3) {
                        Text(
                            "+${lowStockProducts.size - 3} produk lainnya",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { viewModel.navigateTo("manage_products") },
                        colors = ButtonDefaults.textButtonColors(contentColor = DangerRed)
                    ) {
                        Text("Kelola Stok Sekarang", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Menu Grid
        Text("Menu Admin", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminMenuCard(
                icon = Icons.Default.Inventory2,
                title = "Kelola Produk",
                subtitle = "Tambah, edit, hapus produk & stok",
                color = TealPrimary,
                badgeCount = lowStockCount,
                onClick = { viewModel.navigateTo("manage_products") }
            )
            AdminMenuCard(
                icon = Icons.Default.People,
                title = "Kelola Kasir",
                subtitle = "Tambah, edit, hapus kasir & reset PIN",
                color = WarningAmber,
                onClick = { viewModel.navigateTo("manage_cashiers") }
            )
            AdminMenuCard(
                icon = Icons.Default.Analytics,
                title = "Laporan Penjualan",
                subtitle = "Lihat laporan harian, mingguan, bulanan",
                color = SuccessGreen,
                onClick = { viewModel.navigateTo("reports") }
            )
            AdminMenuCard(
                icon = Icons.Default.Store,
                title = "Pengaturan Toko",
                subtitle = "Nama toko, pajak, struk, dll",
                color = MaterialTheme.colorScheme.tertiary,
                onClick = { viewModel.navigateTo("store_settings") }
            )
            AdminMenuCard(
                icon = Icons.Default.LocalOffer,
                title = "Kelola Promo",
                subtitle = "Buat & kelola kode diskon",
                color = WarningAmber,
                onClick = { viewModel.navigateTo("manage_promos") }
            )
            AdminMenuCard(
                icon = Icons.Default.Schedule,
                title = "Manajemen Shift",
                subtitle = "Buka/tutup shift kasir",
                color = TealPrimary,
                onClick = { viewModel.navigateTo("shift") }
            )
            AdminMenuCard(
                icon = Icons.Default.Category,
                title = "Kelola Kategori",
                subtitle = "Tambah, ubah, hapus kategori produk",
                color = SuccessGreen,
                onClick = { viewModel.navigateTo("manage_categories") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Text("Aksi Cepat", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { viewModel.navigateTo("dashboard") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
            ) {
                Icon(Icons.Default.PointOfSale, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mode Kasir")
            }
            OutlinedButton(
                onClick = { viewModel.navigateTo("history") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Riwayat")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AdminMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (badgeCount > 0) {
                Badge(containerColor = DangerRed) {
                    Text("$badgeCount")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
