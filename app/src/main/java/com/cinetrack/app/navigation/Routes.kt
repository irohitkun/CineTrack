package com.cinetrack.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Home("home", "Home", Icons.Default.Home),
    Search("search", "Search", Icons.Default.Search),
    Library("library", "Library", Icons.Default.VideoLibrary),
    Profile("profile", "Profile", Icons.Default.Person)
}

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val LIBRARY = "library"
    const val PROFILE = "profile"
    const val DETAILS = "details/{mediaType}/{mediaId}"

    fun details(mediaType: String, mediaId: Int): String = "details/$mediaType/$mediaId"
}
