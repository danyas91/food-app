package com.schibsted.nde.feature.meals

sealed class MealsViewEvent {
    data class OnMealItemClicked(val mealId: String): MealsViewEvent()
    data class OnSubmitQuery(val query: String?): MealsViewEvent()
    data object LoadMeals : MealsViewEvent()
    data object OnNavigateToMealDetails : MealsViewEvent()
    data object ErrorSnackBarDismissed : MealsViewEvent()
}