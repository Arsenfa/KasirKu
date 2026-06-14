package com.kasirku.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasirku.app.data.model.Cashier
import com.kasirku.app.ui.theme.*
import com.kasirku.app.viewmodel.KasirViewModel

@Composable
fun ManageCashiersScreen(viewModel: KasirViewModel) {
    val users by viewModel.allUsers.collectAsState()
    val showForm by viewModel.showCashierForm.collectAsState()
    val editingCashier by viewModel.editingCashier.collectAsState()

    val formName by viewModel.cashierFormName.collectAsState()
    val formPin by viewModel.cashierFormPin.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<Cashier?>(null) }
    var showResetPinDialog by remember { mutableStateOf<Cashier?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Cashier Form Dialog
    if (showForm) {
        AlertDialog(
            onDismissRequest = { viewModel.closeCashierForm() },
            title = {
                Text(
                    if (editingCashier != null) "Edit Kasir" else "Tambah Kasir",
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
                        onValueChange = { viewModel.setCashierFormName(it) },
                        label = { Text("Nama Kasir") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = formPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                viewModel.setCashierFormPin(it)
                            }
                        },
                        label = { Text("PIN (4 digit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (editingCashier != null) {
                        Text(
                            "Role: ${if (editingCashier?.isAdmin() == true) "Admin" else "Kasir"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveCashier() },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = formName.isNotEmpty() && formPin.length == 4
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeCashierForm() }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Confirmation
    showDeleteDialog?.let { cashier ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Kasir?") },
            text = { Text("Kasir \"${cashier.name}\" akan dinonaktifkan dan tidak bisa login lagi.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCashier(cashier.id) { success, message ->
                            snackbarMessage = message
                        }
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

    // Reset PIN Dialog
    showResetPinDialog?.let { cashier ->
        var newPin by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showResetPinDialog = null },
            title = { Text("Reset PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Reset PIN untuk ${cashier.name}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) newPin = it
                        },
                        label = { Text("PIN Baru (4 digit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPin.length == 4) {
                            viewModel.resetCashierPin(cashier.id, newPin)
                            showResetPinDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = newPin.length == 4
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPinDialog = null }) {
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
                "Kelola Kasir",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = TealPrimary.copy(alpha = 0.08f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${users.count { it.isAdmin() }}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber
                    )
                    Text("Admin", style = MaterialTheme.typography.labelMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${users.count { it.isCashier() }}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Text("Kasir", style = MaterialTheme.typography.labelMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${users.size}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cashier List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                CashierListItem(
                    cashier = user,
                    onEdit = { viewModel.openEditCashierForm(user) },
                    onDelete = { showDeleteDialog = user },
                    onResetPin = { showResetPinDialog = user }
                )
            }
        }

        // Snackbar message
        snackbarMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                snackbarMessage = null
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.contains("berhasil")) SuccessGreen.copy(alpha = 0.15f)
                    else DangerRed.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (message.contains("berhasil")) SuccessGreen else DangerRed
                )
            }
        }

        // FAB - Add Cashier
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.openAddCashierForm() },
                containerColor = TealPrimary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Kasir")
            }
        }
    }
}

@Composable
fun CashierListItem(
    cashier: Cashier,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onResetPin: () -> Unit
) {
    val isAdmin = cashier.isAdmin()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isAdmin) WarningAmber.copy(alpha = 0.15f)
                        else TealPrimary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isAdmin) WarningAmber else TealPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cashier.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (isAdmin) "Owner / Admin" else "Kasir",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isAdmin) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            if (!isAdmin) {
                IconButton(onClick = onResetPin) {
                    Icon(Icons.Default.Key, contentDescription = "Reset PIN", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TealPrimary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = DangerRed, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
