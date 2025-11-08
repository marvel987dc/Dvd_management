package com.example.productapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.productapp.data.Product
import com.example.productapp.data.ProductDatabase
import com.example.productapp.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    val favoriteProducts: LiveData<List<Product>>
    private val _addProductSuccess = MutableStateFlow(false)
    val addProductSuccess: StateFlow<Boolean> = _addProductSuccess.asStateFlow()

    // Form state handling
    data class AddProductState(
        val id: String = "",
        val name: String = "",
        val price: String = "",
        val quantity: String = "",
        val deliveryDate: String = "",
        val category: String = "",
        val isFavorite: Boolean = false,
        val errors: List<String> = emptyList()
    )

    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState: StateFlow<AddProductState> = _addProductState.asStateFlow()

    init {
        val dao = ProductDatabase.getDatabase(application).productDao()
        repository = ProductRepository(dao)
        allProducts = repository.products.asLiveData()
        favoriteProducts = repository.favoriteProducts.asLiveData()

        // Observe products list and update next ID when loaded
        allProducts.observeForever { productList ->
            if (productList.isNotEmpty()) {
                val nextId = (productList.mapNotNull { it.productId.toIntOrNull() }.maxOrNull() ?: 100) + 1
                _addProductState.update { it.copy(id = nextId.toString()) }
            } else {
                // If empty list, start from 101
                _addProductState.update { it.copy(id = "101") }
            }
        }
    }

    // Generate the next product ID based on the current max value
    private fun generateNextProductId(): String {
        val currentList = allProducts.value ?: emptyList()
        val maxId = currentList.mapNotNull { it.productId.toIntOrNull() }.maxOrNull() ?: 100
        return (maxId + 1).toString()
    }

    fun validateAndAddProduct() {
        val state = _addProductState.value
        val errors = mutableListOf<String>()

        // ID validation (auto-generated, but still checked)
        val id = state.id.toIntOrNull()
        if (id == null || id !in 101..999) errors.add("Invalid ID (101-999)")

        // Price validation
        val price = state.price.toDoubleOrNull()
        if (price == null || price <= 0) errors.add("Price must be positive")

        // Quantity validation (must be > 0)
        val quantity = state.quantity.toIntOrNull()
        if (quantity == null || quantity <= 0) errors.add("Quantity must be greater than 0")

        // Date validation
        val currentDate = LocalDate.now()
        val deliveryDate = try {
            LocalDate.parse(state.deliveryDate)
        } catch (e: Exception) {
            null
        }
        if (deliveryDate == null || deliveryDate.isBefore(currentDate)) {
            errors.add("Invalid delivery date")
        }

        // Category validation
        if (state.category !in listOf("Electronics", "Appliances", "Cell Phone", "Media")) {
            errors.add("Select a category")
        }

        if (errors.isEmpty()) {
            insert(
                Product(
                    id = 0,
                    productId = state.id,
                    name = state.name,
                    price = price!!,
                    quantity = quantity!!,
                    category = state.category,
                    isFavorite = state.isFavorite,
                    deliveryDate = state.deliveryDate
                )
            )

            // Clear errors and mark success
            _addProductState.update {
                it.copy(
                    errors = emptyList(),
                    id = generateNextProductId(), // prepare next product ID
                    name = "",
                    price = "",
                    quantity = "",
                    category = "",
                    deliveryDate = "",
                    isFavorite = false
                )
            }

            _addProductSuccess.value = true
        } else {
            _addProductState.update { it.copy(errors = errors) }
            _addProductSuccess.value = false
        }
    }

    // Update form fields
    fun updateFormState(
        id: String = _addProductState.value.id,
        name: String = _addProductState.value.name,
        price: String = _addProductState.value.price,
        category: String = _addProductState.value.category,
        isFavorite: Boolean = _addProductState.value.isFavorite,
        deliveryDate: String = _addProductState.value.deliveryDate,
        quantity: String = _addProductState.value.quantity
    ) {
        _addProductState.value = _addProductState.value.copy(
            id = id,
            name = name,
            price = price,
            category = category,
            isFavorite = isFavorite,
            deliveryDate = deliveryDate,
            quantity = quantity
        )
    }

    fun toggleFavorite(product: Product) {
        val updatedProduct = product.copy(isFavorite = !product.isFavorite)
        viewModelScope.launch {
            repository.updateProduct(updatedProduct)
        }
    }

    fun resetSuccessState() {
        _addProductSuccess.value = false
    }

    // CRUD operations
    private fun insert(product: Product) = viewModelScope.launch { repository.addProduct(product) }
    fun update(product: Product) = viewModelScope.launch { repository.updateProduct(product) }
    fun delete(product: Product) = viewModelScope.launch { repository.deleteProduct(product) }
}
