package com.kasirku.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val items: String, // JSON serialized list of cart items
    val subtotal: Double,
    val taxPercent: Double,
    val taxAmount: Double,
    val total: Double,
    val totalCost: Double = 0.0, // total modal (cost price * qty)
    val discountAmount: Double = 0.0,
    val promoCode: String = "",
    val paymentMethod: String, // "Tunai", "QRIS", "Transfer"
    val amountPaid: Double,
    val change: Double,
    val cashierName: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val profit: Double get() = subtotal - totalCost
    val marginPercent: Double get() = if (subtotal > 0) (profit / subtotal) * 100 else 0.0
}
