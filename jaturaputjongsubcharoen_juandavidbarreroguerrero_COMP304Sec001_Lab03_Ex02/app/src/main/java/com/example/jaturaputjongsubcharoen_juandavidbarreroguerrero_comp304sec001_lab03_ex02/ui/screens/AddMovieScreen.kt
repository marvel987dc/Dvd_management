package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.viewmodel.MovieViewModel
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieScreen(
    navController: NavController,
    viewModel: MovieViewModel = viewModel()
) {
    val state by viewModel.formState.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var genreExpanded by remember { mutableStateOf(false) }

    // Real-time error states
    var titleError by rememberSaveable { mutableStateOf<String?>(null) }
    var directorError by rememberSaveable { mutableStateOf<String?>(null) }
    var priceError by rememberSaveable { mutableStateOf<String?>(null) }
    var durationError by rememberSaveable { mutableStateOf<String?>(null) }
    var dateError by rememberSaveable { mutableStateOf<String?>(null) }
    var genreError by rememberSaveable { mutableStateOf<String?>(null) }

    val genres = listOf("Family", "Comedy", "Thriller", "Action", "Drama", "Sci-Fi")

    // Navigate back after successful add
    if (addSuccess) {
        LaunchedEffect(Unit) {
            viewModel.resetAddSuccess()
            navController.popBackStack()
        }
    }

    // Date Picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.LocalDate.ofInstant(
                            java.time.Instant.ofEpochMilli(millis),
                            java.time.ZoneOffset.UTC
                        ).toString()
                        viewModel.updateFormState(releaseDate = date)
                        dateError = null // clear error if selected
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Movie") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
        ) {

            // Auto Movie ID
            OutlinedTextField(
                value = state.movieId,
                onValueChange = {},
                label = { Text("Movie ID (Auto)") },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Title (validation added)
            OutlinedTextField(
                value = state.title,
                onValueChange = {
                    viewModel.updateFormState(title = it)
                    titleError = null
                },
                label = { Text("Title") },
                isError = titleError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            if (titleError != null) {
                Text(
                    text = titleError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // Director (live validation)
            OutlinedTextField(
                value = state.director,
                onValueChange = {
                    viewModel.updateFormState(director = it)
                    directorError = when {
                        it.isBlank() -> null
                        !Regex("^[A-Za-z ]+$").matches(it) ->
                            "Director name must contain only letters."
                        else -> null
                    }
                },
                label = { Text("Director") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = directorError != null
            )
            if (directorError != null) {
                Text(
                    text = directorError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // Price
            OutlinedTextField(
                value = state.price,
                onValueChange = {
                    viewModel.updateFormState(price = it)
                    val priceVal = it.toDoubleOrNull()
                    priceError = when {
                        it.isBlank() -> null
                        priceVal == null || priceVal <= 0 ->
                            "Price must be a positive number."
                        else -> null
                    }
                },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = priceError != null
            )
            if (priceError != null) {
                Text(
                    text = priceError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // Duration
            OutlinedTextField(
                value = state.durationMinutes,
                onValueChange = {
                    viewModel.updateFormState(durationMinutes = it)
                    val durationVal = it.toIntOrNull()
                    durationError = when {
                        it.isBlank() -> null
                        durationVal == null || durationVal <= 0 ->
                            "Duration must be greater than 0."
                        else -> null
                    }
                },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                isError = durationError != null
            )
            if (durationError != null) {
                Text(
                    text = durationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // Release Date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (dateError != null) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    if (state.releaseDate.isBlank()) "Select Release Date"
                    else state.releaseDate
                )
            }
            if (dateError != null) {
                Text(
                    text = dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Genre Dropdown
            ExposedDropdownMenuBox(
                expanded = genreExpanded,
                onExpandedChange = { genreExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.genre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Genre") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded)
                    },
                    isError = genreError != null,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = genreExpanded,
                    onDismissRequest = { genreExpanded = false }
                ) {
                    genres.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre) },
                            onClick = {
                                viewModel.updateFormState(genre = genre)
                                genreError = null
                                genreExpanded = false
                            }
                        )
                    }
                }
            }
            if (genreError != null) {
                Text(
                    text = genreError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            // Favorite switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Favorite?")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.isFavorite,
                    onCheckedChange = { viewModel.updateFormState(isFavorite = it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add button
            Button(
                onClick = {
                    var hasError = false

                    // Title
                    if (state.title.isBlank()) {
                        titleError = "Title is required."
                        hasError = true
                    } else titleError = null

                    // Director
                    if (state.director.isBlank() || !Regex("^[A-Za-z ]+$").matches(state.director)) {
                        directorError = if (state.director.isBlank()) "Director is required."
                        else "Director name must contain only letters."
                        hasError = true
                    } else directorError = null

                    // Price
                    val priceVal = state.price.toDoubleOrNull()
                    if (state.price.isBlank() || priceVal == null || priceVal <= 0) {
                        priceError = if (state.price.isBlank()) "Price is required."
                        else "Price must be a positive number."
                        hasError = true
                    } else priceError = null

                    // Duration
                    val durationVal = state.durationMinutes.toIntOrNull()
                    if (state.durationMinutes.isBlank() || durationVal == null || durationVal <= 0) {
                        durationError = if (state.durationMinutes.isBlank()) "Duration is required."
                        else "Duration must be greater than 0."
                        hasError = true
                    } else durationError = null

                    // Release Date
                    if (state.releaseDate.isBlank()) {
                        dateError = "Please select a release date."
                        hasError = true
                    } else dateError = null

                    // Genre
                    if (state.genre.isBlank()) {
                        genreError = "Please choose a genre."
                        hasError = true
                    } else genreError = null

                    // Add only if all good
                    if (!hasError) viewModel.validateAndAddMovie()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            ) {
                Text("Add Movie")
            }
        }
    }
}
