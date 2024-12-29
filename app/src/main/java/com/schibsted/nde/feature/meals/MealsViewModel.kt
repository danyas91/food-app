package com.schibsted.nde.feature.meals

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schibsted.nde.domain.usecase.GetAllMealsUseCase
import com.schibsted.nde.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class MealsViewModel @Inject constructor(
    private val getAllMealsUseCase: GetAllMealsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MealsViewState(isLoading = true))

    val state: StateFlow<MealsViewState>
        get() = _state

    init {
        loadMeals()
    }

    fun onEvent(event: MealsViewEvent) {
        when (event) {
            is MealsViewEvent.OnMealItemClicked -> {
                viewModelScope.launch {
                    _state.emit(_state.value.copy(shouldNavigateToMealDetails = true, clickedMealId = event.mealId))
                }
            }

            MealsViewEvent.LoadMeals -> {
                loadMeals()
            }
            MealsViewEvent.OnNavigateToMealDetails -> {
                viewModelScope.launch {
                    _state.emit(_state.value.copy(shouldNavigateToMealDetails = false, clickedMealId = null))
                }
            }

            is MealsViewEvent.OnSubmitQuery -> {
                submitQuery(event.query)
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun loadMeals() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllMealsUseCase.invoke()
                .onStart {
                    _state.emit(_state.value.copy(isLoading = true))
                }
                .catch { error ->
                    _state.emit(_state.value.copy(isLoading = false))
                }
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            _state.emit(_state.value.copy(isLoading = true))
                        }

                        is Response.Success -> {
                            _state.emit(
                                _state.value.copy(
                                    meals = response.data ?: emptyList(),
                                    filteredMeals = response.data ?: emptyList(),
                                    isLoading = false
                                )
                            )
                        }

                        is Response.Error -> {
                            _state.emit(_state.value.copy(isLoading = false))
                        }
                    }
                }
        }
    }

    private fun submitQuery(query: String?) {
        viewModelScope.launch {
            val filteredMeals = if (query?.isBlank() == true) {
                _state.value.meals
            } else {
                _state.value.meals.filter {
                    it.strMeal.lowercase().contains(query?.lowercase() ?: "")
                }
            }
            _state.emit(_state.value.copy(query = query, filteredMeals = filteredMeals))
        }
    }
}