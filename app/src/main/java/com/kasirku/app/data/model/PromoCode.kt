package com.kasirku.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promo_codes")
data class PromoCode(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val isActive: Boolean = true,
    val usageLimit: Int = 0, // 0 = unlimited
    val usageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean = usageLimit > 0 && usageCount >= usageLimit
}
