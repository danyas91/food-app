package com.schibsted.nde.data.mappers

import com.schibsted.nde.data.database.MealEntity
import com.schibsted.nde.data.model.MealResponse

fun MealResponse.toMealEntity() =
    MealEntity(
        idMeal = idMeal,
        strMeal = strMeal,
        strCategory = strCategory,
        strMealThumb = strMealThumb,
        strYoutube = strYoutube,
        strInstructions = strInstructions
    )