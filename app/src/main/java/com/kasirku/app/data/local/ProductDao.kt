package com.kasirku.app.data.local

import androidx.room.*
import com.kasirku.app.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY category, name")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY isActive DESC, category, name")
    fun getAllProductsIncludingInactive(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND category = :category ORDER BY name")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND name LIKE '%' || :query || '%' ORDER BY name")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND barcode = :barcode LIMIT 1")
    suspend fun findByBarcode(barcode: String): Product?

    @Query("SELECT DISTINCT category FROM products WHERE isActive = 1 ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM products WHERE isActive = 1 AND stock <= lowStockThreshold ORDER BY stock ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Query("UPDATE products SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE products SET stock = :newStock, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStock(id: Long, newStock: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    fun getProductCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1 AND stock <= lowStockThreshold")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT SUM(stock) FROM products WHERE isActive = 1")
    fun getTotalStock(): Flow<Int?>

    @Query("UPDATE products SET category = :newCategory, updatedAt = :updatedAt WHERE category = :oldCategory")
    suspend fun renameCategory(oldCategory: String, newCategory: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1 AND category = :category")
    suspend fun countActiveProductsInCategory(category: String): Int
}
