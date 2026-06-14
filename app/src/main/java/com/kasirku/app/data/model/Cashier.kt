package com.kasirku.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cashiers")
data class Cashier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val pin: String,
    val role: String = ROLE_CASHIER, // "admin" or "cashier"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_CASHIER = "cashier"
    }

    fun isAdmin(): Boolean = role == ROLE_ADMIN
    fun isCashier(): Boolean = role == ROLE_CASHIER
}
