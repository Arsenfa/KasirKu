package com.kasirku.app.ui.screens.payment

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun PaymentScreen(viewModel: KasirViewModel) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val tax by viewModel.cartTaxWithDiscount.collectAsState()
    val taxPct by viewModel.taxPercent.collectAsState()
    val discount by viewModel.cartDiscount.collectAsState()
    val total by viewModel.cartTotalWithDiscount.collectAsState()
    val selectedMethod by viewModel.selectedPaymentMethod.collectAsState()
    val amountPaid by viewModel.amountPaid.collectAsState()
    val change by viewModel.changeWithDiscount.collectAsState()
    val promoInput by viewModel.promoInput.collectAsState()
    val promoError by viewModel.promoError.collectAsState()
    val appliedPromo by viewModel.appliedPromo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.navigateTo("dashboard") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Pembayaran", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary Card
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ringkasan Pesanan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                cartItems.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.product.name} x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                        Text(KasirViewModel.formatRupiah(item.totalPrice), style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal"); Text(KasirViewModel.formatRupiah(subtotal)) }

                if (discount > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diskon (${appliedPromo?.code ?: ""})", color = DangerRed)
                        Text("- ${KasirViewModel.formatRupiah(discount)}", color = DangerRed)
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Pajak (${taxPct.toInt()}%)"); Text(KasirViewModel.formatRupiah(tax)) }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Text(KasirViewModel.formatRupiah(total), fontWeight = FontWeight.Bold, color = TealPrimary, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Promo Code Input
        Text("Kode Promo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (appliedPromo != null) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.1f))) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(appliedPromo!!.code, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text(
                            if (appliedPromo!!.discountPercent > 0) "Diskon ${appliedPromo!!.discountPercent.toInt()}%" else "Diskon ${KasirViewModel.formatRupiah(appliedPromo!!.discountAmount)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = { viewModel.removePromoCode() }) { Icon(Icons.Default.Close, contentDescription = null, tint = DangerRed, modifier = Modifier.size(18.dp)) }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = promoInput, onValueChange = { viewModel.setPromoInput(it) },
                    modifier = Modifier.weight(1f), placeholder = { Text("Masukkan kode") },
                    singleLine = true, shape = RoundedCornerShape(12.dp)
                )
                Button(onClick = { viewModel.applyPromoCode() }, colors = ButtonDefaults.buttonColors(containerColor = TealPrimary), shape = RoundedCornerShape(12.dp)) { Text("Pakai") }
            }
            if (promoError != null) {
                Spacer(Modifier.height(4.dp))
                Text(promoError!!, color = DangerRed, style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payment Methods
        Text("Metode Pembayaran", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Tunai" to Icons.Default.Money, "QRIS" to Icons.Default.QrCode2, "Transfer" to Icons.Default.AccountBalance).forEach { (method, icon) ->
                val isSelected = method == selectedMethod
                Card(
                    modifier = Modifier.weight(1f).clickable { viewModel.selectPaymentMethod(method) }
                        .then(if (isSelected) Modifier.border(2.dp, TealPrimary, RoundedCornerShape(12.dp)) else Modifier),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) TealPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(icon, contentDescription = null, tint = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(method, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (selectedMethod == "Tunai") {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Jumlah Uang", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(50000.0, 100000.0, 150000.0, 200000.0).forEach { amount ->
                    OutlinedButton(onClick = { viewModel.setAmountPaid(amount) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                        Text(KasirViewModel.formatRupiah(amount), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = if (amountPaid > 0) KasirViewModel.formatRupiah(amountPaid) else "",
                onValueChange = { val numeric = it.replace(Regex("[^0-9]"), ""); viewModel.setAmountPaid(numeric.toDoubleOrNull() ?: 0.0) },
                modifier = Modifier.fillMaxWidth(), label = { Text("Masukkan nominal") }, shape = RoundedCornerShape(12.dp)
            )
            if (amountPaid >= total) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kembalian", fontWeight = FontWeight.Bold)
                        Text(KasirViewModel.formatRupiah(change), fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.confirmPayment() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = selectedMethod != "Tunai" || amountPaid >= total,
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Konfirmasi Pembayaran", fontWeight = FontWeight.Bold) }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
