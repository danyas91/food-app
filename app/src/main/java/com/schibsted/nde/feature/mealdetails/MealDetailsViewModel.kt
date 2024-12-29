package com.schibsted.nde.feature.mealdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schibsted.nde.domain.model.Response
import com.schibsted.nde.domain.usecase.GetMealDetailsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MealDetailsViewModel.MealDetailsViewModelFactory::class)
class MealDetailsViewModel @AssistedInject constructor(
    @Assisted private val mealId: String,
    private val getMealDetailsUseCase: GetMealDetailsUseCase
) : ViewModel() {

    @AssistedFactory
    interface MealDetailsViewModelFactory {
        fun create(mealId: String): MealDetailsViewModel
    }

    private val _state = MutableStateFlow(MealDetailsViewState(isLoading = true))

    val state: StateFlow<MealDetailsViewState>
        get() = _state

    init {
        getMealDetails()
    }

    private fun getMealDetails() {
        viewModelScope.launch {
            getMealDetailsUseCase.invoke(mealId)
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
                                    mealName = response.data?.strMeal ?: "",
                                    mealImage = response.data?.strMealThumb ?: "",
                                    mealInstructions = response.data?.strInstructions ?: "",
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
}