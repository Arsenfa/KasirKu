package com.kasirku.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.kasirku.app.ui.components.CartBar
import com.kasirku.app.ui.components.CategoryChip
import com.kasirku.app.ui.components.ProductCard
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: KasirViewModel,
    onNavigateToPayment: () -> Unit
) {
    val products by viewModel.filteredProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val showCart by viewModel.showCart.collectAsState()
    val currentCashier by viewModel.currentCashier.collectAsState()

    val allCategories = listOf("Semua") + categories
    val isAdmin = currentCashier?.isAdmin() == true
    var showBarcodeInput by remember { mutableStateOf(false) }
    var barcodeInput by remember { mutableStateOf("") }

    // Cart Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showCart && cartItems.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowCart(false) },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Keranjang", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { viewModel.clearCart() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Kosongkan", tint = DangerRed)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cart Items
                cartItems.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    KasirViewModel.formatRupiah(item.product.price),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Quantity controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    "${item.quantity}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                KasirViewModel.formatRupiah(item.totalPrice),
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total (${cartCount} item)", fontWeight = FontWeight.Bold)
                        Text(KasirViewModel.formatRupiah(cartTotal), fontWeight = FontWeight.Bold, color = TealPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout Button
                Button(
                    onClick = {
                        viewModel.setShowCart(false)
                        onNavigateToPayment()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Lanjut ke Pembayaran", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isAdmin) {
                    IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Admin")
                    }
                }
                Text(
                    text = "KasirKu",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TealPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                IconButton(onClick = { showBarcodeInput = !showBarcodeInput }) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode")
                }
                IconButton(onClick = { viewModel.navigateTo("settings") }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }

        // Barcode Input
        if (showBarcodeInput) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = { barcodeInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Scan/ketik barcode...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) }
                )
                Button(
                    onClick = {
                        if (barcodeInput.isNotEmpty()) {
                            viewModel.findByBarcode(barcodeInput)
                            barcodeInput = ""
                            showBarcodeInput = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cari") }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari produk...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCategories) { category ->
                CategoryChip(
                    label = category,
                    selected = category == selectedCategory,
                    onClick = { viewModel.selectCategory(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { viewModel.addToCart(product) }
                )
            }
        }

        // Cart Bar - now opens bottom sheet
        CartBar(
            itemCount = cartCount,
            totalPrice = cartTotal,
            onClick = { viewModel.toggleCart() }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
