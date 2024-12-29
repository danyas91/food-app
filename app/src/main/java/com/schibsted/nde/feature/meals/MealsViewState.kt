package com.schibsted.nde.feature.meals

import com.schibsted.nde.data.model.ErrorType
import com.schibsted.nde.domain.model.Meal

data class MealsViewState(
    val meals: List<Meal> = emptyList(),
    val filteredMeals: List<Meal> = emptyList(),
    val isLoading: Boolean = false,
    val query: String? = null,
    val shouldNavigateToMealDetails: Boolean = false,
    val clickedMealId: String? = null,
    val shouldShowError: Boolean = false,
    val errorType: ErrorType? = null
)