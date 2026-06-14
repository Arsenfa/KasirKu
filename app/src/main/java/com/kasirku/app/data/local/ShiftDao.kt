package com.kasirku.app.data.local

import androidx.room.*
import com.kasirku.app.data.model.Shift
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts ORDER BY startTime DESC")
    fun getAllShifts(): Flow<List<Shift>>

    @Query("SELECT * FROM shifts WHERE isOpen = 1 LIMIT 1")
    fun getActiveShift(): Flow<Shift?>

    @Query("SELECT * FROM shifts WHERE isOpen = 1 LIMIT 1")
    suspend fun getActiveShiftSync(): Shift?

    @Insert
    suspend fun insert(shift: Shift): Long

    @Update
    suspend fun update(shift: Shift)

    @Query("SELECT COUNT(*) FROM shifts WHERE startTime >= :since AND isOpen = 0")
    fun getClosedShiftCountSince(since: Long): Flow<Int>
}
