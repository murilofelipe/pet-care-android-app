package com.murilo.petcare.repository

import com.murilo.petcare.model.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositório responsável pela persistência (atualmente em memória).
 * Centraliza as operações de CRUD para garantir a integridade dos dados.
 */
class PetRepository {

    // Lista mutável interna que armazena os animais e lotes
    private val _pets = MutableStateFlow<List<Pet>>(emptyList())

    // Exposição segura da lista para o ViewModel
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    /**
     * Adiciona um novo animal (Individual ou Lote).
     */
    fun addPet(pet: Pet) {
        val currentList = _pets.value.toMutableList()
        currentList.add(pet)
        _pets.value = currentList
    }

    /**
     * Atualiza um animal existente buscando pelo seu ID único.
     * Essencial para o fluxo de edição que corrigimos no formulário.
     */
    fun updatePet(updatedPet: Pet) {
        val currentList = _pets.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedPet.id }
        if (index != -1) {
            currentList[index] = updatedPet
            _pets.value = currentList
        }
    }

    /**
     * Remove um animal ou lote da lista.
     */
    fun removePet(petId: String) {
        val currentList = _pets.value.toMutableList()
        currentList.removeAll { it.id == petId }
        _pets.value = currentList
    }
}