package com.kasirku.app.ui.screens.reports

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
import com.kasirku.app.ui.components.CategoryChip
import com.kasirku.app.ui.components.SalesChart
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(viewModel: KasirViewModel) {
    val reportPeriod by viewModel.reportPeriod.collectAsState()
    val sales by viewModel.reportSales.collectAsState()
    val txCount by viewModel.reportTransactionCount.collectAsState()
    val topProducts by viewModel.topProducts.collectAsState()
    val paymentBreakdown by viewModel.reportPaymentBreakdown.collectAsState()
    val transactions by viewModel.reportTransactions.collectAsState()
    val dailySalesData by viewModel.dailySalesData.collectAsState()
    val totalCost by viewModel.reportTotalCost.collectAsState()
    val profit by viewModel.reportProfit.collectAsState()
    val marginPercent by viewModel.reportMarginPercent.collectAsState()
    val isAdmin = viewModel.isAdmin

    LaunchedEffect(reportPeriod) {
        viewModel.setReportPeriod(reportPeriod)
    }

    val periods = listOf(
        "today" to "Hari Ini",
        "week" to "7 Hari",
        "month" to "Bulan Ini"
    )
    val periodLabel = periods.find { it.first == reportPeriod }?.second ?: "Hari Ini"

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isAdmin) {
                IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
            Text("Laporan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(periods) { (key, label) ->
                        CategoryChip(
                            label = label,
                            selected = key == reportPeriod,
                            onClick = { viewModel.setReportPeriod(key) }
                        )
                    }
                }
            }

            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Penjualan", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(KasirViewModel.formatRupiah(sales), style = MaterialTheme.typography.headlineSmall, color = TealPrimary, fontWeight = FontWeight.Bold)
                            Text("$txCount transaksi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Profit", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(KasirViewModel.formatRupiah(profit), style = MaterialTheme.typography.headlineSmall, color = if (profit >= 0) SuccessGreen else DangerRed, fontWeight = FontWeight.Bold)
                            Text("Margin ${String.format("%.1f", marginPercent)}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Sales Chart
            item {
                Text("Grafik Penjualan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    SalesChart(
                        data = dailySalesData,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Payment Method Breakdown
            if (paymentBreakdown.isNotEmpty()) {
                item {
                    Text("Metode Pembayaran", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val totalSalesAmt = paymentBreakdown.sumOf { it.total }
                            paymentBreakdown.forEach { method ->
                                val percentage = if (totalSalesAmt > 0) (method.total / totalSalesAmt * 100).toInt() else 0
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        when (method.paymentMethod) {
                                            "Tunai" -> Icons.Default.Money
                                            "QRIS" -> Icons.Default.QrCode2
                                            else -> Icons.Default.AccountBalance
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = TealPrimary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(method.paymentMethod, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text("${method.count} transaksi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(KasirViewModel.formatRupiah(method.total), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                        Text("$percentage%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (method != paymentBreakdown.last()) {
                                    HorizontalDivider(thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }

            // Top Products
            item {
                Text("Produk Terlaris", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (topProducts.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Belum ada data penjualan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    val maxQty = topProducts.maxOfOrNull { it.second } ?: 1
                    topProducts.forEachIndexed { index, (name, count) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", fontWeight = FontWeight.Bold, color = TealPrimary, modifier = Modifier.width(24.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { count.toFloat() / maxQty.toFloat() },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = TealPrimary,
                                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("${count}x", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Text("Transaksi Terakhir", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Belum ada transaksi", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            items(transactions.take(10)) { tx ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.invoiceNumber, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text(
                                "${SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt))} • ${tx.cashierName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(KasirViewModel.formatRupiah(tx.total), fontWeight = FontWeight.Bold, color = TealPrimary)
                            if (tx.totalCost > 0) {
                                Text(
                                    "Profit: ${KasirViewModel.formatRupiah(tx.profit)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (tx.profit >= 0) SuccessGreen else DangerRed
                                )
                            }
                            Text(tx.paymentMethod, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
