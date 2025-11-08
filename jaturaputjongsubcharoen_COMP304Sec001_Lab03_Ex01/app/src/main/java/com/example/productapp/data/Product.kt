package com.example.productapp.data
import androidx.room.PrimaryKey
import androidx.room.Entity

// Represents a product entity in the Room database
@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val isFavorite: Boolean = false,
    val deliveryDate: String? = null
)