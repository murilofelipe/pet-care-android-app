package com.murilo.petcare.ui.pets

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.remote.dto.PetRequest
import com.murilo.petcare.data.repository.PetRepository
import com.murilo.petcare.ui.common.isValidIsoDateOrBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PetFormState(
    val name: String = "",
    val species: String = "",
    val breed: String = "",
    val gender: String? = null,
    val birthDate: String = "",
    val weight: String = "",
    val photoUri: String = "",
    val notes: String = "",
    val loading: Boolean = false,
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PetFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val petRepository: PetRepository,
) : ViewModel() {

    val petId: String? = savedStateHandle["petId"]

    private val _state = MutableStateFlow(PetFormState(loading = petId != null))
    val state: StateFlow<PetFormState> = _state.asStateFlow()

    init {
        petId?.let { id ->
            viewModelScope.launch {
                val pet = petRepository.observePet(id).first()
                if (pet != null) {
                    _state.value = _state.value.copy(
                        name = pet.name,
                        species = pet.species,
                        breed = pet.breed.orEmpty(),
                        gender = pet.gender,
                        birthDate = pet.birthDate.orEmpty(),
                        weight = pet.weight?.toString().orEmpty(),
                        photoUri = pet.photoUri.orEmpty(),
                        notes = pet.notes.orEmpty(),
                        loading = false,
                    )
                } else {
                    _state.value = _state.value.copy(loading = false, error = "Pet não encontrado")
                }
            }
        }
    }

    fun update(transform: PetFormState.() -> PetFormState) {
        _state.value = _state.value.transform().copy(error = null)
    }

    fun save() {
        val s = _state.value
        val error = when {
            s.name.isBlank() -> "Informe o nome do pet"
            s.species.isBlank() -> "Informe a espécie"
            !s.birthDate.isValidIsoDateOrBlank() -> "Data de nascimento inválida (use aaaa-mm-dd)"
            s.weight.isNotBlank() && normalizedWeight(s.weight) == null -> "Peso inválido"
            else -> null
        }
        if (error != null) {
            _state.value = s.copy(error = error)
            return
        }

        val request = PetRequest(
            name = s.name.trim(),
            species = s.species.trim(),
            breed = s.breed.trim().ifBlank { null },
            gender = s.gender,
            birthDate = s.birthDate.trim().ifBlank { null },
            weight = normalizedWeight(s.weight),
            photoUri = s.photoUri.trim().ifBlank { null },
            notes = s.notes.trim().ifBlank { null },
        )

        _state.value = s.copy(saving = true, error = null)
        viewModelScope.launch {
            val result = if (petId == null) {
                petRepository.createPet(request)
            } else {
                petRepository.updatePet(petId, request)
            }
            result
                .onSuccess { _state.value = _state.value.copy(saving = false, saved = true) }
                .onFailure { _state.value = _state.value.copy(saving = false, error = it.message) }
        }
    }

    private fun normalizedWeight(raw: String): Double? =
        raw.trim().replace(',', '.').toDoubleOrNull()
}