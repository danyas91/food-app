package com.schibsted.nde.di

import android.content.Context
import androidx.room.Room
import com.schibsted.nde.data.api.BackendApi
import com.schibsted.nde.data.repository.MealsRepositoryImpl
import com.schibsted.nde.data.database.AppDatabase
import com.schibsted.nde.data.database.MealEntityDao
import com.schibsted.nde.domain.repository.MealsRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .build()

    @Singleton
    @Provides
    fun provideBackendApi(moshi: Moshi): BackendApi = Retrofit.Builder()
        .baseUrl("https://www.themealdb.com")
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(BackendApi::class.java)

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "android-job-assignment")
            .build()

    @Singleton
    @Provides
    fun provideMealEntityDao(appDatabase: AppDatabase): MealEntityDao = appDatabase.mealDao()

    @Provides
    @Singleton
    fun provideMealsRepository(
        api: BackendApi,
        dao: MealEntityDao
    ): MealsRepository {
        return MealsRepositoryImpl(api, dao)
    }
}