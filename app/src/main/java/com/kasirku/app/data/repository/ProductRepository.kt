package com.kasirku.app.data.repository

import com.kasirku.app.data.local.ProductDao
import com.kasirku.app.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val dao: ProductDao) {
    fun getAllProducts(): Flow<List<Product>> = dao.getAllProducts()
    fun getAllProductsIncludingInactive(): Flow<List<Product>> = dao.getAllProductsIncludingInactive()
    fun getProductsByCategory(category: String): Flow<List<Product>> = dao.getProductsByCategory(category)
    fun searchProducts(query: String): Flow<List<Product>> = dao.searchProducts(query)
    fun getAllCategories(): Flow<List<String>> = dao.getAllCategories()
    fun getLowStockProducts(): Flow<List<Product>> = dao.getLowStockProducts()
    suspend fun getProductById(id: Long): Product? = dao.getProductById(id)
    fun getProductCount(): Flow<Int> = dao.getProductCount()
    fun getLowStockCount(): Flow<Int> = dao.getLowStockCount()
    fun getTotalStock(): Flow<Int?> = dao.getTotalStock()

    suspend fun insert(product: Product) = dao.insert(product)
    suspend fun insertAll(products: List<Product>) = dao.insertAll(products)
    suspend fun update(product: Product) = dao.update(product)
    suspend fun delete(id: Long) = dao.softDelete(id)
    suspend fun updateStock(id: Long, newStock: Int) = dao.updateStock(id, newStock)
    suspend fun renameCategory(oldCategory: String, newCategory: String) = dao.renameCategory(oldCategory, newCategory)
    suspend fun countActiveProductsInCategory(category: String): Int = dao.countActiveProductsInCategory(category)
}
