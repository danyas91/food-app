package com.schibsted.nde.di

import com.schibsted.nde.domain.repository.MealsRepository
import com.schibsted.nde.domain.usecase.GetAllMealsUseCase
import com.schibsted.nde.domain.usecase.GetMealDetailsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideGetAllMealsUseCase(repository: MealsRepository): GetAllMealsUseCase {
        return GetAllMealsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMealByDetailsUseCase(repository: MealsRepository): GetMealDetailsUseCase {
        return GetMealDetailsUseCase(repository)
    }
}