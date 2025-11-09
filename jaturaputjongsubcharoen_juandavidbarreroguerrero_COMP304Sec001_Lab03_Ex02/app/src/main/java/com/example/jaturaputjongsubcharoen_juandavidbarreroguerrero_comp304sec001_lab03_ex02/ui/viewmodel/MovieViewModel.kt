package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data.Movie
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data.MovieDatabase
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data.MovieRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository

    val allMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())

    // --- UI Form State ---
    data class MovieFormState(
        val movieId: String = "",
        val title: String = "",
        val director: String = "",
        val price: String = "",
        val durationMinutes: String = "",
        val releaseDate: String = "",
        val genre: String = "",
        val isFavorite: Boolean = false,
        val errors: List<String> = emptyList(),
        val directorError: String? = null,
        val priceError: String? = null
    )

    private val _formState = MutableStateFlow(MovieFormState())
    val formState: StateFlow<MovieFormState> = _formState.asStateFlow()

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    init {
        val dao = MovieDatabase.getDatabase(application).movieDao()
        repository = MovieRepository(dao)

        // Collect all movies and favorites
        viewModelScope.launch {
            repository.allMovies.collect { allMovies.value = it }
        }
        viewModelScope.launch {
            repository.favoriteMovies.collect { favoriteMovies.value = it }
        }

        // Automatically assign next Movie ID
        viewModelScope.launch {
            val movies = repository.allMovies.first()

            // Get sorted existing IDs between 11–99
            val sortedIds = movies.map { it.movieId }.sorted()

            // Find the first missing ID in sequence (like 13 if 11,12,14 exist)
            val nextId = (11..99).firstOrNull { it !in sortedIds } ?: (sortedIds.maxOrNull()?.plus(1) ?: 11)

            _formState.value = _formState.value.copy(movieId = nextId.toString())
        }
    }

    // --- Utility resets ---
    fun resetAddSuccess() { _addSuccess.value = false }
    fun resetUpdateSuccess() { _updateSuccess.value = false }

    // --- Update form fields dynamically ---
    fun updateFormState(
        movieId: String = _formState.value.movieId,
        title: String = _formState.value.title,
        director: String = _formState.value.director,
        price: String = _formState.value.price,
        releaseDate: String = _formState.value.releaseDate,
        durationMinutes: String = _formState.value.durationMinutes,
        genre: String = _formState.value.genre,
        isFavorite: Boolean = _formState.value.isFavorite
    ) {
        _formState.value = _formState.value.copy(
            movieId = movieId,
            title = title,
            director = director,
            price = price,
            releaseDate = releaseDate,
            durationMinutes = durationMinutes,
            genre = genre,
            isFavorite = isFavorite
        )
    }

    // --- Validation Rules ---
    private fun validateForm(): MovieFormState {
        val state = _formState.value
        val errors = mutableListOf<String>()
        var directorError: String? = null
        var priceError: String? = null

        // Validate title
        if (state.title.isBlank()) errors.add("Title is required")

        // Validate director (only letters & spaces)
        val directorRegex = Regex("^[A-Za-z ]+$")
        if (state.director.isBlank()) {
            errors.add("Director is required")
        } else if (!directorRegex.matches(state.director)) {
            directorError = "Director name must contain only letters."
        }

        // Validate price (positive numeric)
        val priceVal = state.price.toDoubleOrNull()
        if (state.price.isBlank()) {
            errors.add("Price is required")
        } else if (priceVal == null || priceVal <= 0.0) {
            priceError = "Price must be a positive number."
        }

        // Validate duration
        val duration = state.durationMinutes.toIntOrNull()
        if (duration == null || duration <= 0) {
            errors.add("Duration must be greater than 0.")
        }

        // Validate release date
        if (state.releaseDate.isBlank()) errors.add("Release date is required")

        // Validate genre
        if (state.genre.isBlank()) errors.add("Genre must be selected")

        return state.copy(
            errors = errors,
            directorError = directorError,
            priceError = priceError
        )
    }

    // --- Add Movie ---
    fun validateAndAddMovie() {
        viewModelScope.launch {
            val validated = validateForm()
            val currentErrors = validated.errors.toMutableList()

            // Stop if any validation failed
            if (validated.directorError != null || validated.priceError != null || currentErrors.isNotEmpty()) {
                _formState.value = validated
                _addSuccess.value = false
                return@launch
            }

            val movie = Movie(
                movieId = validated.movieId.toInt(),
                title = validated.title,
                director = validated.director,
                price = validated.price.toDouble(),
                releaseDate = validated.releaseDate,
                durationMinutes = validated.durationMinutes.toInt(),
                genre = validated.genre,
                isFavorite = validated.isFavorite
            )

            repository.insert(movie)

            // Compute next ID for next movie
            val nextId = validated.movieId.toInt() + 1

            _formState.value = MovieFormState(movieId = nextId.toString())
            _addSuccess.value = true
        }
    }

    // --- Load movie for editing ---
    fun loadMovieForEdit(movieId: Int) {
        viewModelScope.launch {
            repository.getMovieByMovieId(movieId).collect { movie ->
                movie?.let {
                    _formState.value = MovieFormState(
                        movieId = it.movieId.toString(),
                        title = it.title,
                        director = it.director,
                        price = it.price.toString(),
                        releaseDate = it.releaseDate,
                        durationMinutes = it.durationMinutes.toString(),
                        genre = it.genre,
                        isFavorite = it.isFavorite
                    )
                }
            }
        }
    }

    // --- Update existing movie ---
    fun validateAndUpdateMovie(originalMovieId: Int) {
        viewModelScope.launch {
            val validated = validateForm()
            if (validated.directorError != null || validated.priceError != null || validated.errors.isNotEmpty()) {
                _formState.value = validated
                _updateSuccess.value = false
                return@launch
            }

            val existing = allMovies.value.firstOrNull { it.movieId == originalMovieId } ?: return@launch

            val updated = existing.copy(
                title = validated.title,
                director = validated.director,
                price = validated.price.toDouble(),
                releaseDate = validated.releaseDate,
                durationMinutes = validated.durationMinutes.toInt(),
                genre = validated.genre,
                isFavorite = validated.isFavorite
            )

            repository.update(updated)
            _updateSuccess.value = true
        }
    }

    // --- Delete & Favorite toggles ---
    fun deleteMovie(movie: Movie) {
        viewModelScope.launch { repository.delete(movie) }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.update(movie.copy(isFavorite = !movie.isFavorite))
        }
    }
}