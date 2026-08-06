package com.cinetrack.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.UserMedia

@Composable
fun TvProgressSection(
    details: MediaDetails,
    userMedia: UserMedia,
    onSeasonDelta: (Int) -> Unit,
    onEpisodeDelta: (Int) -> Unit,
    onMarkComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val seasonInfo = details.seasons.firstOrNull { it.seasonNumber == userMedia.currentSeason }
    val episodeCount = seasonInfo?.episodeCount?.takeIf { it > 0 } ?: 1
    val progress = (userMedia.currentEpisode.toFloat() / episodeCount.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "TV Progress",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        ProgressStepper(
            label = "Season",
            value = userMedia.currentSeason,
            onMinus = { onSeasonDelta(-1) },
            onPlus = { onSeasonDelta(1) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ProgressStepper(
            label = "Episode",
            value = userMedia.currentEpisode,
            onMinus = { onEpisodeDelta(-1) },
            onPlus = { onEpisodeDelta(1) }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Episode ${userMedia.currentEpisode} of $episodeCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onMarkComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mark Complete")
        }
    }
}

@Composable
private fun ProgressStepper(
    label: String,
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onMinus) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease $label")
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(36.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedButton(onClick = onPlus) {
                Icon(Icons.Default.Add, contentDescription = "Increase $label")
            }
        }
    }
}
