package com.schibsted.nde.feature.meals

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.schibsted.nde.R
import com.schibsted.nde.data.model.ErrorType
import com.schibsted.nde.domain.model.Meal
import com.schibsted.nde.feature.common.MealImage
import com.schibsted.nde.ui.LocalSnackbarHostState
import com.schibsted.nde.ui.typography
import kotlinx.coroutines.launch

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@Composable
fun MealsScreen(
    state: MealsViewState,
    onNavigateToMealDetails: (String) -> Unit,
    onEvent: (MealsViewEvent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val networkErrorTypeMessage = stringResource(id = R.string.error_message)

    LaunchedEffect(state) {
        if (state.shouldNavigateToMealDetails && state.clickedMealId != null) {
            onNavigateToMealDetails(state.clickedMealId)
            onEvent(MealsViewEvent.OnNavigateToMealDetails)
        }
    }

    val snackbarHostState = LocalSnackbarHostState.current
    LaunchedEffect(state.shouldShowError) {
        if (state.shouldShowError) {
            coroutineScope.launch {
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = networkErrorTypeMessage,
                    duration = SnackbarDuration.Short
                )
                if (snackbarResult == SnackbarResult.Dismissed) {
                    onEvent(MealsViewEvent.ErrorSnackBarDismissed)
                }
            }
        }
    }

    ModalBottomSheetLayout(sheetContent = {
        val focusManager = LocalFocusManager.current
        if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
            DisposableEffect(Unit) {
                onDispose {
                    focusManager.clearFocus()
                }
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            var query by rememberSaveable { mutableStateOf("") }

            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(text = stringResource(id = R.string.query_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    onEvent(MealsViewEvent.OnSubmitQuery(query))
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.align(Alignment.End)) {
                OutlinedButton(onClick = {
                    onEvent(MealsViewEvent.OnSubmitQuery(null))
                    query = ""
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }) {
                    Text(text = stringResource(id = R.string.clear_label))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    onEvent(MealsViewEvent.OnSubmitQuery(query))
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }) {
                    Text(text = stringResource(id = R.string.search_label))
                }
            }
        }
    }, sheetState = modalBottomSheetState) {
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.toolbar_title))
                        },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    modalBottomSheetState.show()
                                }
                            }) {
                                Icon(Icons.Filled.Search, "search")
                            }
                        }
                    )
                },
                content = {
                    MealsScreenContent(
                        state = state,
                        onEvent = { onEvent(it) }
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = LocalSnackbarHostState.current,
                    ) {
                        Snackbar(
                            modifier = Modifier
                                .padding(24.dp, 0.dp, 24.dp, 64.dp),
                            backgroundColor = MaterialTheme.colors.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = it.message,
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@ExperimentalFoundationApi
@Composable
fun MealsScreenContent(
    state: MealsViewState,
    onEvent: (MealsViewEvent) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            SwipeRefresh(
                state = rememberSwipeRefreshState(state.isLoading),
                onRefresh = { onEvent(MealsViewEvent.LoadMeals) },
                indicator = { state, trigger -> SwipeRefreshIndicator(state, trigger) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxSize()
            ) {
                if (!state.isLoading) {
                    if (state.filteredMeals.isEmpty()) {
                        Text(text = stringResource(id = R.string.not_found))
                    } else {
                        val orientation = LocalConfiguration.current.orientation
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.filteredMeals) { meal ->
                                    Divider(thickness = 8.dp)
                                    MealRowComposable(meal, onMealClicked = {
                                        onEvent(
                                            MealsViewEvent.OnMealItemClicked(
                                                it
                                            )
                                        )
                                    })
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.background(
                                    MaterialTheme.colors.onSurface.copy(
                                        alpha = 0.12f
                                    )
                                )
                            ) {
                                itemsIndexed(state.filteredMeals) { index, meal ->
                                    val isFirstColumn = index % 2 == 0
                                    Column(
                                        Modifier.padding(
                                            if (isFirstColumn) 8.dp else 4.dp,
                                            8.dp,
                                            if (isFirstColumn) 4.dp else 8.dp,
                                            0.dp
                                        )
                                    ) {
                                        MealRowComposable(meal, onMealClicked = {
                                            onEvent(
                                                MealsViewEvent.OnMealItemClicked(
                                                    it
                                                )
                                            )
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealRowComposable(meal: Meal, onMealClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clip(RoundedCornerShape(4.dp))
            .padding(16.dp)
            .clickable { onMealClicked(meal.idMeal) }
    ) {
        MealImage(meal.strMealThumb, Modifier.size(64.dp))
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    meal.strCategory,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                meal.strMeal, fontWeight = FontWeight.Bold,
                style = typography.h6
            )
        }
    }
}