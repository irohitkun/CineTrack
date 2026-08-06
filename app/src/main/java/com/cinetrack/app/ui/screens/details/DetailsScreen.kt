package com.cinetrack.app.ui.screens.details

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cinetrack.app.domain.model.CastMember
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import com.cinetrack.app.ui.components.ErrorState
import com.cinetrack.app.ui.components.LoadingState
import com.cinetrack.app.ui.components.PosterImage
import com.cinetrack.app.ui.components.RatingBadge
import com.cinetrack.app.ui.components.TvProgressSection
import com.cinetrack.app.utils.ImageUrlBuilder
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.details?.title.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.shareText()?.let { text ->
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share"))
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            uiState.isLoading && uiState.details == null -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            uiState.errorMessage != null && uiState.details == null -> {
                ErrorState(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::loadDetails,
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.details != null -> {
                DetailsContent(
                    details = uiState.details!!,
                    userMedia = uiState.userMedia,
                    onStatus = viewModel::setStatus,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onSeasonDelta = viewModel::adjustSeason,
                    onEpisodeDelta = viewModel::adjustEpisode,
                    onMarkComplete = viewModel::markCompleted,
                    onRatingChange = viewModel::setPersonalRating,
                    onNotesChange = viewModel::setNotes,
                    onReviewChange = viewModel::setReview,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun DetailsContent(
    details: MediaDetails,
    userMedia: UserMedia?,
    onStatus: (WatchStatus) -> Unit,
    onToggleFavorite: () -> Unit,
    onSeasonDelta: (Int) -> Unit,
    onEpisodeDelta: (Int) -> Unit,
    onMarkComplete: () -> Unit,
    onRatingChange: (Float?) -> Unit,
    onNotesChange: (String) -> Unit,
    onReviewChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroHeader(details = details)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = details.title,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        details.tagline?.let { tagline ->
            Text(
                text = tagline,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        MetaRow(details = details)

        GenreChips(genres = details.genres.map { it.name })

        ActionRow(
            userMedia = userMedia,
            onStatus = onStatus,
            onToggleFavorite = onToggleFavorite
        )

        SectionTitle("Overview")
        Text(
            text = details.overview.ifBlank { "No overview available." },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (details.cast.isNotEmpty()) {
            SectionTitle("Cast")
            CastRow(cast = details.cast.take(12))
        }

        if (details.productionCompanies.isNotEmpty()) {
            SectionTitle("Production")
            Text(
                text = details.productionCompanies.joinToString { it.name },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (details.mediaType == MediaType.TV && details.seasons.isNotEmpty()) {
            SectionTitle("Seasons")
            Text(
                text = details.seasons.joinToString("\n") {
                    "S${it.seasonNumber}: ${it.name} (${it.episodeCount} eps)"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (details.mediaType == MediaType.TV && userMedia != null) {
            TvProgressSection(
                details = details,
                userMedia = userMedia,
                onSeasonDelta = onSeasonDelta,
                onEpisodeDelta = onEpisodeDelta,
                onMarkComplete = onMarkComplete
            )
        }

        PersonalTrackingSection(
            userMedia = userMedia,
            onRatingChange = onRatingChange,
            onNotesChange = onNotesChange,
            onReviewChange = onReviewChange
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HeroHeader(details: MediaDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        val backdrop = ImageUrlBuilder.build(
            details.backdropPath ?: details.posterPath,
            ImageUrlBuilder.Size.BACKDROP_W780
        )
        if (backdrop != null) {
            AsyncImage(
                model = backdrop,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
        )
        PosterImage(
            posterPath = details.posterPath,
            contentDescription = details.title,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 8.dp)
                .width(110.dp)
                .aspectRatio(2f / 3f),
            size = ImageUrlBuilder.Size.POSTER_W342
        )
        if (details.voteAverage > 0) {
            RatingBadge(
                rating = details.voteAverage,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun MetaRow(details: MediaDetails) {
    val parts = buildList {
        details.year?.let { add(it) }
        details.runtimeMinutes?.let { add("${it}m") }
        details.numberOfSeasons?.let { add("$it seasons") }
        details.numberOfEpisodes?.let { add("$it episodes") }
        add(String.format(Locale.US, "Popularity %.0f", details.popularity))
    }
    Text(
        text = parts.joinToString(" · "),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun GenreChips(genres: List<String>) {
    if (genres.isEmpty()) return
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            AssistChip(onClick = {}, label = { Text(genre) })
        }
    }
}

@Composable
private fun ActionRow(
    userMedia: UserMedia?,
    onStatus: (WatchStatus) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val current = userMedia?.status
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = current == WatchStatus.WATCHING,
            onClick = { onStatus(WatchStatus.WATCHING) },
            label = { Text("Watching") },
            leadingIcon = { Icon(Icons.Default.PlayCircle, null, Modifier.size(18.dp)) }
        )
        FilterChip(
            selected = current == WatchStatus.COMPLETED,
            onClick = { onStatus(WatchStatus.COMPLETED) },
            label = { Text("Completed") },
            leadingIcon = { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)) }
        )
        FilterChip(
            selected = current == WatchStatus.PLAN_TO_WATCH,
            onClick = { onStatus(WatchStatus.PLAN_TO_WATCH) },
            label = { Text("Plan to Watch") },
            leadingIcon = {
                Icon(
                    if (current == WatchStatus.PLAN_TO_WATCH) Icons.Default.Bookmark
                    else Icons.Default.BookmarkBorder,
                    null,
                    Modifier.size(18.dp)
                )
            }
        )
        FilterChip(
            selected = userMedia?.isFavorite == true,
            onClick = onToggleFavorite,
            label = { Text("Favorite") },
            leadingIcon = {
                Icon(
                    if (userMedia?.isFavorite == true) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    null,
                    Modifier.size(18.dp)
                )
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun CastRow(cast: List<CastMember>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cast, key = { it.id }) { member ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(88.dp)
            ) {
                val profile = ImageUrlBuilder.build(
                    member.profilePath,
                    ImageUrlBuilder.Size.PROFILE_W185
                )
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile != null) {
                        AsyncImage(
                            model = profile,
                            contentDescription = member.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = member.character,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PersonalTrackingSection(
    userMedia: UserMedia?,
    onRatingChange: (Float?) -> Unit,
    onNotesChange: (String) -> Unit,
    onReviewChange: (String) -> Unit
) {
    var rating by remember(userMedia?.personalRating) {
        mutableFloatStateOf(userMedia?.personalRating ?: 0f)
    }
    var notes by remember(userMedia?.notes) { mutableStateOf(userMedia?.notes.orEmpty()) }
    var review by remember(userMedia?.review) { mutableStateOf(userMedia?.review.orEmpty()) }

    SectionTitle("Your Tracking")
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = if (rating > 0f) {
                "Personal rating: ${String.format(Locale.US, "%.1f", rating)} / 10"
            } else {
                "Personal rating: not set"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = rating,
            onValueChange = { rating = it },
            onValueChangeFinished = {
                onRatingChange(if (rating <= 0f) null else rating)
            },
            valueRange = 0f..10f,
            steps = 19
        )
        OutlinedTextField(
            value = notes,
            onValueChange = {
                notes = it
                onNotesChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Private notes") },
            minLines = 2
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = review,
            onValueChange = {
                review = it
                onReviewChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Personal review") },
            minLines = 3
        )
        if (userMedia == null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saving rating, notes, or review adds this title to your library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
