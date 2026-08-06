package com.cinetrack.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinetrack.app.data.repository.LibraryRepository
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class LibraryTab {
    WATCHING,
    COMPLETED,
    PLAN_TO_WATCH,
    FAVORITES
}

data class LibraryUiState(
    val watching: List<UserMedia> = emptyList(),
    val completed: List<UserMedia> = emptyList(),
    val planToWatch: List<UserMedia> = emptyList(),
    val favorites: List<UserMedia> = emptyList(),
    val selectedTab: LibraryTab = LibraryTab.WATCHING
) {
    fun itemsForTab(): List<UserMedia> = when (selectedTab) {
        LibraryTab.WATCHING -> watching
        LibraryTab.COMPLETED -> completed
        LibraryTab.PLAN_TO_WATCH -> planToWatch
        LibraryTab.FAVORITES -> favorites
    }
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    libraryRepository: LibraryRepository
) : ViewModel() {

    private val selectedTab = kotlinx.coroutines.flow.MutableStateFlow(LibraryTab.WATCHING)

    val uiState: StateFlow<LibraryUiState> = combine(
        libraryRepository.observeByStatus(WatchStatus.WATCHING),
        libraryRepository.observeByStatus(WatchStatus.COMPLETED),
        libraryRepository.observeByStatus(WatchStatus.PLAN_TO_WATCH),
        libraryRepository.observeFavorites(),
        selectedTab
    ) { watching, completed, plan, favorites, tab ->
        LibraryUiState(
            watching = watching,
            completed = completed,
            planToWatch = plan,
            favorites = favorites,
            selectedTab = tab
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState()
    )

    fun selectTab(tab: LibraryTab) {
        selectedTab.value = tab
    }
}
