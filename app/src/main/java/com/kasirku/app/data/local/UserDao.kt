package com.kasirku.app.data.local

import androidx.room.*
import com.kasirku.app.data.model.Cashier
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM cashiers WHERE isActive = 1 ORDER BY role, name")
    fun getAllUsers(): Flow<List<Cashier>>

    @Query("SELECT * FROM cashiers WHERE isActive = 1 AND role = :role ORDER BY name")
    fun getUsersByRole(role: String): Flow<List<Cashier>>

    @Query("SELECT * FROM cashiers WHERE id = :id")
    suspend fun getUserById(id: Long): Cashier?

    @Query("SELECT * FROM cashiers WHERE isActive = 1 AND name = :name AND pin = :pin")
    suspend fun authenticate(name: String, pin: String): Cashier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashier: Cashier): Long

    @Update
    suspend fun update(cashier: Cashier)

    @Query("UPDATE cashiers SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE cashiers SET pin = :newPin WHERE id = :id")
    suspend fun resetPin(id: Long, newPin: String)

    @Query("SELECT COUNT(*) FROM cashiers WHERE isActive = 1")
    fun getUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cashiers WHERE isActive = 1")
    suspend fun getUserCountSync(): Int

    @Query("SELECT COUNT(*) FROM cashiers WHERE isActive = 1 AND role = 'admin'")
    suspend fun getAdminCount(): Int
}
