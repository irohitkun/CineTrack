package com.cinetrack.app.data.api

import com.cinetrack.app.data.api.dto.TmdbGenreListDto
import com.cinetrack.app.data.api.dto.TmdbMovieDetailsDto
import com.cinetrack.app.data.api.dto.TmdbPagedResponseDto
import com.cinetrack.app.data.api.dto.TmdbTvDetailsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("trending/tv/day")
    suspend fun getTrendingTv(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("tv/popular")
    suspend fun getPopularTv(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(@Query("page") page: Int = 1): TmdbPagedResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") append: String = "credits"
    ): TmdbMovieDetailsDto

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("append_to_response") append: String = "credits"
    ): TmdbTvDetailsDto

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPagedResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPagedResponseDto

    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false
    ): TmdbPagedResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): TmdbGenreListDto

    @GET("genre/tv/list")
    suspend fun getTvGenres(): TmdbGenreListDto

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1
    ): TmdbPagedResponseDto

    @GET("tv/{tv_id}/recommendations")
    suspend fun getTvRecommendations(
        @Path("tv_id") tvId: Int,
        @Query("page") page: Int = 1
    ): TmdbPagedResponseDto
}
