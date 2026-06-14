package com.kasirku.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.data.model.PromoCode
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun ManagePromosScreen(viewModel: KasirViewModel) {
    val promos by viewModel.allPromos.collectAsState()
    val showForm by viewModel.showPromoForm.collectAsState()
    val formCode by viewModel.promoFormCode.collectAsState()
    val formDiscPct by viewModel.promoFormDiscountPercent.collectAsState()
    val formDiscAmt by viewModel.promoFormDiscountAmount.collectAsState()
    val formLimit by viewModel.promoFormUsageLimit.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<PromoCode?>(null) }

    if (showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.closePromoForm() },
            title = { Text("Tambah Promo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = formCode, onValueChange = { viewModel.setPromoFormCode(it) }, label = { Text("Kode Promo") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = formDiscPct, onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setPromoFormDiscountPercent(it) }, label = { Text("Diskon (%)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = formDiscAmt, onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setPromoFormDiscountAmount(it) }, label = { Text("Atau Diskon (Rp)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = formLimit, onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setPromoFormUsageLimit(it) }, label = { Text("Batas Pakai (0=unlimited)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            },
            confirmButton = { Button(onClick = { viewModel.savePromo() }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), shape = RoundedCornerShape(12.dp)) { Text("Simpan") } },
            dismissButton = { TextButton(onClick = { viewModel.closePromoForm() }) { Text("Batal") } }
        )
    }

    showDeleteDialog?.let { promo ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Nonaktifkan Promo?") },
            text = { Text("Promo \"${promo.code}\" akan dinonaktifkan.") },
            confirmButton = { Button(onClick = { viewModel.deletePromo(promo.id); showDeleteDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed), shape = RoundedCornerShape(12.dp)) { Text("Nonaktifkan") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Batal") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("admin_hub") }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Kelola Promo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(promos) { promo ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(if (promo.isActive) TealPrimary.copy(0.1f) else DangerRed.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = if (promo.isActive) TealPrimary else DangerRed)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(promo.code, fontWeight = FontWeight.Bold)
                            Text(
                                if (promo.discountPercent > 0) "${promo.discountPercent.toInt()}%" else KasirViewModel.formatRupiah(promo.discountAmount),
                                style = MaterialTheme.typography.labelMedium, color = TealPrimary
                            )
                            Text(
                                if (promo.isActive) "Aktif • ${promo.usageCount}/${if (promo.usageLimit > 0) promo.usageLimit else "∞"}" else "Nonaktif",
                                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (promo.isActive) {
                            IconButton(onClick = { showDeleteDialog = promo }) { Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(20.dp)) }
                        }
                    }
                }
            }
            if (promos.isEmpty()) {
                item { Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp)); Text("Belum ada promo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }}
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(onClick = { viewModel.openAddPromoForm() }, containerColor = TealPrimary) { Icon(Icons.Default.Add, contentDescription = "Tambah Promo") }
        }
    }
}
