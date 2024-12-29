package com.schibsted.nde.domain.usecase

import com.schibsted.nde.domain.model.Meal
import com.schibsted.nde.domain.model.Response
import com.schibsted.nde.domain.repository.MealsRepository
import kotlinx.coroutines.flow.Flow

class GetMealDetailsUseCase(
    private val repository: MealsRepository
) {

    suspend operator fun invoke(mealId: String) : Flow<Response<Meal>> {
        return repository.getMealById(mealId)
    }
}