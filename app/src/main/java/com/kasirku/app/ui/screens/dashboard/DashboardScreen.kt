package com.kasirku.app.ui.screens.dashboard

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasirku.app.ui.components.CartBar
import com.kasirku.app.ui.components.CategoryChip
import com.kasirku.app.ui.components.ProductCard
import com.kasirku.app.ui.theme.DangerRed
import com.kasirku.app.ui.theme.PlusJakartaSans
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.KasirViewModel

/** Avatar bg color from name, same logic as LoginScreen. */
private fun avatarBgColor(name: String): Color {
    val hue = ((name.hashCode() and 0x7FFFFFFF) % 360).toFloat()
    return Color.hsl(hue, 0.55f, 0.50f)
}

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

    val context = LocalContext.current

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* permission result - proceed regardless */ }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        viewModel.checkAndNotify(context)
    }

    val allCategories = listOf("Semua") + categories
    val isAdmin = currentCashier?.isAdmin() == true
    var showBarcodeInput by remember { mutableStateOf(false) }
    var barcodeInput by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Cart bottom sheet ──
    if (showCart && cartItems.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowCart(false) },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Handle + header
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Keranjang",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = { viewModel.clearCart() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Kosongkan",
                            tint = DangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cart items
                cartItems.forEach { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        avatarBgColor(item.product.name).copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    item.product.name.take(1).uppercase(),
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = avatarBgColor(item.product.name)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.product.name,
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    KasirViewModel.formatRupiah(item.product.price),
                                    fontFamily = PlusJakartaSans,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Quantity controls — clean pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text(
                                        "−",
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    "${item.quantity}",
                                    fontFamily = PlusJakartaSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.widthIn(min = 24.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text(
                                        "+",
                                        fontFamily = PlusJakartaSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TealPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                KasirViewModel.formatRupiah(item.totalPrice),
                                fontFamily = PlusJakartaSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TealPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TealPrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total (${cartCount} item)",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        KasirViewModel.formatRupiah(cartTotal),
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout button
                Button(
                    onClick = {
                        viewModel.setShowCart(false)
                        onNavigateToPayment()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        "Lanjut ke Pembayaran",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: branding + cashier name
            Column {
                Text(
                    text = "KasirKu",
                    fontFamily = PlusJakartaSans,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = TealPrimary,
                    letterSpacing = (-0.3).sp
                )
                currentCashier?.let { cashier ->
                    Text(
                        text = "Halo, ${cashier.name}",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right: action icons + avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isAdmin) {
                    IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                        Icon(
                            Icons.Outlined.ManageAccounts,
                            contentDescription = "Admin Hub",
                            tint = WarningAmber
                        )
                    }
                }
                IconButton(onClick = { showBarcodeInput = !showBarcodeInput }) {
                    Icon(
                        Icons.Outlined.QrCodeScanner,
                        contentDescription = "Scan Barcode",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { viewModel.navigateTo("settings") }) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Cashier avatar
                currentCashier?.let { cashier ->
                    val bgColor = if (cashier.isAdmin()) WarningAmber else avatarBgColor(cashier.name)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cashier.name.take(1).uppercase(),
                            fontFamily = PlusJakartaSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // ── Barcode input ──
        AnimatedVisibility(
            visible = showBarcodeInput,
            enter = fadeIn(tween(200)) + slideInVertically { -it },
            exit = fadeOut(tween(150)) + slideOutVertically { -it }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = { barcodeInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Scan / ketik barcode...",
                            fontFamily = PlusJakartaSans,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    textStyle = LocalTextStyle.current.copy(fontFamily = PlusJakartaSans, fontSize = 14.sp)
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
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Cari",
                        fontFamily = PlusJakartaSans,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── Search bar ──
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text(
                    "Cari produk...",
                    fontFamily = PlusJakartaSans,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            textStyle = LocalTextStyle.current.copy(fontFamily = PlusJakartaSans, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Category chips ──
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

        // ── Product grid ──
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { viewModel.addToCart(product) }
                )
            }
        }

        // ── Cart bar ──
        CartBar(
            itemCount = cartCount,
            totalPrice = cartTotal,
            onClick = { viewModel.toggleCart() }
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}
