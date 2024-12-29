package com.schibsted.nde.ui

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarHostState = compositionLocalOf {
    SnackbarHostState()
}