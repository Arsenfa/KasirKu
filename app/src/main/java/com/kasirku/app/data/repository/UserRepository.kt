package com.kasirku.app.data.repository

import com.kasirku.app.data.local.UserDao
import com.kasirku.app.data.model.Cashier
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {
    fun getAllUsers(): Flow<List<Cashier>> = dao.getAllUsers()
    fun getCashiers(): Flow<List<Cashier>> = dao.getUsersByRole(Cashier.ROLE_CASHIER)
    suspend fun getUserById(id: Long): Cashier? = dao.getUserById(id)
    suspend fun authenticate(name: String, pin: String): Cashier? = dao.authenticate(name, pin)
    suspend fun insert(cashier: Cashier): Long = dao.insert(cashier)
    suspend fun update(cashier: Cashier) = dao.update(cashier)
    suspend fun delete(id: Long) = dao.softDelete(id)
    suspend fun resetPin(id: Long, newPin: String) = dao.resetPin(id, newPin)
    fun getUserCount(): Flow<Int> = dao.getUserCount()
    suspend fun getAdminCount(): Int = dao.getAdminCount()
}
