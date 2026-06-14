package com.kasirku.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kasirku.app.data.model.Product
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductsScreen(viewModel: KasirViewModel) {
    val products by viewModel.allProductsIncludingInactive.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val showForm by viewModel.showProductForm.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()

    val formName by viewModel.productFormName.collectAsState()
    val formPrice by viewModel.productFormPrice.collectAsState()
    val formCostPrice by viewModel.productFormCostPrice.collectAsState()
    val formCategory by viewModel.productFormCategory.collectAsState()
    val formStock by viewModel.productFormStock.collectAsState()
    val formDescription by viewModel.productFormDescription.collectAsState()
    val formBarcode by viewModel.productFormBarcode.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = products.filter {
        searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
    }

    // Product Form Dialog
    if (showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.closeProductForm() },
            title = {
                Text(
                    if (editingProduct != null) "Edit Produk" else "Tambah Produk",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = formName,
                        onValueChange = { viewModel.setProductFormName(it) },
                        label = { Text("Nama Produk") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formPrice,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setProductFormPrice(it) },
                            label = { Text("Harga Jual") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            prefix = { Text("Rp") }
                        )
                        OutlinedTextField(
                            value = formCostPrice,
                            onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setProductFormCostPrice(it) },
                            label = { Text("Harga Modal") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            prefix = { Text("Rp") }
                        )
                    }
                    OutlinedTextField(
                        value = formCategory,
                        onValueChange = { viewModel.setProductFormCategory(it) },
                        label = { Text("Kategori") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = formStock,
                        onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.setProductFormStock(it) },
                        label = { Text("Stok") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = formDescription,
                        onValueChange = { viewModel.setProductFormDescription(it) },
                        label = { Text("Deskripsi (opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = formBarcode,
                        onValueChange = { viewModel.setProductFormBarcode(it) },
                        label = { Text("Barcode (opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveProduct() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeProductForm() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Produk?") },
            text = { Text("Produk \"${product.name}\" akan dihapus dari daftar. Data transaksi tidak terpengaruh.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("admin_hub") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Kelola Produk",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Cari produk...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Product List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProducts) { product ->
                ProductListItem(
                    product = product,
                    onEdit = { viewModel.openEditProductForm(product) },
                    onDelete = { showDeleteDialog = product },
                    onStockUpdate = { newStock -> viewModel.updateProductStock(product.id, newStock) }
                )
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada produk", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // FAB - Add Product
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.openAddProductForm() },
                containerColor = TealPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStockUpdate: (Int) -> Unit
) {
    var showStockInput by remember { mutableStateOf(false) }
    var stockInput by remember { mutableStateOf(product.stock.toString()) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isActive) MaterialTheme.colorScheme.surfaceContainerLowest
            else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(TealPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        product.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!product.isActive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Nonaktif",
                                style = MaterialTheme.typography.labelMedium,
                                color = DangerRed
                            )
                        }
                    }
                    Text(
                        "${product.category} • ${KasirViewModel.formatRupiah(product.price)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stock indicator
                Column(horizontalAlignment = Alignment.End) {
                    if (product.isLowStock() && product.isActive) {
                        Text(
                            "Stok: ${product.stock}",
                            style = MaterialTheme.typography.labelMedium,
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "Stok: ${product.stock}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = {
                        showStockInput = !showStockInput
                        stockInput = product.stock.toString()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stok", style = MaterialTheme.typography.labelMedium)
                }
                if (product.isActive) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hapus", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Stock update inline
            if (showStockInput) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = stockInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) stockInput = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Jumlah stok baru") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            stockInput.toIntOrNull()?.let { onStockUpdate(it) }
                            showStockInput = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}
