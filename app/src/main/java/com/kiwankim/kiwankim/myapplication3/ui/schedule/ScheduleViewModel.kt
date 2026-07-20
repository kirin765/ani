package com.kiwankim.kiwankim.myapplication3.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwankim.kiwankim.myapplication3.data.AnimeRepository
import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.domain.Weekday
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _selectedWeek = MutableStateFlow(Weekday.today())
    val selectedWeek: StateFlow<Weekday> = _selectedWeek.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<Anime>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Anime>>> = _uiState.asStateFlow()

    val favoriteIds: StateFlow<Set<Int>> = repository.observeFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        load(_selectedWeek.value)
    }

    fun selectWeek(week: Weekday) {
        if (week == _selectedWeek.value) return
        _selectedWeek.value = week
        load(week)
    }

    fun refresh() = load(_selectedWeek.value, forceRefresh = true)

    private fun load(week: Weekday, forceRefresh: Boolean = false) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                UiState.Success(repository.schedule(week, forceRefresh))
            } catch (e: Exception) {
                UiState.Error("편성표를 불러오지 못했어요. 네트워크를 확인해 주세요.")
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch { repository.toggleFavorite(anime) }
    }
}
