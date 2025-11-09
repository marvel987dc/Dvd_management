package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data

import kotlinx.coroutines.flow.Flow

class MovieRepository(private val dao: MovieDao) {

    // Reactive flows
    val allMovies: Flow<List<Movie>> = dao.getAllMovies()
    val favoriteMovies: Flow<List<Movie>> = dao.getFavoriteMovies()

    // Single movie query
    fun getMovieByMovieId(movieId: Int): Flow<Movie?> = dao.getMovieByMovieId(movieId)

    // Add this function (so ViewModel can call getAllMovies().first())
    fun fetchAllMovies(): Flow<List<Movie>> = dao.getAllMovies()

    // CRUD
    suspend fun insert(movie: Movie) = dao.insert(movie)
    suspend fun update(movie: Movie) = dao.update(movie)
    suspend fun delete(movie: Movie) = dao.delete(movie)

    // Check existence
    suspend fun movieIdExists(movieId: Int): Boolean = dao.countByMovieId(movieId) > 0
}
