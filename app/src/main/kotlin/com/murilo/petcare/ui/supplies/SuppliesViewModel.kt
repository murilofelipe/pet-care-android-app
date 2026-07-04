package com.murilo.petcare.ui.supplies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.local.SupplyEntity
import com.murilo.petcare.data.remote.dto.SupplyRequest
import com.murilo.petcare.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuppliesViewModel @Inject constructor(
    private val supplyRepository: SupplyRepository,
) : ViewModel() {

    val supplies: StateFlow<List<SupplyEntity>> = supplyRepository.supplies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
            supplyRepository.refresh().onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    /** supplyId nulo = criação; preenchido = edição. */
    fun save(supplyId: String?, request: SupplyRequest) {
        viewModelScope.launch {
            _busy.value = true
            val result = if (supplyId == null) {
                supplyRepository.create(request)
            } else {
                supplyRepository.update(supplyId, request)
            }
            result.onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun delete(supplyId: String) {
        viewModelScope.launch {
            _busy.value = true
            supplyRepository.delete(supplyId).onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}