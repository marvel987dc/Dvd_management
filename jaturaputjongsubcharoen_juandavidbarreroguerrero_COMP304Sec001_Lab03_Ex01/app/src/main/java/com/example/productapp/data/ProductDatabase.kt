package com.example.productapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Room database containing the products table
@Database(entities = [Product::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao // Provides access to ProductDao

    companion object {
        @Volatile
        private var Instance: ProductDatabase? = null

        // Singleton pattern to get or create the database instance
        fun getDatabase(context: Context): ProductDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_db"
                )
                    // This ensures data is written directly to the DB file (no WAL mode)
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)

                    // Prepopulate database with sample data on first creation
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("ROOM", "Database created and prepopulating data...")

                            CoroutineScope(Dispatchers.IO).launch {
                                Instance?.productDao()?.insertSampleProducts(
                                    listOf(
                                        Product(
                                            productId = "101",
                                            name = "Phone",
                                            price = 299.99,
                                            quantity = 10,
                                            category = "Electronics",
                                            isFavorite = false,
                                            deliveryDate = "2025-04-01"
                                        ),
                                        Product(
                                            productId = "102",
                                            name = "Laptop",
                                            price = 999.99,
                                            quantity = 5,
                                            category = "Electronics",
                                            isFavorite = true,
                                            deliveryDate = "2025-04-02"
                                        ),
                                        Product(
                                            productId = "103",
                                            name = "Microwave",
                                            price = 149.99,
                                            quantity = 8,
                                            category = "Appliances",
                                            isFavorite = false,
                                            deliveryDate = "2025-04-03"
                                        )
                                    )
                                )
                                Log.d("ROOM", "Sample products inserted successfully.")
                            }
                        }
                    })
                    .build()

                Instance = instance
                instance
            }
        }
    }
}
