package com.murilo.petcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.model.Pet
import com.murilo.petcare.model.CreationCategory
import com.murilo.petcare.repository.PetRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por gerenciar o estado da UI dos animais da chácara.
 * Utiliza StateFlow para garantir que a UI no seu Xiaomi POCO seja sempre reativa.
 */
class PetViewModel(
    private val repository: PetRepository = PetRepository()
) : ViewModel() {

    // Coleta a lista de pets do repositório de forma reativa
    val pets: StateFlow<List<Pet>> = repository.pets

    init {
        // Dados iniciais para teste (Mock) com os animais da sua chácara
        if (pets.value.isEmpty()) {
            addPet(Pet(name = "Snow", species = "Cão", category = CreationCategory.DOMESTICO))
            addPet(Pet(name = "Sky", species = "Cão", category = CreationCategory.DOMESTICO))
            addPet(Pet(name = "Nox", species = "Gato", category = CreationCategory.DOMESTICO))
            addPet(Pet(name = "Aura", species = "Gato", category = CreationCategory.DOMESTICO))
            addPet(Pet(name = "Luna", species = "Coelho", category = CreationCategory.LAZER))
        }
    }

    /**
     * Adiciona um novo animal ou lote ao sistema.
     */
    fun addPet(pet: Pet) {
        viewModelScope.launch {
            repository.addPet(pet)
        }
    }

    /**
     * Atualiza os dados de um animal existente (Edição).
     */
    fun updatePet(updatedPet: Pet) {
        viewModelScope.launch {
            repository.updatePet(updatedPet)
        }
    }

    /**
     * Remove um animal ou lote pelo ID.
     */
    fun removePet(petId: String) {
        viewModelScope.launch {
            repository.removePet(petId)
        }
    }

    /**
     * Função auxiliar para buscar um pet pelo ID (útil para verificar parentesco).
     */
    fun getPetById(id: String): Pet? {
        return pets.value.find { it.id == id }
    }
}