package com.kiwankim.kiwankim.myapplication3.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kiwankim.kiwankim.myapplication3.AniApplication
import com.kiwankim.kiwankim.myapplication3.ui.detail.DetailViewModel
import com.kiwankim.kiwankim.myapplication3.ui.favorites.FavoritesViewModel
import com.kiwankim.kiwankim.myapplication3.ui.schedule.ScheduleViewModel
import com.kiwankim.kiwankim.myapplication3.ui.search.SearchViewModel

fun CreationExtras.app(): AniApplication = this[APPLICATION_KEY] as AniApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { ScheduleViewModel(app().container.repository) }
        initializer { FavoritesViewModel(app().container.repository) }
        initializer { SearchViewModel(app().container.repository) }
        initializer { DetailViewModel(app().container.repository) }
    }
}
