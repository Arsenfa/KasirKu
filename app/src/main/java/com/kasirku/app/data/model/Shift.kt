package com.kasirku.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class Shift(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cashierName: String,
    val openingBalance: Double = 0.0,
    val closingBalance: Double = 0.0,
    val totalSales: Double = 0.0,
    val transactionCount: Int = 0,
    val isOpen: Boolean = true,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = 0L
)
