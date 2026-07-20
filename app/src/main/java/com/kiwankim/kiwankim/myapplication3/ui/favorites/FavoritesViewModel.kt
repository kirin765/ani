package com.kiwankim.kiwankim.myapplication3.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwankim.kiwankim.myapplication3.data.AnimeRepository
import com.kiwankim.kiwankim.myapplication3.data.local.FavoriteAnime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: AnimeRepository) : ViewModel() {

    val favorites: StateFlow<List<FavoriteAnime>> = repository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setNotify(animeNo: Int, enabled: Boolean) {
        viewModelScope.launch { repository.setNotify(animeNo, enabled) }
    }

    fun remove(favorite: FavoriteAnime) {
        viewModelScope.launch { repository.removeFavorite(favorite.animeNo) }
    }
}
