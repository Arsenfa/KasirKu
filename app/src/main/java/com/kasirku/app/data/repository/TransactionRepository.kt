package com.kasirku.app.data.repository

import com.kasirku.app.data.local.PaymentMethodSummary
import com.kasirku.app.data.local.TransactionDao
import com.kasirku.app.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()
    fun getTransactionsSince(startTime: Long): Flow<List<Transaction>> = dao.getTransactionsSince(startTime)
    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<Transaction>> = dao.getTransactionsBetween(startTime, endTime)
    fun searchTransactions(query: String): Flow<List<Transaction>> = dao.searchTransactions(query)
    fun getTransactionCountSince(startTime: Long): Flow<Int> = dao.getTransactionCountSince(startTime)
    fun getTransactionCountBetween(startTime: Long, endTime: Long): Flow<Int> = dao.getTransactionCountBetween(startTime, endTime)
    fun getTotalSalesSince(startTime: Long): Flow<Double?> = dao.getTotalSalesSince(startTime)
    fun getTotalSalesBetween(startTime: Long, endTime: Long): Flow<Double?> = dao.getTotalSalesBetween(startTime, endTime)
    fun getTotalCostSince(startTime: Long): Flow<Double?> = dao.getTotalCostSince(startTime)
    fun getSalesByPaymentMethod(startTime: Long): Flow<List<PaymentMethodSummary>> = dao.getSalesByPaymentMethod(startTime)
    fun getTransactionItemsSince(startTime: Long): Flow<List<String>> = dao.getTransactionItemsSince(startTime)

    suspend fun insert(transaction: Transaction): Long = dao.insert(transaction)
}
