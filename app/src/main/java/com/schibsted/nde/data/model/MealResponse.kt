package com.schibsted.nde.data.model

data class MealResponse(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strMealThumb: String,
    val strYoutube: String?,
    val strInstructions: String
)