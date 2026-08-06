package com.cinetrack.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cinetrack.app.ui.screens.details.DetailsScreen
import com.cinetrack.app.ui.screens.home.HomeScreen
import com.cinetrack.app.ui.screens.library.LibraryScreen
import com.cinetrack.app.ui.screens.profile.ProfileScreen
import com.cinetrack.app.ui.screens.search.SearchScreen

@Composable
fun CineTrackApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavRoutes = BottomNavItem.entries.map { it.route }
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    BottomNavItem.entries.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onMediaClick = { item ->
                        navController.navigate(
                            Routes.details(item.mediaType.apiValue, item.id)
                        )
                    }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onMediaClick = { item ->
                        navController.navigate(
                            Routes.details(item.mediaType.apiValue, item.id)
                        )
                    }
                )
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(
                    onMediaClick = { item ->
                        navController.navigate(
                            Routes.details(item.mediaType.apiValue, item.tmdbId)
                        )
                    }
                )
            }
            composable(Routes.PROFILE) { ProfileScreen() }
            composable(
                route = Routes.DETAILS,
                arguments = listOf(
                    navArgument("mediaType") { type = NavType.StringType },
                    navArgument("mediaId") { type = NavType.IntType }
                )
            ) { entry ->
                DetailsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
