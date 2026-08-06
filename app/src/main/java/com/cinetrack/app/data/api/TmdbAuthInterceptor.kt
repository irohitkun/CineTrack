package com.cinetrack.app.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Appends the TMDB API key to every request. Key comes from BuildConfig / local.properties.
 */
class TmdbAuthInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()
        return chain.proceed(original.newBuilder().url(url).build())
    }
}
