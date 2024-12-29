package com.schibsted.nde.domain.usecase

import com.schibsted.nde.domain.model.Meal
import com.schibsted.nde.domain.repository.MealsRepository
import com.schibsted.nde.domain.model.Response
import kotlinx.coroutines.flow.Flow

class GetAllMealsUseCase(
    private val repository: MealsRepository
) {

    suspend operator fun invoke() : Flow<Response<List<Meal>>> {
        return repository.getAllMeals()
    }
}