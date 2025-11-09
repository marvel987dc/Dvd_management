package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Room database containing the movies table
@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var Instance: MovieDatabase? = null

        // Singleton pattern to get or create the database instance
        fun getDatabase(context: Context): MovieDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_db"
                )
                    // ✅ Force Room to write directly into the DB (no WAL)
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)

                    // ✅ Drop & recreate DB if schema changes
                    .fallbackToDestructiveMigration()

                    // ✅ Prepopulate database with sample movies
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("ROOM", "Database created, prepopulating sample movies...")

                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    Instance?.movieDao()?.apply {
                                        insert(
                                            Movie(
                                                movieId = 11,
                                                title = "Inception",
                                                director = "Christopher Nolan",
                                                price = 19.99,
                                                releaseDate = "2010-07-16",
                                                durationMinutes = 148,
                                                genre = "Thriller",
                                                isFavorite = true
                                            )
                                        )
                                        insert(
                                            Movie(
                                                movieId = 12,
                                                title = "The Lion King",
                                                director = "Roger Allers",
                                                price = 14.99,
                                                releaseDate = "1994-06-24",
                                                durationMinutes = 88,
                                                genre = "Family",
                                                isFavorite = false
                                            )
                                        )
                                        insert(
                                            Movie(
                                                movieId = 13,
                                                title = "Avengers: Endgame",
                                                director = "Anthony and Joe Russo",
                                                price = 24.99,
                                                releaseDate = "2019-04-26",
                                                durationMinutes = 181,
                                                genre = "Action",
                                                isFavorite = true
                                            )
                                        )
                                    }
                                    Log.d("ROOM", "Sample movies inserted successfully.")
                                } catch (e: Exception) {
                                    Log.e("ROOM", "Error inserting sample data: ${e.message}")
                                }
                            }
                        }
                    })
                    .build()

                Instance = instance
                instance
            }
        }
    }
}
