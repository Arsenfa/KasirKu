package com.kasirku.app.ui.screens.receipt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.content.Intent
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptScreen(viewModel: KasirViewModel) {
    val transaction by viewModel.lastTransaction.collectAsState()
    val config = viewModel.storeConfig
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Success Icon
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SuccessGreen,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Pembayaran Berhasil!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Receipt Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(config.storeName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text(config.storeAddress, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(config.storePhone, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

                transaction?.let { tx ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tx.invoiceNumber, style = MaterialTheme.typography.labelMedium)
                        Text(
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt)),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Kasir: ${tx.cashierName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    tx.items.split("|").forEach { item ->
                        Text(item, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal")
                        Text(KasirViewModel.formatRupiah(tx.subtotal))
                    }
                    if (tx.discountAmount > 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon${if (tx.promoCode.isNotEmpty()) " (${tx.promoCode})" else ""}", color = DangerRed)
                            Text("- ${KasirViewModel.formatRupiah(tx.discountAmount)}", color = DangerRed)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pajak (${tx.taxPercent.toInt()}%)")
                        Text(KasirViewModel.formatRupiah(tx.taxAmount))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", fontWeight = FontWeight.Bold)
                        Text(KasirViewModel.formatRupiah(tx.total), fontWeight = FontWeight.Bold, color = TealPrimary)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tx.paymentMethod)
                        Text(KasirViewModel.formatRupiah(tx.amountPaid))
                    }
                    if (tx.change > 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kembalian")
                            Text(KasirViewModel.formatRupiah(tx.change), color = SuccessGreen)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp)
                Text(config.receiptFooter, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        OutlinedButton(
            onClick = {
                transaction?.let { tx ->
                    val receiptText = buildString {
                        appendLine("🧾 ${config.storeName}")
                        appendLine(config.storeAddress)
                        appendLine(config.storePhone)
                        appendLine("─".repeat(30))
                        appendLine("${tx.invoiceNumber}")
                        appendLine(SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt)))
                        appendLine("Kasir: ${tx.cashierName}")
                        appendLine("─".repeat(30))
                        tx.items.split("|").forEach { appendLine(it) }
                        appendLine("─".repeat(30))
                        appendLine("Subtotal: ${KasirViewModel.formatRupiah(tx.subtotal)}")
                        appendLine("Pajak (${tx.taxPercent.toInt()}%): ${KasirViewModel.formatRupiah(tx.taxAmount)}")
                        appendLine("TOTAL: ${KasirViewModel.formatRupiah(tx.total)}")
                        appendLine("─".repeat(30))
                        appendLine("${tx.paymentMethod}: ${KasirViewModel.formatRupiah(tx.amountPaid)}")
                        if (tx.change > 0) appendLine("Kembalian: ${KasirViewModel.formatRupiah(tx.change)}")
                        appendLine("─".repeat(30))
                        appendLine(config.receiptFooter)
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, receiptText)
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // WhatsApp not installed, use generic share
                        val genericIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, receiptText)
                        }
                        context.startActivity(Intent.createChooser(genericIntent, "Share struk"))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share via WA")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                transaction?.let { tx ->
                    val printers = BluetoothPrinter.getPairedPrinters(context)
                    if (printers.isEmpty()) {
                        // No paired printers, try to share as text instead
                        val receiptText = buildString {
                            appendLine("${config.storeName}")
                            appendLine(config.storeAddress)
                            appendLine("─".repeat(30))
                            appendLine(tx.invoiceNumber)
                            appendLine("Kasir: ${tx.cashierName}")
                            tx.items.split("|").forEach { appendLine(it) }
                            appendLine("─".repeat(30))
                            appendLine("TOTAL: ${KasirViewModel.formatRupiah(tx.total)}")
                            appendLine(config.receiptFooter)
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, receiptText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Print/Share"))
                    } else {
                        val printer = printers.first()
                        BluetoothPrinter.printReceipt(
                            context = context,
                            device = printer,
                            storeName = config.storeName,
                            storeAddress = config.storeAddress,
                            storePhone = config.storePhone,
                            invoice = tx.invoiceNumber,
                            date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(tx.createdAt)),
                            cashier = tx.cashierName,
                            items = tx.items.split("|"),
                            subtotal = KasirViewModel.formatRupiah(tx.subtotal),
                            discount = if (tx.discountAmount > 0) KasirViewModel.formatRupiah(tx.discountAmount) else "",
                            tax = KasirViewModel.formatRupiah(tx.taxAmount),
                            total = KasirViewModel.formatRupiah(tx.total),
                            paymentMethod = tx.paymentMethod,
                            amountPaid = KasirViewModel.formatRupiah(tx.amountPaid),
                            change = if (tx.change > 0) KasirViewModel.formatRupiah(tx.change) else "",
                            footer = config.receiptFooter
                        ) { success, message ->
                            // Result handled via callback
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
        ) {
            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Print Bluetooth")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (viewModel.isAdmin) {
                    viewModel.navigateTo("admin_hub")
                } else {
                    viewModel.navigateTo("dashboard")
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Transaksi Baru", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
