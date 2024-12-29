package com.schibsted.nde.domain.repository

import com.schibsted.nde.domain.model.Meal
import com.schibsted.nde.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface MealsRepository {
    suspend fun getAllMeals(): Flow<Response<List<Meal>>>
    suspend fun getMealById(id: String): Flow<Meal>
}