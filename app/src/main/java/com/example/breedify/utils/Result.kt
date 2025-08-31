package com.example.breedify.utils

/**
 * A generic wrapper for handling success and error states
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable, val message: String = exception.message ?: "Unknown error") : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

/**
 * Extension functions for easier result handling
 */
inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (Throwable, String) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(exception, message)
    return this
}

inline fun <T> ApiResult<T>.onLoading(action: () -> Unit): ApiResult<T> {
    if (this is ApiResult.Loading) action()
    return this
}