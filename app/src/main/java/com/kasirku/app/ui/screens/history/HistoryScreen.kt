package com.kasirku.app.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.kasirku.app.data.model.Transaction
import com.kasirku.app.ui.components.CategoryChip
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: KasirViewModel) {
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val currentCashier by viewModel.currentCashier.collectAsState()
    val historyFilter by viewModel.historyFilter.collectAsState()
    val historySearchQuery by viewModel.historySearchQuery.collectAsState()
    val selectedTransaction by viewModel.selectedTransaction.collectAsState()
    val isAdmin = currentCashier?.isAdmin() == true

    val filters = listOf("Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini")

    // Transaction Detail Dialog
    selectedTransaction?.let { tx ->
        AlertDialog(
            onDismissRequest = { viewModel.selectTransaction(null) },
            title = { Text("Detail Transaksi", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tx.invoiceNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Kasir: ${tx.cashierName}", style = MaterialTheme.typography.labelMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    tx.items.split("§").forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal")
                        Text(KasirViewModel.formatRupiah(tx.subtotal))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pajak (${tx.taxPercent.toInt()}%)")
                        Text(KasirViewModel.formatRupiah(tx.taxAmount))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        Text(KasirViewModel.formatRupiah(tx.total), fontWeight = FontWeight.Bold, color = TealPrimary, style = MaterialTheme.typography.headlineSmall)
                    }

                    if (tx.totalCost > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Modal")
                            Text(KasirViewModel.formatRupiah(tx.totalCost), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Profit", fontWeight = FontWeight.Bold)
                            Text(
                                KasirViewModel.formatRupiah(tx.profit),
                                fontWeight = FontWeight.Bold,
                                color = if (tx.profit >= 0) SuccessGreen else DangerRed
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Metode")
                        Text(tx.paymentMethod)
                    }
                    if (tx.paymentMethod == "Tunai") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dibayar")
                            Text(KasirViewModel.formatRupiah(tx.amountPaid))
                        }
                        if (tx.change > 0) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Kembalian")
                                Text(KasirViewModel.formatRupiah(tx.change), color = SuccessGreen)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.selectTransaction(null) }) {
                    Text("Tutup")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAdmin) {
                IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
            Text("Riwayat Transaksi", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        // Filters
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                CategoryChip(
                    label = filter,
                    selected = filter == historyFilter,
                    onClick = { viewModel.setHistoryFilter(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search
        OutlinedTextField(
            value = historySearchQuery,
            onValueChange = { viewModel.setHistorySearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Cari nomor invoice...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (historySearchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Count
        Text(
            "${filteredTransactions.size} transaksi",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Transaction List
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTransactions) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectTransaction(tx) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(tx.invoiceNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            Text(
                                SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt)),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${tx.items.split("§").size} item • ${tx.cashierName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    KasirViewModel.formatRupiah(tx.total),
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary
                                )
                                if (tx.totalCost > 0) {
                                    Text(
                                        "Profit: ${KasirViewModel.formatRupiah(tx.profit)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (tx.profit >= 0) SuccessGreen else DangerRed
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(tx.paymentMethod, style = MaterialTheme.typography.labelMedium) }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Tidak ada transaksi", fontWeight = FontWeight.SemiBold)
                            Text(
                                if (historySearchQuery.isNotEmpty()) "Coba kata kunci lain"
                                else "Belum ada transaksi pada periode $historyFilter",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
