package com.kasirku.app.ui.screens.admin

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
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ShiftScreen(viewModel: KasirViewModel) {
    val activeShift by viewModel.activeShift.collectAsState()
    val allShifts by viewModel.allShifts.collectAsState()
    val openingBalance by viewModel.openingBalance.collectAsState()
    val todaySales by viewModel.todaySales.collectAsState()
    val todayCount by viewModel.todayTransactionCount.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("admin_hub") }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            Text("Manajemen Shift", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Active Shift Status
            item {
                if (activeShift != null) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.1f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(Modifier.width(8.dp))
                                Text("Shift Aktif", fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Kasir: ${activeShift?.cashierName}", style = MaterialTheme.typography.bodyMedium)
                            Text("Saldo Awal: ${KasirViewModel.formatRupiah(activeShift?.openingBalance ?: 0.0)}", style = MaterialTheme.typography.bodyMedium)
                            Text("Mulai: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(activeShift?.startTime ?: 0))}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column { Text("Penjualan Shift", style = MaterialTheme.typography.labelMedium); Text(KasirViewModel.formatRupiah(todaySales), fontWeight = FontWeight.Bold, color = TealPrimary) }
                                Column(horizontalAlignment = Alignment.End) { Text("Transaksi", style = MaterialTheme.typography.labelMedium); Text("$todayCount", fontWeight = FontWeight.Bold) }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.closeShift() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = DangerRed), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Tutup Shift", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(0.1f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PauseCircle, contentDescription = null, tint = WarningAmber)
                                Spacer(Modifier.width(8.dp))
                                Text("Tidak Ada Shift Aktif", fontWeight = FontWeight.Bold, color = WarningAmber)
                            }
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = openingBalance, onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setOpeningBalance(it) }, label = { Text("Saldo Awal (Rp)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.openShift() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen), shape = RoundedCornerShape(12.dp)) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Buka Shift", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Shift History
            item { Text("Riwayat Shift", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TealPrimary) }

            items(allShifts.filter { !it.isOpen }.take(10)) { shift ->
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${shift.cashierName} • ${shift.transactionCount} trx", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            Text(KasirViewModel.formatRupiah(shift.totalSales), fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(shift.startTime)), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Saldo: ${KasirViewModel.formatRupiah(shift.closingBalance)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
