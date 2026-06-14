package com.kasirku.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kasirku.app.data.model.Cashier
import com.kasirku.app.data.model.Product
import com.kasirku.app.data.model.PromoCode
import com.kasirku.app.data.model.Shift
import com.kasirku.app.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, Transaction::class, Cashier::class, PromoCode::class, Shift::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao
    abstract fun promoDao(): PromoDao
    abstract fun shiftDao(): ShiftDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kasirku_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.userDao()?.let { dao ->
                                    dao.insert(Cashier(name = "Admin", pin = "0000", role = Cashier.ROLE_ADMIN))
                                    dao.insert(Cashier(name = "Andi", pin = "1234", role = Cashier.ROLE_CASHIER))
                                    dao.insert(Cashier(name = "Sari", pin = "5678", role = Cashier.ROLE_CASHIER))
                                    dao.insert(Cashier(name = "Budi", pin = "9012", role = Cashier.ROLE_CASHIER))
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
