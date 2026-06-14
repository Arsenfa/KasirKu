package com.kasirku.app.data.local

import androidx.room.*
import com.kasirku.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE createdAt >= :startTime ORDER BY createdAt DESC")
    fun getTransactionsSince(startTime: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE invoiceNumber LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE createdAt >= :startTime")
    fun getTransactionCountSince(startTime: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM transactions WHERE createdAt >= :startTime AND createdAt <= :endTime")
    fun getTransactionCountBetween(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT SUM(total) FROM transactions WHERE createdAt >= :startTime")
    fun getTotalSalesSince(startTime: Long): Flow<Double?>

    @Query("SELECT SUM(total) FROM transactions WHERE createdAt >= :startTime AND createdAt <= :endTime")
    fun getTotalSalesBetween(startTime: Long, endTime: Long): Flow<Double?>

    @Query("SELECT SUM(total) FROM transactions WHERE createdAt >= :startTime AND paymentMethod = :method")
    fun getTotalSalesByPaymentMethod(startTime: Long, method: String): Flow<Double?>

    @Query("SELECT paymentMethod, COUNT(*) as count, SUM(total) as total FROM transactions WHERE createdAt >= :startTime GROUP BY paymentMethod")
    fun getSalesByPaymentMethod(startTime: Long): Flow<List<PaymentMethodSummary>>

    @Query("SELECT items FROM transactions WHERE createdAt >= :startTime")
    fun getTransactionItemsSince(startTime: Long): Flow<List<String>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT SUM(total) FROM transactions WHERE createdAt >= :startTime")
    fun getTotalProfitSince(startTime: Long): Flow<Double?>

    @Query("SELECT SUM(totalCost) FROM transactions WHERE createdAt >= :startTime")
    fun getTotalCostSince(startTime: Long): Flow<Double?>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

data class PaymentMethodSummary(
    val paymentMethod: String,
    val count: Int,
    val total: Double
)
