package com.schibsted.nde.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.schibsted.nde.data.api.BackendApi
import com.schibsted.nde.data.database.MealEntityDao
import com.schibsted.nde.data.mappers.toMeal
import com.schibsted.nde.data.mappers.toMealEntity
import com.schibsted.nde.data.model.ErrorType
import com.schibsted.nde.domain.model.Meal
import com.schibsted.nde.domain.model.Response
import com.schibsted.nde.domain.repository.MealsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MealsRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val dao: MealEntityDao
) : MealsRepository {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getAllMeals(): Flow<Response<List<Meal>>> = flow {
        emit(Response.Loading())

        val meals = dao.getAll().first().map { it.toMeal() }
        emit(Response.Loading(data = meals))

        try {
            val newMeals = backendApi.getMeals().meals.map { it.toMealEntity() }
            dao.deleteAllMeals()
            dao.insertMeals(newMeals)
        } catch (e: HttpException) {
            emit(
                Response.Error(
                    error = ErrorType.NetworkError,
                    data = meals
                )
            )
        } catch (e: IOException) {
            emit(
                Response.Error(
                    error = ErrorType.ServerError,
                    data = meals
                )
            )
        }

        val updatedMeals = dao.getAll().first().map { it.toMeal() }
        emit(Response.Success(updatedMeals))
    }

    override suspend fun getMealById(id: String): Flow<Meal> {
        TODO("Not yet implemented")
    }
}