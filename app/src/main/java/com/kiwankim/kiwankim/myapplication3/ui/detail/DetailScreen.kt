package com.kiwankim.kiwankim.myapplication3.ui.detail

import android.content.Intent
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiwankim.kiwankim.myapplication3.R
import com.kiwankim.kiwankim.myapplication3.domain.Anime
import com.kiwankim.kiwankim.myapplication3.domain.Caption
import com.kiwankim.kiwankim.myapplication3.ui.AppViewModelProvider
import com.kiwankim.kiwankim.myapplication3.ui.UiState
import com.kiwankim.kiwankim.myapplication3.ui.components.ErrorState
import com.kiwankim.kiwankim.myapplication3.ui.components.GenreChip
import com.kiwankim.kiwankim.myapplication3.ui.components.LoadingState
import com.kiwankim.kiwankim.myapplication3.ui.theme.NeonPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animeNo: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LaunchedEffect(animeNo) { viewModel.load(animeNo) }

    val animeState by viewModel.anime.collectAsStateWithLifecycle()
    val captionsState by viewModel.captions.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    if (animeState is UiState.Success) {
                        IconButton(onClick = viewModel::toggleFavorite) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(R.string.cd_favorite),
                                tint = if (isFavorite) NeonPink else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        when (val s = animeState) {
            is UiState.Loading -> LoadingState(Modifier.padding(padding))
            is UiState.Error -> ErrorState(s.messageRes, onRetry = { viewModel.load(animeNo) }, Modifier.padding(padding))
            is UiState.Success -> DetailContent(
                anime = s.data,
                captionsState = captionsState,
                modifier = Modifier.padding(padding).fillMaxSize(),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(anime: Anime, captionsState: UiState<List<Caption>>, modifier: Modifier) {
    val context = LocalContext.current
    fun open(url: String) {
        if (url.isBlank()) return
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                anime.subject,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            if (anime.originalSubject.isNotBlank() && anime.originalSubject != anime.subject) {
                Text(
                    anime.originalSubject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow(
                        stringResource(R.string.detail_label_airing),
                        "${stringResource(anime.week.labelRes)} ${anime.time}".trim(),
                    )
                    InfoRow(
                        stringResource(R.string.detail_label_status),
                        stringResource(if (anime.airing) R.string.status_airing else R.string.status_break),
                    )
                    if (anime.startDate.isNotBlank()) {
                        InfoRow(stringResource(R.string.detail_label_period), listOf(anime.startDate, anime.endDate).filter { it.isNotBlank() }.joinToString(" ~ "))
                    }
                    if (anime.genres.isNotEmpty()) {
                        Text(stringResource(R.string.detail_label_genre), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            anime.genres.forEach { GenreChip(it) }
                        }
                    }
                    if (anime.website.isNotBlank()) {
                        LinkRow(stringResource(R.string.detail_official_site)) { open(anime.website) }
                    }
                }
            }
        }

        item {
            Text(
                stringResource(R.string.detail_captions_section),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        when (val cs = captionsState) {
            is UiState.Loading -> item { Spacer(Modifier.height(8.dp)); LoadingState(Modifier.height(80.dp)) }
            is UiState.Error -> item { Text(stringResource(cs.messageRes), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            is UiState.Success -> {
                if (cs.data.isEmpty()) {
                    item { Text(stringResource(R.string.detail_captions_empty), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(cs.data) { caption -> CaptionRow(caption, onOpen = { open(caption.website) }) }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(56.dp),
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(6.dp))
        Icon(
            Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(16.dp),
        )
    }
}

@Composable
private fun CaptionRow(caption: Caption, onOpen: () -> Unit) {
    val hasLink = caption.website.isNotBlank()
    Card(
        onClick = onOpen,
        enabled = hasLink,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (caption.episode == "0") stringResource(R.string.detail_episode_special)
                    else stringResource(R.string.detail_episode_number, caption.episode),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "${caption.name} · ${caption.updatedAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (hasLink) {
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = stringResource(R.string.cd_open_caption),
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(stringResource(R.string.detail_caption_pending), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
