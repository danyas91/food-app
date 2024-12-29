package com.schibsted.nde.data.model

sealed class ErrorType {
    data object NetworkError: ErrorType()
    data object UnknownError: ErrorType()
    data object ServerError: ErrorType()
}