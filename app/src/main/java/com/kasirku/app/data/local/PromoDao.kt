package com.kasirku.app.data.local

import androidx.room.*
import com.kasirku.app.data.model.PromoCode
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoDao {
    @Query("SELECT * FROM promo_codes WHERE isActive = 1 ORDER BY code")
    fun getAllActivePromos(): Flow<List<PromoCode>>

    @Query("SELECT * FROM promo_codes ORDER BY isActive DESC, code")
    fun getAllPromos(): Flow<List<PromoCode>>

    @Query("SELECT * FROM promo_codes WHERE isActive = 1 AND code = :code LIMIT 1")
    suspend fun findByCode(code: String): PromoCode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(promo: PromoCode): Long

    @Update
    suspend fun update(promo: PromoCode)

    @Query("UPDATE promo_codes SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("UPDATE promo_codes SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsage(id: Long)
}
