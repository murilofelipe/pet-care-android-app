package com.murilo.petcare.ui.pets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.local.HealthRecordEntity
import com.murilo.petcare.data.local.PetEntity
import com.murilo.petcare.data.remote.dto.HealthRecordRequest
import com.murilo.petcare.data.remote.dto.VetAccessDto
import com.murilo.petcare.data.repository.PetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val petRepository: PetRepository,
) : ViewModel() {

    val petId: String = checkNotNull(savedStateHandle["petId"])

    val pet: StateFlow<PetEntity?> = petRepository.observePet(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val healthRecords: StateFlow<List<HealthRecordEntity>> =
        petRepository.observeHealthRecords(petId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _vetAccess = MutableStateFlow<List<VetAccessDto>>(emptyList())
    val vetAccess: StateFlow<List<VetAccessDto>> = _vetAccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    /** true depois que o pet foi excluído — a tela deve voltar. */
    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            petRepository.refreshHealthRecords(petId).onFailure { _error.value = it.message }
            refreshVetAccess()
        }
    }

    private suspend fun refreshVetAccess() {
        // Somente o tutor pode listar acessos; para o vet a chamada falha com 400/403 — ignora
        petRepository.listVetAccess(petId).onSuccess { _vetAccess.value = it }
    }

    fun addHealthRecord(request: HealthRecordRequest) {
        viewModelScope.launch {
            _busy.value = true
            petRepository.createHealthRecord(petId, request)
                .onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun grantVetAccess(email: String) {
        if (email.isBlank()) {
            _error.value = "Informe o e-mail do veterinário"
            return
        }
        viewModelScope.launch {
            _busy.value = true
            petRepository.grantVetAccess(petId, email)
                .onSuccess { refreshVetAccess() }
                .onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun revokeVetAccess(accessId: String) {
        viewModelScope.launch {
            _busy.value = true
            petRepository.revokeVetAccess(petId, accessId)
                .onSuccess { refreshVetAccess() }
                .onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun deletePet() {
        viewModelScope.launch {
            _busy.value = true
            petRepository.deletePet(petId)
                .onSuccess { _deleted.value = true }
                .onFailure { _error.value = it.message }
            _busy.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}