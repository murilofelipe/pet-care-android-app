package com.murilo.petcare.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.local.ExpenseEntity
import com.murilo.petcare.data.local.PetEntity
import com.murilo.petcare.data.remote.dto.ExpenseRequest
import com.murilo.petcare.data.repository.ExpenseRepository
import com.murilo.petcare.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    petRepository: PetRepository,
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseEntity>> = expenseRepository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Pets do tutor, para vincular um gasto a um pet (opcional). */
    val pets: StateFlow<List<PetEntity>> = petRepository.myPets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Soma dos gastos do mês corrente. */
    val monthTotal: StateFlow<Double> = expenseRepository.expenses
        .map { list ->
            val now = LocalDate.now()
            list.filter {
                val date = runCatching { LocalDate.parse(it.date) }.getOrNull()
                date != null && date.year == now.year && date.month == now.month
            }.sumOf { it.amount }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _busy.value = true
            expenseRepository.refresh().onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun create(request: ExpenseRequest) {
        viewModelScope.launch {
            _busy.value = true
            expenseRepository.create(request).onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun delete(expenseId: String) {
        viewModelScope.launch {
            _busy.value = true
            expenseRepository.delete(expenseId).onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}