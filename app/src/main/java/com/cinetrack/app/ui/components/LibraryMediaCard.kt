package com.cinetrack.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.domain.model.UserMedia
import com.cinetrack.app.domain.model.WatchStatus
import java.util.Locale

@Composable
fun LibraryMediaCard(
    item: UserMedia,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterImage(
                posterPath = item.posterPath,
                contentDescription = item.title,
                modifier = Modifier
                    .width(72.dp)
                    .aspectRatio(2f / 3f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaTypeChip(mediaType = item.mediaType)
                    Text(
                        text = statusLabel(item.status),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item.personalRating?.let { rating ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your rating: ${String.format(Locale.US, "%.1f", rating)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (item.mediaType == MediaType.TV && item.status == WatchStatus.WATCHING) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "S${item.currentSeason} · E${item.currentEpisode}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val total = item.totalEpisodes?.takeIf { it > 0 }
                    if (total != null) {
                        val approx = ((item.currentSeason - 1) * (total / (item.totalSeasons ?: 1).coerceAtLeast(1)) +
                            item.currentEpisode).toFloat()
                        val progress = (approx / total.toFloat()).coerceIn(0f, 1f)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )
                    }
                }
                if (item.isFavorite) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "★ Favorite",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun statusLabel(status: WatchStatus): String = when (status) {
    WatchStatus.WATCHING -> "Watching"
    WatchStatus.COMPLETED -> "Completed"
    WatchStatus.PLAN_TO_WATCH -> "Plan to Watch"
}
