package com.schibsted.nde

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schibsted.nde.feature.mealdetails.MealDetailsScreen
import com.schibsted.nde.feature.mealdetails.MealDetailsViewModel
import com.schibsted.nde.feature.meals.MealsScreen
import com.schibsted.nde.feature.meals.MealsViewModel
import com.schibsted.nde.ui.AppTheme
import com.schibsted.nde.ui.LocalSnackbarHostState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            AppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    NavGraph(navController)
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@ExperimentalFoundationApi
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "meals") {
        composable("meals") {
            val viewModel = hiltViewModel<MealsViewModel>()
            val state = viewModel.state.collectAsState()
            MealsScreen(
                state = state.value,
                onEvent = viewModel::onEvent,
                onNavigateToMealDetails = { mealId ->
                    navController.navigate("mealDetails?mealId=$mealId")
                }
            )
        }
        composable("mealDetails?mealId={mealId}") {
            val mealId = navController.currentBackStackEntry?.arguments?.getString(
                "mealId"
            ) ?: ""
            val viewModel = hiltViewModel(
                creationCallback = { factory: MealDetailsViewModel.MealDetailsViewModelFactory ->
                    factory.create(
                        mealId = mealId
                    )
                }
            )
            val state = viewModel.state.collectAsState()
            MealDetailsScreen(
                state = state.value,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}