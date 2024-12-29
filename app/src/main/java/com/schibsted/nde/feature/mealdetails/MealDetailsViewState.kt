package com.schibsted.nde.feature.mealdetails

data class MealDetailsViewState(
    val isLoading: Boolean = false,
    val mealName: String = "",
    val mealImage: String = "",
    val mealInstructions: String = ""
)