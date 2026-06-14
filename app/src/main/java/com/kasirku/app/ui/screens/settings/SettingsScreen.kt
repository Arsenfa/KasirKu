package com.kasirku.app.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun SettingsScreen(viewModel: KasirViewModel) {
    val cashier by viewModel.currentCashier.collectAsState()
    val productCount by viewModel.productCount.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Pengaturan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Account
        Text("Akun", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(cashier?.name ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(if (cashier?.isAdmin() == true) "Owner / Admin" else "Kasir", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Theme
        Text("Tampilan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tema Aplikasi", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("light" to "Light", "dark" to "Dark", "auto" to "Auto").forEach { (mode, label) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Store Info
        Text("Info Toko", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsRow("Nama Toko", viewModel.storeConfig.storeName)
                SettingsRow("Alamat", viewModel.storeConfig.storeAddress)
                SettingsRow("Telepon", viewModel.storeConfig.storePhone)
                SettingsRow("Pajak", "${viewModel.storeConfig.taxPercent.toInt()}%")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Products
        Text("Produk", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsRow("Total Produk Aktif", "$productCount produk")
                SettingsRow("Kategori", "${categories.size} kategori")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Export Data
        Text("Export Data", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        val csv = viewModel.exportTransactionsCsv()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_TEXT, csv)
                            putExtra(Intent.EXTRA_SUBJECT, "Export Transaksi KasirKu")
                        }
                        context.startActivity(Intent.createChooser(intent, "Export CSV"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Transaksi (CSV)")
                }
                OutlinedButton(
                    onClick = {
                        val json = viewModel.exportTransactionsJson()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_TEXT, json)
                            putExtra(Intent.EXTRA_SUBJECT, "Backup KasirKu")
                        }
                        context.startActivity(Intent.createChooser(intent, "Backup Data"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Backup Data (JSON)")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout
        OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar (${cashier?.name ?: ""})")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(4.dp))
}
