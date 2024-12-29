package com.schibsted.nde.domain.model

import com.schibsted.nde.data.model.ErrorType

sealed class Response<T>(val data: T? = null, val error: ErrorType? = null) {
    class Loading<T>(data: T? = null): Response<T>(data)
    class Success<T>(data: T?): Response<T>(data)
    class Error<T>(error: ErrorType, data: T? = null): Response<T>(data, error)
}