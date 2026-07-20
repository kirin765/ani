package com.kiwankim.kiwankim.myapplication3.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kiwankim.kiwankim.myapplication3.ui.detail.DetailScreen
import com.kiwankim.kiwankim.myapplication3.ui.favorites.FavoritesScreen
import com.kiwankim.kiwankim.myapplication3.ui.schedule.ScheduleScreen
import com.kiwankim.kiwankim.myapplication3.ui.search.SearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AniApp(startAnimeNo: Int?) {
    val navController = rememberNavController()

    LaunchedEffect(startAnimeNo) {
        if (startAnimeNo != null && startAnimeNo > 0) {
            navController.navigate(Routes.detail(startAnimeNo))
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val topDestination = TopDestination.entries.firstOrNull { it.route == currentRoute }
    val showBars = topDestination != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = {
                        Text(
                            topDestination?.titleRes?.let { stringResource(it) } ?: "",
                            fontWeight = FontWeight.ExtraBold,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    val entry by navController.currentBackStackEntryAsState()
                    val dest = entry?.destination
                    TopDestination.entries.forEach { item ->
                        val selected = dest?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopDestination.Schedule.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(TopDestination.Schedule.route) {
                ScheduleScreen(
                    onAnimeClick = { navController.navigate(Routes.detail(it)) },
                    contentPadding = padding,
                )
            }
            composable(TopDestination.Search.route) {
                SearchScreen(
                    onAnimeClick = { navController.navigate(Routes.detail(it)) },
                    contentPadding = padding,
                )
            }
            composable(TopDestination.Favorites.route) {
                FavoritesScreen(
                    onAnimeClick = { navController.navigate(Routes.detail(it)) },
                    contentPadding = padding,
                )
            }
            composable(
                route = Routes.DETAIL_PATTERN,
                arguments = listOf(navArgument(Routes.ARG_ANIME_NO) { type = NavType.IntType }),
            ) { entry ->
                val animeNo = entry.arguments?.getInt(Routes.ARG_ANIME_NO) ?: return@composable
                DetailScreen(animeNo = animeNo, onBack = { navController.popBackStack() })
            }
        }
    }
}

