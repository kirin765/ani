package com.kiwankim.kiwankim.myapplication3.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwankim.kiwankim.myapplication3.R
import com.kiwankim.kiwankim.myapplication3.data.AnimeRepository
import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.domain.Caption
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: AnimeRepository) : ViewModel() {

    private var animeNo: Int = -1

    private val _anime = MutableStateFlow<UiState<Anime>>(UiState.Loading)
    val anime: StateFlow<UiState<Anime>> = _anime.asStateFlow()

    private val _captions = MutableStateFlow<UiState<List<Caption>>>(UiState.Loading)
    val captions: StateFlow<UiState<List<Caption>>> = _captions.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.observeFavoriteIds()
        .map { animeNo in it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun load(no: Int) {
        if (no == animeNo && _anime.value is UiState.Success) return
        animeNo = no
        viewModelScope.launch {
            _anime.value = try {
                repository.findAnime(no)?.let { UiState.Success(it) }
                    ?: UiState.Error(R.string.error_anime_not_found)
            } catch (e: Exception) {
                UiState.Error(R.string.error_anime_load)
            }
        }
        viewModelScope.launch {
            _captions.value = try {
                UiState.Success(repository.captions(no))
            } catch (e: Exception) {
                UiState.Error(R.string.error_captions_load)
            }
        }
    }

    fun toggleFavorite() {
        val current = (_anime.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { repository.toggleFavorite(current) }
    }
}
