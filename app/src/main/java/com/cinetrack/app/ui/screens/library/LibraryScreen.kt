package com.cinetrack.app.ui.screens.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.ui.components.EmptyState
import com.cinetrack.app.ui.components.LibraryMediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onMediaClick: (UserMedia) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = LibraryTab.entries
    val selectedIndex = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tabs.forEach { tab ->
                    val count = when (tab) {
                        LibraryTab.WATCHING -> uiState.watching.size
                        LibraryTab.COMPLETED -> uiState.completed.size
                        LibraryTab.PLAN_TO_WATCH -> uiState.planToWatch.size
                        LibraryTab.FAVORITES -> uiState.favorites.size
                    }
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            Text(
                                when (tab) {
                                    LibraryTab.WATCHING -> "Watching ($count)"
                                    LibraryTab.COMPLETED -> "Completed ($count)"
                                    LibraryTab.PLAN_TO_WATCH -> "Plan ($count)"
                                    LibraryTab.FAVORITES -> "Favorites ($count)"
                                }
                            )
                        }
                    )
                }
            }

            val items = uiState.itemsForTab()
            if (items.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.VideoLibrary,
                    title = emptyTitle(uiState.selectedTab),
                    message = "Add titles from Details — Watching, Completed, Plan to Watch, or Favorite."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = items,
                        key = { "${it.mediaType.apiValue}-${it.tmdbId}" }
                    ) { item ->
                        LibraryMediaCard(
                            item = item,
                            onClick = { onMediaClick(item) }
                        )
                    }
                }
            }
        }
    }
}

private fun emptyTitle(tab: LibraryTab): String = when (tab) {
    LibraryTab.WATCHING -> "Nothing Watching"
    LibraryTab.COMPLETED -> "No Completed Titles"
    LibraryTab.PLAN_TO_WATCH -> "Empty Watchlist"
    LibraryTab.FAVORITES -> "No Favorites Yet"
}
