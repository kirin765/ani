package com.kiwankim.kiwankim.myapplication3.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwankim.kiwankim.myapplication3.domain.Weekday
import com.kiwankim.kiwankim.myapplication3.ui.AppViewModelProvider
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import com.kiwankim.kiwankim.myapplication3.ui.components.AnimeCard
import com.kiwankim.kiwankim.myapplication3.ui.components.EmptyState
import com.kiwankim.kiwankim.myapplication3.ui.components.ErrorState
import com.kiwankim.kiwankim.myapplication3.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onAnimeClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val selectedWeek by viewModel.selectedWeek.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    Column(modifier.fillMaxSize().padding(contentPadding)) {
        val tabs = Weekday.tabs
        val selectedIndex = tabs.indexOf(selectedWeek).coerceAtLeast(0)
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            tabs.forEachIndexed { index, week ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { viewModel.selectWeek(week) },
                    text = {
                        Text(
                            week.label,
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                )
            }
        }

        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(s.message, onRetry = viewModel::refresh)
            is UiState.Success -> {
                if (s.data.isEmpty()) {
                    EmptyState("🍥", "편성작이 없어요", "다른 요일을 확인해 보세요.")
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
