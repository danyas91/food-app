package com.schibsted.nde.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealEntityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeals(meals: List<MealEntity>)

    @Query("SELECT * FROM meal ORDER BY id")
    fun getAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal WHERE idMeal = :mealId")
    fun getMealById(mealId: String): Flow<MealEntity>

    @Query("DELETE FROM meal")
    fun deleteAllMeals()
}
