package com.kasirku.app.ui.screens.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun StoreSettingsScreen(viewModel: KasirViewModel) {
    val storeName by viewModel.storeName.collectAsState()
    val storeAddress by viewModel.storeAddress.collectAsState()
    val storePhone by viewModel.storePhone.collectAsState()
    val storeTaxPercent by viewModel.storeTaxPercent.collectAsState()
    val storeReceiptFooter by viewModel.storeReceiptFooter.collectAsState()
    val productCount by viewModel.productCount.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()

    var showSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Pengaturan Toko",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Store Info Section
        Text("Info Toko", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { viewModel.setStoreName(it) },
                    label = { Text("Nama Toko") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) }
                )
                OutlinedTextField(
                    value = storeAddress,
                    onValueChange = { viewModel.setStoreAddress(it) },
                    label = { Text("Alamat") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    maxLines = 2
                )
                OutlinedTextField(
                    value = storePhone,
                    onValueChange = { viewModel.setStorePhone(it) },
                    label = { Text("Telepon") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tax & Receipt Section
        Text("Pajak & Struk", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = storeTaxPercent,
                    onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setStoreTaxPercent(it) },
                    label = { Text("Pajak (%)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Percent, contentDescription = null) },
                    suffix = { Text("%") }
                )
                OutlinedTextField(
                    value = storeReceiptFooter,
                    onValueChange = { viewModel.setStoreReceiptFooter(it) },
                    label = { Text("Footer Struk") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Store Stats
        Text("Statistik Toko", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Produk Aktif", style = MaterialTheme.typography.bodyMedium)
                    Text("$productCount", fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Stok Menipis", style = MaterialTheme.typography.bodyMedium)
                    Text("$lowStockCount", fontWeight = FontWeight.Bold, color = if (lowStockCount > 0) MaterialTheme.colorScheme.error else SuccessGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.saveStoreConfig()
                showSaved = true
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Pengaturan", fontWeight = FontWeight.Bold)
        }

        if (showSaved) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pengaturan berhasil disimpan!", color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
