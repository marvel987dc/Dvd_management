package com.example.productapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Data Access Object for product-related database operations
@Dao
interface ProductDao {

    @Query("SELECT * FROM product_table")
    fun getAll(): Flow<List<Product>>    // Retrieves all products as a Flow

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product) // Inserts a product, replacing if conflict occurs

    @Update
    suspend fun update(product: Product) // Updates an existing product

    @Delete
    suspend fun delete(product: Product) // Deletes a product

    @Query("SELECT * FROM product_table WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<Product>> // Retrieves favorite products as a Flow

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSampleProducts(products: List<Product>) // Inserts sample data, ignoring conflicts
}
