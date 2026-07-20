package com.kiwankim.kiwankim.myapplication3.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.kiwankim.kiwankim.myapplication3.R

enum class TopDestination(
    val route: String,
    @StringRes val labelRes: Int,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
) {
    Schedule("schedule", R.string.nav_schedule, R.string.title_schedule, Icons.Filled.CalendarMonth),
    Search("search", R.string.nav_search, R.string.title_search, Icons.Filled.Search),
    Favorites("favorites", R.string.nav_favorites, R.string.title_favorites, Icons.Filled.Favorite),
}

object Routes {
    const val DETAIL = "detail"
    fun detail(animeNo: Int) = "$DETAIL/$animeNo"
    const val DETAIL_PATTERN = "$DETAIL/{animeNo}"
    const val ARG_ANIME_NO = "animeNo"
}
