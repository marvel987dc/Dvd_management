package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.screens

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
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.viewmodel.MovieViewModel
import java.time.Instant
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate
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
    var directorError by rememberSaveable { mutableStateOf<String?>(null) }
    var priceError by rememberSaveable { mutableStateOf<String?>(null) }
    var durationError by rememberSaveable { mutableStateOf<String?>(null) }

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
                        // FIXED DATE OFFSET ISSUE (use UTC, not system default)
                        val date = java.time.LocalDate.ofInstant(
                            java.time.Instant.ofEpochMilli(millis),
                            java.time.ZoneOffset.UTC
                        ).toString()
                        viewModel.updateFormState(releaseDate = date)
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

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.updateFormState(title = it) },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

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

            // Price (live validation)
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

            // Duration (live validation)
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (state.releaseDate.isBlank()) "Select Release Date"
                    else state.releaseDate
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Genre Dropdown
            ExposedDropdownMenuBox(
                expanded = genreExpanded,
                onExpandedChange = { genreExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = state.genre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Genre") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded)
                    },
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
                                genreExpanded = false
                            }
                        )
                    }
                }
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
                onClick = { viewModel.validateAndAddMovie() },
                modifier = Modifier.fillMaxWidth(),
                enabled = directorError == null && priceError == null && durationError == null
            ) {
                Text("Add Movie")
            }
        }
    }
}
