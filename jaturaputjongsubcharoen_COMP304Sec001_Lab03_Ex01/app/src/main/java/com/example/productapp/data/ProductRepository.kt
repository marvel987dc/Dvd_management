package com.example.productapp.data
import kotlinx.coroutines.flow.Flow

// Repository class managing data operations between UI and database
class ProductRepository(private val dao: ProductDao) {
    val products: Flow<List<Product>> = dao.getAll()        // Stream of all products
    val favoriteProducts: Flow<List<Product>> = dao.getFavorites() // Stream of favorite products

    suspend fun addProduct(product: Product) { dao.insert(product) }    // Adds a new product
    suspend fun updateProduct(product: Product) { dao.update(product) } // Updates an existing product
    suspend fun deleteProduct(product: Product) { dao.delete(product) } // Deletes a product

    // Inserts initial sample products if needed
    suspend fun insertSampleProducts(products: List<Product>) {
        dao.insertSampleProducts(products)
    }
}