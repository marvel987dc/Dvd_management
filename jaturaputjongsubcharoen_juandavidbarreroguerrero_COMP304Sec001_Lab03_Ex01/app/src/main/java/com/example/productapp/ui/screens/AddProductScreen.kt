package com.example.productapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.productapp.ui.viewmodel.ProductViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel()
) {
    val state = viewModel.addProductState.collectAsState().value
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Appliances", "Cell Phone", "Media")

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(),
            yearRange = 2025..2030
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                .toString()
                            viewModel.updateFormState(deliveryDate = date)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add a New Product") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Display validation errors if any
                    state.errors.forEach { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Product Name
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.updateFormState(name = it) },
                        label = { Text("Product Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    // Product ID (Auto) and Price side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Product ID (Auto)
                        OutlinedTextField(
                            value = state.id,
                            onValueChange = {},
                            label = { Text("Product ID (Auto)") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        )

                        // Price
                        OutlinedTextField(
                            value = state.price,
                            onValueChange = { viewModel.updateFormState(price = it) },
                            label = { Text("Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Quantity field with validation
                    OutlinedTextField(
                        value = state.quantity,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toIntOrNull()?.let { it >= 0 } == true) {
                                viewModel.updateFormState(quantity = newValue)
                            }
                        },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.quantity.toIntOrNull()?.let { it <= 0 } == true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    if (state.quantity.toIntOrNull()?.let { it <= 0 } == true) {
                        Text(
                            text = "Quantity must be greater than 0",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 8.dp, top = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Date Picker Button
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(state.deliveryDate.ifEmpty { "Select Delivery Date" })
                    }

                    Spacer(Modifier.height(16.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
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
                                        viewModel.updateFormState(category = category)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Favorite Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Favorite?")
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = state.isFavorite,
                            onCheckedChange = { viewModel.updateFormState(isFavorite = it) }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Add Product Button
                    val success by viewModel.addProductSuccess.collectAsState()
                    Button(
                        onClick = { viewModel.validateAndAddProduct() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Product")
                    }

                    // Navigate after success
                    LaunchedEffect(success) {
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("add") { inclusive = true }
                            }
                            viewModel.resetSuccessState()
                        }
                    }
                }
            }
        }
    }
}
