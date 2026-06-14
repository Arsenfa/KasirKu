package com.kasirku.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.app.data.local.AppDatabase
import com.kasirku.app.data.local.NotificationHelper
import com.kasirku.app.data.local.PaymentMethodSummary
import com.kasirku.app.data.local.StoreConfig
import com.kasirku.app.data.model.*
import com.kasirku.app.data.model.PromoCode
import com.kasirku.app.data.model.Shift
import com.kasirku.app.data.repository.ProductRepository
import com.kasirku.app.data.repository.TransactionRepository
import com.kasirku.app.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class KasirViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val productRepo = ProductRepository(db.productDao())
    private val transactionRepo = TransactionRepository(db.transactionDao())
    private val userRepo = UserRepository(db.userDao())
    val storeConfig = StoreConfig(application)

    // ==================== NAVIGATION ====================
    private val _currentScreen = MutableStateFlow("splash")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // ==================== AUTH & USER ====================
    private val _currentCashier = MutableStateFlow<Cashier?>(null)
    val currentCashier: StateFlow<Cashier?> = _currentCashier.asStateFlow()

    val isAdmin: Boolean
        get() = _currentCashier.value?.isAdmin() == true

    val allUsers: StateFlow<List<Cashier>> = userRepo.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCount: StateFlow<Int> = userRepo.getUserCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(user: Cashier, pin: String) {
        if (pin == user.pin) {
            _currentCashier.value = user
            _loginError.value = null
            if (user.isAdmin()) {
                _currentScreen.value = "admin_hub"
            } else {
                _currentScreen.value = "dashboard"
            }
        } else {
            _loginError.value = "PIN salah! Coba lagi."
        }
    }

    fun logout() {
        _currentCashier.value = null
        clearCart()
        _currentScreen.value = "login"
    }

    // ==================== PRODUCTS ====================
    val allProducts: StateFlow<List<Product>> = productRepo.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProductsIncludingInactive: StateFlow<List<Product>> = productRepo.getAllProductsIncludingInactive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = productRepo.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts, _selectedCategory, _searchQuery
    ) { products, category, query ->
        products.filter { product ->
            val matchCategory = category == "Semua" || product.category == category
            val matchQuery = query.isEmpty() || product.name.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productCount: StateFlow<Int> = productRepo.getProductCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockProducts: StateFlow<List<Product>> = productRepo.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = productRepo.getLowStockCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // ---- Product CRUD (Admin) ----
    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    private val _showProductForm = MutableStateFlow(false)
    val showProductForm: StateFlow<Boolean> = _showProductForm.asStateFlow()

    // Product form fields
    private val _productFormName = MutableStateFlow("")
    val productFormName: StateFlow<String> = _productFormName.asStateFlow()
    private val _productFormPrice = MutableStateFlow("")
    val productFormPrice: StateFlow<String> = _productFormPrice.asStateFlow()
    private val _productFormCostPrice = MutableStateFlow("")
    val productFormCostPrice: StateFlow<String> = _productFormCostPrice.asStateFlow()
    private val _productFormCategory = MutableStateFlow("")
    val productFormCategory: StateFlow<String> = _productFormCategory.asStateFlow()
    private val _productFormStock = MutableStateFlow("")
    val productFormStock: StateFlow<String> = _productFormStock.asStateFlow()
    private val _productFormDescription = MutableStateFlow("")
    val productFormDescription: StateFlow<String> = _productFormDescription.asStateFlow()
    private val _productFormBarcode = MutableStateFlow("")
    val productFormBarcode: StateFlow<String> = _productFormBarcode.asStateFlow()

    fun openAddProductForm() {
        _editingProduct.value = null
        _productFormName.value = ""
        _productFormPrice.value = ""
        _productFormCostPrice.value = ""
        _productFormCategory.value = ""
        _productFormStock.value = ""
        _productFormDescription.value = ""
        _productFormBarcode.value = ""
        _showProductForm.value = true
    }

    fun openEditProductForm(product: Product) {
        _editingProduct.value = product
        _productFormName.value = product.name
        _productFormPrice.value = product.price.toLong().toString()
        _productFormCostPrice.value = product.costPrice.toLong().toString()
        _productFormCategory.value = product.category
        _productFormStock.value = product.stock.toString()
        _productFormDescription.value = product.description
        _productFormBarcode.value = product.barcode
        _showProductForm.value = true
    }

    fun closeProductForm() {
        _showProductForm.value = false
        _editingProduct.value = null
    }

    fun setProductFormName(value: String) { _productFormName.value = value }
    fun setProductFormPrice(value: String) { _productFormPrice.value = value }
    fun setProductFormCostPrice(value: String) { _productFormCostPrice.value = value }
    fun setProductFormCategory(value: String) { _productFormCategory.value = value }
    fun setProductFormStock(value: String) { _productFormStock.value = value }
    fun setProductFormDescription(value: String) { _productFormDescription.value = value }
    fun setProductFormBarcode(value: String) { _productFormBarcode.value = value }

    fun saveProduct() {
        val name = _productFormName.value.trim()
        val price = _productFormPrice.value.toDoubleOrNull() ?: 0.0
        val costPrice = _productFormCostPrice.value.toDoubleOrNull() ?: 0.0
        val category = _productFormCategory.value.trim()
        val stock = _productFormStock.value.toIntOrNull() ?: 0
        val description = _productFormDescription.value.trim()
        val barcode = _productFormBarcode.value.trim()

        if (name.isEmpty() || price <= 0 || category.isEmpty()) return

        viewModelScope.launch {
            val editing = _editingProduct.value
            if (editing != null) {
                // Update existing
                productRepo.update(
                    editing.copy(
                        name = name,
                        price = price,
                        costPrice = costPrice,
                        category = category,
                        stock = stock,
                        description = description,
                        barcode = barcode,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                // Insert new
                productRepo.insert(
                    Product(
                        name = name,
                        price = price,
                        costPrice = costPrice,
                        category = category,
                        stock = stock,
                        description = description,
                        barcode = barcode
                    )
                )
            }
            closeProductForm()
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            productRepo.delete(productId)
        }
    }

    fun updateProductStock(productId: Long, newStock: Int) {
        viewModelScope.launch {
            productRepo.updateStock(productId, newStock)
        }
    }

    // ==================== CART ====================
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartCount: StateFlow<Int> = _cartItems.map { it.sumOf { item -> item.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = _cartItems.map { it.sumOf { item -> item.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _taxPercent = MutableStateFlow(10.0)
    val taxPercent: StateFlow<Double> = _taxPercent.asStateFlow()

    val cartTax: StateFlow<Double> = combine(cartSubtotal, _taxPercent) { sub, tax ->
        sub * tax / 100
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, cartTax) { sub, tax ->
        sub + tax
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(product: Product) {
        // Block out-of-stock products
        if (product.stock <= 0) return

        val current = _cartItems.value.toMutableList()
        val existing = current.find { it.product.id == product.id }
        if (existing != null) {
            // Only increase if we haven't reached stock limit
            if (existing.quantity < product.stock) {
                existing.quantity++
            }
        } else {
            current.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(productId: Long, quantity: Int) {
        val current = _cartItems.value.toMutableList()
        if (quantity <= 0) {
            current.removeAll { it.product.id == productId }
        } else {
            val item = current.find { it.product.id == productId }
            // Enforce stock limit when increasing quantity
            if (item != null && quantity <= item.product.stock) {
                item.quantity = quantity
            }
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: Long) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _amountPaid.value = 0.0
    }

    // ==================== PAYMENT ====================
    private val _selectedPaymentMethod = MutableStateFlow("Tunai")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _amountPaid = MutableStateFlow(0.0)
    val amountPaid: StateFlow<Double> = _amountPaid.asStateFlow()

    val change: StateFlow<Double> = combine(amountPaid, cartTotal) { paid, total ->
        (paid - total).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun setAmountPaid(amount: Double) {
        _amountPaid.value = amount
    }

    fun confirmPayment() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault())
            val invoice = "INV-${dateFormat.format(Date(now))}"

            val totalCost = _cartItems.value.sumOf { it.product.costPrice * it.quantity }
            val discount = cartDiscount.value
            val promo = _appliedPromo.value

            val transaction = Transaction(
                invoiceNumber = invoice,
                items = _cartItems.value.joinToString("§") { "${it.product.name} x${it.quantity} ${it.product.price}" },
                subtotal = cartSubtotal.value,
                taxPercent = _taxPercent.value,
                taxAmount = cartTaxWithDiscount.value,
                total = cartTotalWithDiscount.value,
                totalCost = totalCost,
                discountAmount = discount,
                promoCode = promo?.code ?: "",
                paymentMethod = _selectedPaymentMethod.value,
                amountPaid = _amountPaid.value,
                change = changeWithDiscount.value,
                cashierName = _currentCashier.value?.name ?: "Unknown"
            )

            transactionRepo.insert(transaction)

            // Increment promo usage
            promo?.let { db.promoDao().incrementUsage(it.id) }

            // Reduce stock
            _cartItems.value.forEach { item ->
                val freshProduct = productRepo.getProductById(item.product.id)
                if (freshProduct != null) {
                    val newStock = (freshProduct.stock - item.quantity).coerceAtLeast(0)
                    productRepo.updateStock(item.product.id, newStock)
                }
            }

            _lastTransaction.value = transaction
            clearCart()
            removePromoCode()
            _currentScreen.value = "receipt"
        }
    }

    // ==================== RECEIPT ====================
    private val _lastTransaction = MutableStateFlow<Transaction?>(null)
    val lastTransaction: StateFlow<Transaction?> = _lastTransaction.asStateFlow()

    // ==================== TRANSACTIONS / HISTORY ====================
    val allTransactions: StateFlow<List<Transaction>> = transactionRepo.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==================== REPORTS ====================
    private val _reportPeriod = MutableStateFlow("today") // today, week, month, custom
    val reportPeriod: StateFlow<String> = _reportPeriod.asStateFlow()

    private val reportStartTime: Long
        get() {
            val cal = Calendar.getInstance()
            when (_reportPeriod.value) {
                "today" -> {
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
                "week" -> {
                    cal.add(Calendar.DAY_OF_MONTH, -7)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
                "month" -> {
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
            }
            return cal.timeInMillis
        }

    private val reportEndTime: Long
        get() = System.currentTimeMillis()

    val reportSales: StateFlow<Double> = _reportPeriod.flatMapLatest {
        transactionRepo.getTotalSalesSince(reportStartTime)
    }.map { it ?: 0.0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val reportTransactionCount: StateFlow<Int> = _reportPeriod.flatMapLatest {
        transactionRepo.getTransactionCountSince(reportStartTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val reportTransactions: StateFlow<List<Transaction>> = _reportPeriod.flatMapLatest {
        transactionRepo.getTransactionsSince(reportStartTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportPaymentBreakdown: StateFlow<List<PaymentMethodSummary>> = _reportPeriod.flatMapLatest {
        transactionRepo.getSalesByPaymentMethod(reportStartTime)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Top products from transactions
    private val _topProducts = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val topProducts: StateFlow<List<Pair<String, Int>>> = _topProducts.asStateFlow()

    fun setReportPeriod(period: String) {
        _reportPeriod.value = period
        calculateTopProducts()
        calculateDailySales()
    }

    private fun calculateTopProducts() {
        viewModelScope.launch {
            _reportPeriod.flatMapLatest {
                transactionRepo.getTransactionItemsSince(reportStartTime)
            }.take(1).collect { itemsList ->
                val productCounts = mutableMapOf<String, Int>()
                itemsList.forEach { items ->
                    items.split("§").forEach { itemStr ->
                        // Parse "Product x2 15000.0" format
                        val parts = itemStr.trim().split(" x")
                        if (parts.size >= 2) {
                            val name = parts[0].trim()
                            val qty = parts[1].split(" ")[0].toIntOrNull() ?: 1
                            productCounts[name] = (productCounts[name] ?: 0) + qty
                        }
                    }
                }
                _topProducts.value = productCounts.toList()
                    .sortedByDescending { it.second }
                    .take(5)
            }
        }
    }

    // Also keep the old today-specific flows for cashier dashboard
    private val todayMillis: Long
        get() {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

    val todaySales: StateFlow<Double> = transactionRepo.getTotalSalesSince(todayMillis)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTransactionCount: StateFlow<Int> = transactionRepo.getTransactionCountSince(todayMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ==================== CASHIER MANAGEMENT (Admin) ====================
    private val _editingCashier = MutableStateFlow<Cashier?>(null)
    val editingCashier: StateFlow<Cashier?> = _editingCashier.asStateFlow()

    private val _showCashierForm = MutableStateFlow(false)
    val showCashierForm: StateFlow<Boolean> = _showCashierForm.asStateFlow()

    private val _cashierFormName = MutableStateFlow("")
    val cashierFormName: StateFlow<String> = _cashierFormName.asStateFlow()
    private val _cashierFormPin = MutableStateFlow("")
    val cashierFormPin: StateFlow<String> = _cashierFormPin.asStateFlow()

    fun openAddCashierForm() {
        _editingCashier.value = null
        _cashierFormName.value = ""
        _cashierFormPin.value = ""
        _showCashierForm.value = true
    }

    fun openEditCashierForm(cashier: Cashier) {
        _editingCashier.value = cashier
        _cashierFormName.value = cashier.name
        _cashierFormPin.value = cashier.pin
        _showCashierForm.value = true
    }

    fun closeCashierForm() {
        _showCashierForm.value = false
        _editingCashier.value = null
    }

    fun setCashierFormName(value: String) { _cashierFormName.value = value }
    fun setCashierFormPin(value: String) { _cashierFormPin.value = value }

    fun saveCashier() {
        val name = _cashierFormName.value.trim()
        val pin = _cashierFormPin.value.trim()
        if (name.isEmpty() || pin.length != 4) return

        viewModelScope.launch {
            val editing = _editingCashier.value
            if (editing != null) {
                userRepo.update(editing.copy(name = name, pin = pin))
            } else {
                userRepo.insert(Cashier(name = name, pin = pin, role = Cashier.ROLE_CASHIER))
            }
            closeCashierForm()
        }
    }

    fun deleteCashier(cashierId: Long) {
        viewModelScope.launch {
            userRepo.delete(cashierId)
        }
    }

    fun resetCashierPin(cashierId: Long, newPin: String) {
        if (newPin.length != 4) return
        viewModelScope.launch {
            userRepo.resetPin(cashierId, newPin)
        }
    }

    // ==================== STORE SETTINGS (Admin) ====================
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()
    private val _storeAddress = MutableStateFlow("")
    val storeAddress: StateFlow<String> = _storeAddress.asStateFlow()
    private val _storePhone = MutableStateFlow("")
    val storePhone: StateFlow<String> = _storePhone.asStateFlow()
    private val _storeTaxPercent = MutableStateFlow("")
    val storeTaxPercent: StateFlow<String> = _storeTaxPercent.asStateFlow()
    private val _storeReceiptFooter = MutableStateFlow("")
    val storeReceiptFooter: StateFlow<String> = _storeReceiptFooter.asStateFlow()

    fun loadStoreConfig() {
        _storeName.value = storeConfig.storeName
        _storeAddress.value = storeConfig.storeAddress
        _storePhone.value = storeConfig.storePhone
        _storeTaxPercent.value = storeConfig.taxPercent.toLong().toString()
        _storeReceiptFooter.value = storeConfig.receiptFooter
        _taxPercent.value = storeConfig.taxPercent
    }

    fun setStoreName(value: String) { _storeName.value = value }
    fun setStoreAddress(value: String) { _storeAddress.value = value }
    fun setStorePhone(value: String) { _storePhone.value = value }
    fun setStoreTaxPercent(value: String) { _storeTaxPercent.value = value }
    fun setStoreReceiptFooter(value: String) { _storeReceiptFooter.value = value }

    fun saveStoreConfig() {
        storeConfig.storeName = _storeName.value.trim().ifEmpty { "Toko Sejahtera" }
        storeConfig.storeAddress = _storeAddress.value.trim()
        storeConfig.storePhone = _storePhone.value.trim()
        storeConfig.taxPercent = _storeTaxPercent.value.toDoubleOrNull() ?: 10.0
        storeConfig.receiptFooter = _storeReceiptFooter.value.trim().ifEmpty { "Terima kasih!" }
        _taxPercent.value = storeConfig.taxPercent
    }

    // ==================== PROFIT TRACKING ====================
    val reportTotalCost: StateFlow<Double> = _reportPeriod.flatMapLatest {
        transactionRepo.getTotalCostSince(reportStartTime)
    }.map { it ?: 0.0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val reportProfit: StateFlow<Double> = combine(reportSales, reportTotalCost) { sales, cost ->
        sales - cost
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val reportMarginPercent: StateFlow<Double> = combine(reportSales, reportProfit) { sales, profit ->
        if (sales > 0) (profit / sales) * 100 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ==================== DAILY SALES CHART DATA ====================
    private val _dailySalesData = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val dailySalesData: StateFlow<List<Pair<String, Double>>> = _dailySalesData.asStateFlow()

    private fun calculateDailySales() {
        viewModelScope.launch {
            _reportPeriod.flatMapLatest {
                transactionRepo.getTransactionsSince(reportStartTime)
            }.take(1).collect { transactions ->
                val cal = Calendar.getInstance()
                val dayFormat = SimpleDateFormat("dd/MM", Locale("id", "ID"))
                val dailyMap = linkedMapOf<String, Double>()

                transactions.forEach { tx ->
                    cal.timeInMillis = tx.createdAt
                    val dayKey = dayFormat.format(Date(tx.createdAt))
                    dailyMap[dayKey] = (dailyMap[dayKey] ?: 0.0) + tx.total
                }

                _dailySalesData.value = dailyMap.toList().takeLast(7)
            }
        }
    }

    // ==================== HISTORY FILTER & SEARCH ====================
    private val _historyFilter = MutableStateFlow("Hari Ini")
    val historyFilter: StateFlow<String> = _historyFilter.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val historyStartTime: Long
        get() {
            val cal = Calendar.getInstance()
            when (_historyFilter.value) {
                "Hari Ini" -> {
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
                "Kemarin" -> {
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
                "Minggu Ini" -> {
                    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
                "Bulan Ini" -> {
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                }
            }
            return cal.timeInMillis
        }

    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        _historyFilter.flatMapLatest {
            transactionRepo.getTransactionsSince(historyStartTime)
        },
        _historySearchQuery
    ) { transactions, query ->
        if (query.isEmpty()) transactions
        else transactions.filter { it.invoiceNumber.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setHistoryFilter(filter: String) {
        _historyFilter.value = filter
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    // ==================== CART VISIBILITY ====================
    private val _showCart = MutableStateFlow(false)
    val showCart: StateFlow<Boolean> = _showCart.asStateFlow()

    fun toggleCart() {
        _showCart.value = !_showCart.value
    }

    fun setShowCart(show: Boolean) {
        _showCart.value = show
    }

    // ==================== SELECTED TRANSACTION (for detail) ====================
    private val _selectedTransaction = MutableStateFlow<Transaction?>(null)
    val selectedTransaction: StateFlow<Transaction?> = _selectedTransaction.asStateFlow()

    fun selectTransaction(tx: Transaction?) {
        _selectedTransaction.value = tx
    }

    // ==================== SEED DATA ====================

    fun seedDefaultCashiers() {
        viewModelScope.launch {
            val count = userRepo.getUserCountSync()
            if (count == 0) {
                val defaults = listOf(
                    Cashier(name = "Admin", pin = "0000", role = Cashier.ROLE_ADMIN),
                    Cashier(name = "Andi", pin = "1234", role = Cashier.ROLE_CASHIER),
                    Cashier(name = "Sari", pin = "5678", role = Cashier.ROLE_CASHIER),
                    Cashier(name = "Budi", pin = "9012", role = Cashier.ROLE_CASHIER)
                )
                defaults.forEach { userRepo.insert(it) }
            }
        }
    }

    fun seedSampleProducts() {
        viewModelScope.launch {
            val count = productCount.value
            if (count == 0) {
                val products = listOf(
                    Product(name = "Nasi Goreng", price = 15000.0, category = "Makanan", stock = 50, costPrice = 8000.0),
                    Product(name = "Mie Ayam", price = 12000.0, category = "Makanan", stock = 30, costPrice = 6000.0),
                    Product(name = "Ayam Goreng", price = 20000.0, category = "Makanan", stock = 25, costPrice = 12000.0),
                    Product(name = "Sate Ayam", price = 18000.0, category = "Makanan", stock = 20, costPrice = 10000.0),
                    Product(name = "Roti Bakar", price = 8000.0, category = "Snack", stock = 40, costPrice = 3000.0),
                    Product(name = "Pisang Goreng", price = 5000.0, category = "Snack", stock = 50, costPrice = 2000.0),
                    Product(name = "Es Teh Manis", price = 5000.0, category = "Minuman", stock = 100, costPrice = 1500.0),
                    Product(name = "Jus Jeruk", price = 12000.0, category = "Minuman", stock = 30, costPrice = 5000.0),
                    Product(name = "Kopi Susu", price = 15000.0, category = "Minuman", stock = 40, costPrice = 7000.0),
                    Product(name = "Es Campur", price = 10000.0, category = "Dessert", stock = 25, costPrice = 4000.0),
                    Product(name = "Puding", price = 8000.0, category = "Dessert", stock = 20, costPrice = 3000.0),
                    Product(name = "Air Mineral", price = 3000.0, category = "Minuman", stock = 200, costPrice = 1500.0)
                )
                productRepo.insertAll(products)
            }
        }
    }

    // ==================== THEME MODE ====================
    private val _themeMode = MutableStateFlow("auto") // "light", "dark", "auto"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        storeConfig.themeMode = mode
    }

    // ==================== PROMO CODE ====================
    private val _appliedPromo = MutableStateFlow<PromoCode?>(null)
    val appliedPromo: StateFlow<PromoCode?> = _appliedPromo.asStateFlow()

    private val _promoError = MutableStateFlow<String?>(null)
    val promoError: StateFlow<String?> = _promoError.asStateFlow()

    private val _promoInput = MutableStateFlow("")
    val promoInput: StateFlow<String> = _promoInput.asStateFlow()

    val allPromos: StateFlow<List<PromoCode>> = db.promoDao().getAllPromos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setPromoInput(value: String) { _promoInput.value = value }

    fun applyPromoCode() {
        viewModelScope.launch {
            val code = _promoInput.value.trim().uppercase()
            if (code.isEmpty()) {
                _promoError.value = "Masukkan kode promo"
                return@launch
            }
            val promo = db.promoDao().findByCode(code)
            if (promo == null) {
                _promoError.value = "Kode promo tidak ditemukan"
                return@launch
            }
            if (!promo.isActive || promo.isExpired()) {
                _promoError.value = "Kode promo sudah tidak berlaku"
                return@launch
            }
            _appliedPromo.value = promo
            _promoError.value = null
        }
    }

    fun removePromoCode() {
        _appliedPromo.value = null
        _promoInput.value = ""
        _promoError.value = null
    }

    val cartDiscount: StateFlow<Double> = combine(cartSubtotal, _appliedPromo) { subtotal, promo ->
        if (promo == null) 0.0
        else if (promo.discountPercent > 0) subtotal * promo.discountPercent / 100
        else promo.discountAmount.coerceAtMost(subtotal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartAfterDiscount: StateFlow<Double> = combine(cartSubtotal, cartDiscount) { sub, disc ->
        (sub - disc).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTaxWithDiscount: StateFlow<Double> = combine(cartAfterDiscount, _taxPercent) { afterDisc, tax ->
        afterDisc * tax / 100
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalWithDiscount: StateFlow<Double> = combine(cartAfterDiscount, cartTaxWithDiscount) { afterDisc, tax ->
        afterDisc + tax
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val changeWithDiscount: StateFlow<Double> = combine(amountPaid, cartTotalWithDiscount) { paid, total ->
        (paid - total).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ---- Promo CRUD (Admin) ----
    private val _showPromoForm = MutableStateFlow(false)
    val showPromoForm: StateFlow<Boolean> = _showPromoForm.asStateFlow()
    private val _promoFormCode = MutableStateFlow("")
    val promoFormCode: StateFlow<String> = _promoFormCode.asStateFlow()
    private val _promoFormDiscountPercent = MutableStateFlow("")
    val promoFormDiscountPercent: StateFlow<String> = _promoFormDiscountPercent.asStateFlow()
    private val _promoFormDiscountAmount = MutableStateFlow("")
    val promoFormDiscountAmount: StateFlow<String> = _promoFormDiscountAmount.asStateFlow()
    private val _promoFormUsageLimit = MutableStateFlow("")
    val promoFormUsageLimit: StateFlow<String> = _promoFormUsageLimit.asStateFlow()
    private val _editingPromo = MutableStateFlow<PromoCode?>(null)

    fun openAddPromoForm() {
        _editingPromo.value = null
        _promoFormCode.value = ""
        _promoFormDiscountPercent.value = ""
        _promoFormDiscountAmount.value = ""
        _promoFormUsageLimit.value = ""
        _showPromoForm.value = true
    }

    fun openEditPromoForm(promo: PromoCode) {
        _editingPromo.value = promo
        _promoFormCode.value = promo.code
        _promoFormDiscountPercent.value = if (promo.discountPercent > 0) promo.discountPercent.toLong().toString() else ""
        _promoFormDiscountAmount.value = if (promo.discountAmount > 0) promo.discountAmount.toLong().toString() else ""
        _promoFormUsageLimit.value = if (promo.usageLimit > 0) promo.usageLimit.toString() else ""
        _showPromoForm.value = true
    }

    fun closePromoForm() { _showPromoForm.value = false; _editingPromo.value = null }
    fun setPromoFormCode(v: String) { _promoFormCode.value = v.uppercase() }
    fun setPromoFormDiscountPercent(v: String) { _promoFormDiscountPercent.value = v }
    fun setPromoFormDiscountAmount(v: String) { _promoFormDiscountAmount.value = v }
    fun setPromoFormUsageLimit(v: String) { _promoFormUsageLimit.value = v }

    fun savePromo() {
        val code = _promoFormCode.value.trim().uppercase()
        if (code.isEmpty()) return
        val discPct = _promoFormDiscountPercent.value.toDoubleOrNull() ?: 0.0
        val discAmt = _promoFormDiscountAmount.value.toDoubleOrNull() ?: 0.0
        val limit = _promoFormUsageLimit.value.toIntOrNull() ?: 0
        if (discPct <= 0 && discAmt <= 0) return

        viewModelScope.launch {
            val editing = _editingPromo.value
            if (editing != null) {
                db.promoDao().update(editing.copy(code = code, discountPercent = discPct, discountAmount = discAmt, usageLimit = limit))
            } else {
                db.promoDao().insert(PromoCode(code = code, discountPercent = discPct, discountAmount = discAmt, usageLimit = limit))
            }
            closePromoForm()
        }
    }

    fun deletePromo(id: Long) {
        viewModelScope.launch { db.promoDao().deactivate(id) }
    }

    // ==================== SHIFT MANAGEMENT ====================
    val activeShift: StateFlow<Shift?> = db.shiftDao().getActiveShift()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allShifts: StateFlow<List<Shift>> = db.shiftDao().getAllShifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _openingBalance = MutableStateFlow("")
    val openingBalance: StateFlow<String> = _openingBalance.asStateFlow()

    fun setOpeningBalance(value: String) { _openingBalance.value = value }

    fun openShift() {
        viewModelScope.launch {
            val balance = _openingBalance.value.toDoubleOrNull() ?: 0.0
            val shift = Shift(
                cashierName = _currentCashier.value?.name ?: "Unknown",
                openingBalance = balance
            )
            db.shiftDao().insert(shift)
            _openingBalance.value = ""
        }
    }

    fun closeShift() {
        viewModelScope.launch {
            val shift = db.shiftDao().getActiveShiftSync() ?: return@launch
            // Calculate shift totals
            val transactions = transactionRepo.getTransactionsSince(shift.startTime)
            var total = 0.0
            var count = 0
            transactions.take(1).collect { txList ->
                total = txList.sumOf { it.total }
                count = txList.size
            }
            val closingBalance = shift.openingBalance + total
            db.shiftDao().update(
                shift.copy(
                    isOpen = false,
                    closingBalance = closingBalance,
                    totalSales = total,
                    transactionCount = count,
                    endTime = System.currentTimeMillis()
                )
            )
        }
    }

    // ==================== BARCODE LOOKUP ====================
    fun findByBarcode(barcode: String) {
        viewModelScope.launch {
            val product = db.productDao().findByBarcode(barcode)
            if (product != null) {
                addToCart(product)
            }
        }
    }

    // ==================== EXPORT HELPERS ====================
    fun exportTransactionsCsv(): String {
        val transactions = allTransactions.value
        val sb = StringBuilder()
        sb.appendLine("Invoice,Tanggal,Kasir,Items,Subtotal,Pajak,Total,Diskon,Modal,Profit,Metode")
        transactions.forEach { tx ->
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID")).format(Date(tx.createdAt))
            sb.appendLine("\"${tx.invoiceNumber}\",\"$date\",\"${tx.cashierName}\",\"${tx.items}\",${tx.subtotal},${tx.taxAmount},${tx.total},${tx.discountAmount},${tx.totalCost},${tx.profit},\"${tx.paymentMethod}\"")
        }
        return sb.toString()
    }

    fun exportTransactionsJson(): String {
        val transactions = allTransactions.value
        val sb = StringBuilder()
        sb.appendLine("[")
        transactions.forEachIndexed { i, tx ->
            sb.appendLine("""{"invoice":"${tx.invoiceNumber}","date":${tx.createdAt},"cashier":"${tx.cashierName}","items":"${tx.items}","subtotal":${tx.subtotal},"tax":${tx.taxAmount},"total":${tx.total},"discount":${tx.discountAmount},"cost":${tx.totalCost},"profit":${tx.profit},"method":"${tx.paymentMethod}"}""")
            if (i < transactions.size - 1) sb.appendLine(",")
        }
        sb.appendLine("]")
        return sb.toString()
    }

    // Initialize
    init {
        loadStoreConfig()
        _themeMode.value = storeConfig.themeMode
        seedDefaultCashiers()
        calculateTopProducts()
        calculateDailySales()
        NotificationHelper.createNotificationChannel(getApplication())
    }

    // ==================== CATEGORY MANAGEMENT ====================
    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            productRepo.renameCategory(oldName, newName)
        }
    }

    fun checkAndDeleteCategory(category: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val count = productRepo.countActiveProductsInCategory(category)
            if (count > 0) {
                onResult(false, "Tidak bisa menghapus '$category': masih ada $count produk aktif")
            } else {
                // Remove from custom categories if present
                val customList = storeConfig.getCustomCategoriesList()
                if (category in customList) {
                    storeConfig.setCustomCategoriesList(customList - category)
                }
                onResult(true, "Kategori '$category' berhasil dihapus")
            }
        }
    }

    // ==================== NOTIFICATIONS ====================
    fun checkAndNotify(context: Context) {
        viewModelScope.launch {
            val lowStock = lowStockCount.value
            if (lowStock > 0) {
                NotificationHelper.showLowStockNotification(context, lowStock)
            }

            val shift = activeShift.value
            val cashier = _currentCashier.value
            if (shift == null && cashier != null && cashier.isCashier()) {
                NotificationHelper.showShiftReminderNotification(context)
            }
        }
    }

    companion object {
        fun formatRupiah(amount: Double): String {
            val formatter = NumberFormat.getIntegerInstance(Locale("id", "ID"))
            return "Rp ${formatter.format(amount.toLong())}"
        }
    }
}
