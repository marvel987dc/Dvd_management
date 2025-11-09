package com.example.movieapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie_table ORDER BY movieId")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE isFavorite = 1 ORDER BY movieId")
    fun getFavoriteMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movie_table WHERE movieId = :movieId LIMIT 1")
    fun getMovieByMovieId(movieId: Int): Flow<Movie?>

    @Query("SELECT COUNT(*) FROM movie_table WHERE movieId = :movieId")
    suspend fun countByMovieId(movieId: Int): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)
}
