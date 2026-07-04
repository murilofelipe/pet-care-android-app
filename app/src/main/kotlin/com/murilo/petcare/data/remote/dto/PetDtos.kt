package com.murilo.petcare.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Contratos da fatia PetCare do Sition Web (rotas /api/v1/pets, /api/v1/pet-supplies, /api/v1/pet-expenses).
 * Datas trafegam como String ISO (LocalDate "aaaa-mm-dd" / LocalDateTime ISO-8601).
 */

@Serializable
data class PetDto(
    val id: String,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val name: String? = null,
    val species: String,
    val breed: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val weight: Double? = null,
    val photoUri: String? = null,
    val notes: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class PetRequest(
    val name: String,
    val species: String,
    val breed: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val weight: Double? = null,
    val photoUri: String? = null,
    val notes: String? = null,
)

@Serializable
data class HealthRecordDto(
    val id: String,
    val animalId: String,
    val recordedById: String? = null,
    val recordedByName: String? = null,
    val type: String,
    val description: String,
    val date: String,
    val nextDueDate: String? = null,
    val cost: Double? = null,
    val notes: String? = null,
    val vaccineBatch: String? = null,
    val appliedBy: String? = null,
    val dosage: String? = null,
    val frequency: String? = null,
    val endDate: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class HealthRecordRequest(
    val type: String,
    val description: String,
    val date: String,
    val nextDueDate: String? = null,
    val cost: Double? = null,
    val notes: String? = null,
    val vaccineBatch: String? = null,
    val appliedBy: String? = null,
    val dosage: String? = null,
    val frequency: String? = null,
    val endDate: String? = null,
)

@Serializable
data class VetAccessDto(
    val id: String,
    val animalId: String,
    val animalName: String? = null,
    val vetId: String,
    val vetName: String? = null,
    val vetEmail: String,
    val role: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class VetAccessRequest(
    val vetEmail: String,
)

@Serializable
data class SupplyDto(
    val id: String,
    val name: String,
    val category: String,
    val brand: String? = null,
    val quantity: Double = 1.0,
    val purchaseDate: String? = null,
    val replacementDate: String? = null,
    val createdAt: String? = null,
)

@Serializable
data class SupplyRequest(
    val name: String,
    val category: String,
    val brand: String? = null,
    val quantity: Double? = null,
    val purchaseDate: String? = null,
    val replacementDate: String? = null,
)

@Serializable
data class ExpenseDto(
    val id: String,
    val animalId: String? = null,
    val animalName: String? = null,
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val recurring: Boolean = false,
    val createdAt: String? = null,
)

@Serializable
data class ExpenseRequest(
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val recurring: Boolean = false,
    val animalId: String? = null,
)