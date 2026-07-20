package com.kiwankim.kiwankim.myapplication3.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopDestination(val route: String, val label: String, val icon: ImageVector) {
    Schedule("schedule", "편성표", Icons.Filled.CalendarMonth),
    Search("search", "탐색", Icons.Filled.Search),
    Favorites("favorites", "찜", Icons.Filled.Favorite),
}

object Routes {
    const val DETAIL = "detail"
    fun detail(animeNo: Int) = "$DETAIL/$animeNo"
    const val DETAIL_PATTERN = "$DETAIL/{animeNo}"
    const val ARG_ANIME_NO = "animeNo"
}
