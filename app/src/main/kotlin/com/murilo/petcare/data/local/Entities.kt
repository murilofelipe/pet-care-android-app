package com.murilo.petcare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cache offline (Room) — espelho local do que veio da API.
 * `shared = true` marca pets compartilhados comigo via VetAnimalAccess.
 */

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey val id: String,
    val ownerId: String?,
    val ownerName: String?,
    val name: String,
    val species: String,
    val breed: String?,
    val gender: String?,
    val birthDate: String?,
    val weight: Double?,
    val photoUri: String?,
    val notes: String?,
    val shared: Boolean = false,
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey val id: String,
    val petId: String,
    val recordedByName: String?,
    val type: String,
    val description: String,
    val date: String,
    val nextDueDate: String?,
    val cost: Double?,
    val notes: String?,
    val vaccineBatch: String?,
    val appliedBy: String?,
    val dosage: String?,
    val frequency: String?,
    val endDate: String?,
)

@Entity(tableName = "supplies")
data class SupplyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val brand: String?,
    val quantity: Double,
    val purchaseDate: String?,
    val replacementDate: String?,
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val petId: String?,
    val petName: String?,
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val recurring: Boolean,
)