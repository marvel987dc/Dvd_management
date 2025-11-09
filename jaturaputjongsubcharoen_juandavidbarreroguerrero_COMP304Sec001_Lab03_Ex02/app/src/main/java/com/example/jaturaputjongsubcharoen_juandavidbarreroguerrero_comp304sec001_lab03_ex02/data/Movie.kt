package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                   // internal DB id

    @ColumnInfo(name = "movieId")
    val movieId: Int,                  // 2-digit ID 11–99

    val title: String,
    val director: String,
    val price: Double,
    val releaseDate: String,           // yyyy-MM-dd
    val durationMinutes: Int,
    val genre: String,
    val isFavorite: Boolean = false
)
