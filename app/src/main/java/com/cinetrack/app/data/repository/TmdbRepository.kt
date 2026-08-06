package com.cinetrack.app.data.repository

import com.cinetrack.app.domain.model.Genre
import com.cinetrack.app.domain.model.MediaDetails
import com.cinetrack.app.domain.model.MediaItem
import com.cinetrack.app.domain.model.MediaType
import com.cinetrack.app.utils.Result

interface TmdbRepository {
    suspend fun getTrendingMovies(page: Int = 1): Result<List<MediaItem>>
    suspend fun getTrendingTv(page: Int = 1): Result<List<MediaItem>>
    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<MediaItem>>
    suspend fun getUpcomingMovies(page: Int = 1): Result<List<MediaItem>>
    suspend fun getTopRatedMovies(page: Int = 1): Result<List<MediaItem>>
    suspend fun getPopularMovies(page: Int = 1): Result<List<MediaItem>>
    suspend fun getPopularTv(page: Int = 1): Result<List<MediaItem>>
    suspend fun getTopRatedTv(page: Int = 1): Result<List<MediaItem>>
    suspend fun getMovieDetails(id: Int): Result<MediaDetails>
    suspend fun getTvDetails(id: Int): Result<MediaDetails>
    suspend fun getDetails(mediaType: MediaType, id: Int): Result<MediaDetails>
    suspend fun searchMulti(query: String, page: Int = 1): Result<List<MediaItem>>
    suspend fun searchMovies(query: String, page: Int = 1): Result<List<MediaItem>>
    suspend fun searchTv(query: String, page: Int = 1): Result<List<MediaItem>>
    suspend fun getMovieGenres(): Result<List<Genre>>
    suspend fun getTvGenres(): Result<List<Genre>>
    suspend fun getRecommendations(mediaType: MediaType, id: Int, page: Int = 1): Result<List<MediaItem>>
}
