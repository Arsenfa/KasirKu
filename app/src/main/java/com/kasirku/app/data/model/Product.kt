package com.kasirku.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val costPrice: Double = 0.0,
    val category: String,
    val description: String = "",
    val imageUrl: String = "",
    val barcode: String = "",
    val stock: Int = 0,
    val lowStockThreshold: Int = 5,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun isLowStock(): Boolean = stock <= lowStockThreshold
}
