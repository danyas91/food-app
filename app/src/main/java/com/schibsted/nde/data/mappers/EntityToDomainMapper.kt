package com.schibsted.nde.data.mappers

import com.schibsted.nde.data.database.MealEntity
import com.schibsted.nde.domain.model.Meal

fun MealEntity.toMeal() =
    Meal(
        idMeal = idMeal,
        strMeal = strMeal,
        strCategory = strCategory,
        strMealThumb = strMealThumb,
        strYoutube = strYoutube,
        strInstructions = strInstructions
    )