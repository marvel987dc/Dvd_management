package com.example.productapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.productapp.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productId: Int?,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.allProducts.observeAsState(emptyList())

    if (productId == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val product = products.find { it.id == productId }
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Field states
    var editedName by remember { mutableStateOf(product.name) }
    var editedPrice by remember { mutableStateOf(product.price.toString()) }
    var editedCategory by remember { mutableStateOf(product.category) }
    var editedFavorite by remember { mutableStateOf(product.isFavorite) }

    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Appliances", "Cell Phone", "Media")

    // Validation error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {

        // Product Name
        OutlinedTextField(
            value = editedName,
            onValueChange = {
                editedName = it
                nameError = if (it.isBlank()) "Product name is required." else null
            },
            label = { Text("Product Name") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        nameError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        // Price
        OutlinedTextField(
            value = editedPrice,
            onValueChange = {
                editedPrice = it
                val priceVal = it.toDoubleOrNull()
                priceError = when {
                    it.isBlank() -> "Price is required."
                    priceVal == null || priceVal <= 0 -> "Price must be a positive number."
                    else -> null
                }
            },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = priceError != null,
            modifier = Modifier.fillMaxWidth()
        )
        priceError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = editedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Category Dropdown"
                    )
                },
                isError = categoryError != null,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            editedCategory = category
                            categoryError = null
                            expanded = false
                        }
                    )
                }
            }
        }
        if (editedCategory.isBlank()) {
            categoryError = "Category is required."
        }
        categoryError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }

        // Favorite switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Favorite:")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = editedFavorite,
                onCheckedChange = { editedFavorite = it }
            )
        }

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Delete
            Button(
                onClick = {
                    viewModel.delete(product)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete")
            }

            // Save
            Button(
                onClick = {
                    var hasError = false

                    if (editedName.isBlank()) {
                        nameError = "Product name is required."
                        hasError = true
                    }

                    val priceVal = editedPrice.toDoubleOrNull()
                    if (editedPrice.isBlank() || priceVal == null || priceVal <= 0) {
                        priceError = "Price must be a positive number."
                        hasError = true
                    }

                    if (editedCategory.isBlank()) {
                        categoryError = "Category is required."
                        hasError = true
                    }

                    // Proceed only if all inputs are valid
                    if (!hasError) {
                        val updatedProduct = product.copy(
                            name = editedName.trim(),
                            price = priceVal ?: 0.0,
                            category = editedCategory,
                            isFavorite = editedFavorite
                        )
                        viewModel.update(updatedProduct)
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save")
            }
        }
    }
}