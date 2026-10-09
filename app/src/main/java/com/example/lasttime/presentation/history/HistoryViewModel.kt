package com.example.lasttime.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lasttime.data.repository.ActivityRepository
import com.example.lasttime.domain.model.Activity
import com.example.lasttime.presentation.navigation.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(
        val activities: List<Activity>,
        val selected: Activity?,
        val dates: List<LocalDate>
    ) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(
    private val repository: ActivityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Vem da rota history/{activityId}; null quando abre pela aba "Histórico".
    private val selectedId = MutableStateFlow(savedStateHandle.get<Long>(Routes.ARG_ACTIVITY_ID))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HistoryUiState> =
        combine(repository.observeActivities(), selectedId) { activities, id -> activities to id }
            .flatMapLatest { (activities, id) -> historyFor(activities, id) }
            .catch { emit(HistoryUiState.Error("Não foi possível carregar o histórico.")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState.Loading)

    fun onSelectActivity(id: Long) {
        selectedId.value = id
    }

    private fun historyFor(activities: List<Activity>, id: Long?): Flow<HistoryUiState> {
        if (activities.isEmpty()) {
            return flowOf(HistoryUiState.Success(emptyList(), null, emptyList()))
        }
        val selected = if (id == null) activities.first() else activities.firstOrNull { it.id == id }
        if (selected == null) {
            return flowOf(HistoryUiState.Error("Atividade não encontrada."))
        }
        return repository.observeHistory(selected.id)
            .map { dates -> HistoryUiState.Success(activities, selected, dates) }
    }
}
