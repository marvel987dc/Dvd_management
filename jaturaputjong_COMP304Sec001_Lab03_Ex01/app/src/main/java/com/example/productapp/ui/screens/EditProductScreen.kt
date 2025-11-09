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

// Screen for editing an existing product
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productId: Int?,
    viewModel: ProductViewModel = viewModel()
) {
    val products by viewModel.allProducts.observeAsState(emptyList())
    println("EditProductScreen: productId=$productId, products=$products") // Log for debugging

    if (productId == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val product = products.find { it.id == productId }
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator() // Show loading while waiting for product
        }
        return
    }

    var editedName by remember { mutableStateOf(product.name) }
    var editedPrice by remember { mutableStateOf(product.price.toString()) }
    var editedCategory by remember { mutableStateOf(product.category) }
    var editedFavorite by remember { mutableStateOf(product.isFavorite) }
    var expanded by remember { mutableStateOf(false) } // State for dropdown expansion
    val categories = listOf("Electronics", "Appliances", "Cell Phone", "Media")

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = editedPrice,
            onValueChange = { editedPrice = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

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
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Favorite:")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = editedFavorite, onCheckedChange = { editedFavorite = it })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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

            Button(onClick = {
                val updatedProduct = product.copy(
                    name = editedName,
                    price = editedPrice.toDoubleOrNull() ?: 0.0,
                    category = editedCategory,
                    isFavorite = editedFavorite
                )
                viewModel.update(updatedProduct)
                navController.popBackStack()
            }) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save")
            }
        }
    }
}