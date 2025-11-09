package com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.data.Movie
import com.example.jaturaputjongsubcharoen_juandavidbarreroguerrero_comp304sec001_lab03_ex02.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MovieViewModel = viewModel()
) {
    var showFavorites by remember { mutableStateOf(false) }

    val movies by if (showFavorites) {
        viewModel.favoriteMovies.collectAsState()
    } else {
        viewModel.allMovies.collectAsState()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DVD Movie Collection") },
                actions = {
                    IconButton(onClick = { navController.navigate("addMovie") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add movie"
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showFavorites = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("All Movies")
                }
                Button(
                    onClick = { showFavorites = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Favorites")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (movies.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No movies yet")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
                            onEdit = {
                                navController.navigate("editMovie/${movie.movieId}")
                            },
                            onDelete = { viewModel.deleteMovie(movie) },
                            onToggleFavorite = { viewModel.toggleFavorite(movie) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                    Text("ID: ${movie.movieId}", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(onClick = onToggleFavorite) {
                    if (movie.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Unmark favorite"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Mark as favorite"
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit movie"
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete movie"
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Director: ${movie.director}")
            Text("Genre: ${movie.genre}")
            Text("Duration: ${movie.durationMinutes} min")
            Text("Price: $${movie.price}", fontWeight = FontWeight.SemiBold)
            Text("Release: ${movie.releaseDate}")
        }
    }
}
