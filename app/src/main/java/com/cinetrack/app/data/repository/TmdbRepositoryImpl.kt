package com.cinetrack.app.data.repository

import com.cinetrack.app.data.api.TmdbApi
import com.cinetrack.app.data.api.mapper.toDomain
import com.cinetrack.app.data.api.mapper.toDomainList
import com.cinetrack.app.domain.model.Genre
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.MediaItem
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.utils.Result
import com.cinetrack.app.utils.runCatchingResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbRepositoryImpl @Inject constructor(
    private val api: TmdbApi
) : TmdbRepository {

    override suspend fun getTrendingMovies(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getTrendingMovies(page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun getTrendingTv(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getTrendingTv(page).results.toDomainList(MediaType.TV) }

    override suspend fun getNowPlayingMovies(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getNowPlayingMovies(page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun getUpcomingMovies(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getUpcomingMovies(page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun getTopRatedMovies(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getTopRatedMovies(page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun getPopularMovies(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getPopularMovies(page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun getPopularTv(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getPopularTv(page).results.toDomainList(MediaType.TV) }

    override suspend fun getTopRatedTv(page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.getTopRatedTv(page).results.toDomainList(MediaType.TV) }

    override suspend fun getMovieDetails(id: Int): Result<MediaDetails> =
        runCatchingResult { api.getMovieDetails(id).toDomain() }

    override suspend fun getTvDetails(id: Int): Result<MediaDetails> =
        runCatchingResult { api.getTvDetails(id).toDomain() }

    override suspend fun getDetails(mediaType: MediaType, id: Int): Result<MediaDetails> =
        when (mediaType) {
            MediaType.MOVIE -> getMovieDetails(id)
            MediaType.TV -> getTvDetails(id)
        }

    override suspend fun searchMulti(query: String, page: Int): Result<List<MediaItem>> =
        runCatchingResult {
            api.searchMulti(query, page).results
                .toDomainList()
                .filter { it.mediaType == MediaType.MOVIE || it.mediaType == MediaType.TV }
        }

    override suspend fun searchMovies(query: String, page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.searchMovies(query, page).results.toDomainList(MediaType.MOVIE) }

    override suspend fun searchTv(query: String, page: Int): Result<List<MediaItem>> =
        runCatchingResult { api.searchTv(query, page).results.toDomainList(MediaType.TV) }

    override suspend fun getMovieGenres(): Result<List<Genre>> =
        runCatchingResult { api.getMovieGenres().genres.map { it.toDomain() } }

    override suspend fun getTvGenres(): Result<List<Genre>> =
        runCatchingResult { api.getTvGenres().genres.map { it.toDomain() } }

    override suspend fun getRecommendations(
        mediaType: MediaType,
        id: Int,
        page: Int
    ): Result<List<MediaItem>> = runCatchingResult {
        when (mediaType) {
            MediaType.MOVIE -> api.getMovieRecommendations(id, page).results.toDomainList(MediaType.MOVIE)
            MediaType.TV -> api.getTvRecommendations(id, page).results.toDomainList(MediaType.TV)
        }
    }
}
