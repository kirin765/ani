package com.kiwankim.kiwankim.myapplication3.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwankim.kiwankim.myapplication3.R
import com.kiwankim.kiwankim.myapplication3.ui.AppViewModelProvider
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import com.kiwankim.kiwankim.myapplication3.ui.components.AnimeCard
import com.kiwankim.kiwankim.myapplication3.ui.components.EmptyState
import com.kiwankim.kiwankim.myapplication3.ui.components.ErrorState
import com.kiwankim.kiwankim.myapplication3.ui.components.LoadingState

@Composable
fun SearchScreen(
    onAnimeClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Column(modifier.fillMaxSize().padding(contentPadding)) {
        TextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cd_clear))
                    }
                }
            },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )

        if (genres.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(genres, key = { it }) { genre ->
                    FilterChip(
                        selected = genre == selectedGenre,
                        onClick = { viewModel.onGenreSelect(genre) },
                        label = { Text(genre) },
                    )
                }
            }
        }

        when (val s = results) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.messageRes, onRetry = viewModel::retry)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    EmptyState(
                        "🔍",
                        stringResource(R.string.search_empty_title),
                        stringResource(R.string.search_empty_subtitle),
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(s.data, key = { it.animeNo }) { anime ->
                            AnimeCard(
                                anime = anime,
                                isFavorite = anime.animeNo in favoriteIds,
                                onClick = { onAnimeClick(anime.animeNo) },
                                onToggleFavorite = { viewModel.toggleFavorite(anime) },
                            )
                        }
                    }
                }
            }
        }
    }
}
