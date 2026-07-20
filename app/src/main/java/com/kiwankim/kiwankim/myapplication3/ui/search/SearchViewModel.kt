package com.kiwankim.kiwankim.myapplication3.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwankim.kiwankim.myapplication3.data.AnimeRepository
import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _all = MutableStateFlow<UiState<List<Anime>>>(UiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _genre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _genre.asStateFlow()

    val genres: StateFlow<List<String>> = _all
        .map { state ->
            (state as? UiState.Success)?.data
                ?.flatMap { it.genres }
                ?.groupingBy { it }?.eachCount()
                ?.entries?.sortedByDescending { it.value }
                ?.map { it.key }
                ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val results: StateFlow<UiState<List<Anime>>> =
        combine(_all, _query, _genre) { state, query, genre ->
            when (state) {
                is UiState.Success -> {
                    val q = query.trim()
                    val filtered = state.data.filter { anime ->
                        (q.isBlank() ||
                            anime.subject.contains(q, ignoreCase = true) ||
                            anime.originalSubject.contains(q, ignoreCase = true)) &&
                            (genre == null || anime.genres.contains(genre))
                    }
                    UiState.Success(filtered)
                }
                is UiState.Loading -> UiState.Loading
                is UiState.Error -> state
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val favoriteIds: StateFlow<Set<Int>> = repository.observeFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        load()
    }

    fun onQueryChange(value: String) { _query.value = value }

    fun onGenreSelect(genre: String?) {
        _genre.value = if (_genre.value == genre) null else genre
    }

    fun retry() = load()

    private fun load() {
        _all.value = UiState.Loading
        viewModelScope.launch {
            _all.value = try {
                UiState.Success(repository.allAnime())
            } catch (e: Exception) {
                UiState.Error("데이터를 불러오지 못했어요.")
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch { repository.toggleFavorite(anime) }
    }
}
