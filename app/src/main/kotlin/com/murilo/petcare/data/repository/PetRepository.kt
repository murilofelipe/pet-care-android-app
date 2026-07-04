package com.murilo.petcare.data.repository

import com.murilo.petcare.data.local.HealthRecordDao
import com.murilo.petcare.data.local.HealthRecordEntity
import com.murilo.petcare.data.local.PetDao
import com.murilo.petcare.data.local.PetEntity
import com.murilo.petcare.data.remote.PetCareApi
import com.murilo.petcare.data.remote.dto.HealthRecordDto
import com.murilo.petcare.data.remote.dto.HealthRecordRequest
import com.murilo.petcare.data.remote.dto.PetDto
import com.murilo.petcare.data.remote.dto.PetRequest
import com.murilo.petcare.data.remote.dto.VetAccessDto
import com.murilo.petcare.data.remote.dto.VetAccessRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pets + saúde + acesso de veterinário.
 * Room é a fonte da UI (cache offline); refresh() sincroniza com a API.
 */
@Singleton
class PetRepository @Inject constructor(
    private val api: PetCareApi,
    private val petDao: PetDao,
    private val healthRecordDao: HealthRecordDao,
) {

    val myPets: Flow<List<PetEntity>> = petDao.observeMyPets()
    val sharedPets: Flow<List<PetEntity>> = petDao.observeSharedPets()

    fun observePet(petId: String): Flow<PetEntity?> = petDao.observePet(petId)

    fun observeHealthRecords(petId: String): Flow<List<HealthRecordEntity>> =
        healthRecordDao.observeByPet(petId)

    suspend fun refreshMyPets(): Result<Unit> = apiCall {
        val pets = api.listMyPets()
        petDao.replaceAll(shared = false, pets = pets.map { it.toEntity(shared = false) })
    }

    suspend fun refreshSharedPets(): Result<Unit> = apiCall {
        val pets = api.listSharedWithMe()
        petDao.replaceAll(shared = true, pets = pets.map { it.toEntity(shared = true) })
    }

    suspend fun createPet(request: PetRequest): Result<Unit> = apiCall {
        val created = api.createPet(request)
        petDao.upsertAll(listOf(created.toEntity(shared = false)))
    }

    suspend fun updatePet(petId: String, request: PetRequest): Result<Unit> = apiCall {
        val updated = api.updatePet(petId, request)
        petDao.upsertAll(listOf(updated.toEntity(shared = false)))
    }

    suspend fun deletePet(petId: String): Result<Unit> = apiCall {
        api.deletePet(petId)
        petDao.delete(petId)
    }

    suspend fun refreshHealthRecords(petId: String): Result<Unit> = apiCall {
        val records = api.listHealthRecords(petId)
        healthRecordDao.replaceForPet(petId, records.map { it.toEntity() })
    }

    suspend fun createHealthRecord(petId: String, request: HealthRecordRequest): Result<Unit> = apiCall {
        val created = api.createHealthRecord(petId, request)
        healthRecordDao.upsertAll(listOf(created.toEntity()))
    }

    // Acesso de veterinário — sem cache local: sempre online
    suspend fun listVetAccess(petId: String): Result<List<VetAccessDto>> =
        apiCall { api.listVetAccess(petId) }

    suspend fun grantVetAccess(petId: String, vetEmail: String): Result<VetAccessDto> =
        apiCall { api.grantVetAccess(petId, VetAccessRequest(vetEmail.trim())) }

    suspend fun revokeVetAccess(petId: String, accessId: String): Result<Unit> =
        apiCall { api.revokeVetAccess(petId, accessId) }
}

private fun PetDto.toEntity(shared: Boolean) = PetEntity(
    id = id,
    ownerId = ownerId,
    ownerName = ownerName,
    name = name.orEmpty(),
    species = species,
    breed = breed,
    gender = gender,
    birthDate = birthDate,
    weight = weight,
    photoUri = photoUri,
    notes = notes,
    shared = shared,
)

private fun HealthRecordDto.toEntity() = HealthRecordEntity(
    id = id,
    petId = animalId,
    recordedByName = recordedByName,
    type = type,
    description = description,
    date = date,
    nextDueDate = nextDueDate,
    cost = cost,
    notes = notes,
    vaccineBatch = vaccineBatch,
    appliedBy = appliedBy,
    dosage = dosage,
    frequency = frequency,
    endDate = endDate,
)