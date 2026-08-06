package com.cinetrack.app.utils

/**
 * Simple success/error wrapper used by repositories and ViewModels.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    fun getOrNull(): T? = (this as? Success)?.data
}

inline fun <T> runCatchingResult(block: () -> T): Result<T> =
    try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown error", e)
    }
