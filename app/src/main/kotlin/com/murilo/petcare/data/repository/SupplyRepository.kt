package com.murilo.petcare.data.repository

import com.murilo.petcare.data.local.SupplyDao
import com.murilo.petcare.data.local.SupplyEntity
import com.murilo.petcare.data.remote.PetCareApi
import com.murilo.petcare.data.remote.dto.SupplyDto
import com.murilo.petcare.data.remote.dto.SupplyRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Utensílios/ração — Room como cache offline, API como fonte de verdade. */
@Singleton
class SupplyRepository @Inject constructor(
    private val api: PetCareApi,
    private val supplyDao: SupplyDao,
) {

    val supplies: Flow<List<SupplyEntity>> = supplyDao.observeAll()

    suspend fun refresh(): Result<Unit> = apiCall {
        supplyDao.replaceAll(api.listSupplies().map { it.toEntity() })
    }

    suspend fun create(request: SupplyRequest): Result<Unit> = apiCall {
        supplyDao.upsertAll(listOf(api.createSupply(request).toEntity()))
    }

    suspend fun update(supplyId: String, request: SupplyRequest): Result<Unit> = apiCall {
        supplyDao.upsertAll(listOf(api.updateSupply(supplyId, request).toEntity()))
    }

    suspend fun delete(supplyId: String): Result<Unit> = apiCall {
        api.deleteSupply(supplyId)
        refresh()
    }
}

private fun SupplyDto.toEntity() = SupplyEntity(
    id = id,
    name = name,
    category = category,
    brand = brand,
    quantity = quantity,
    purchaseDate = purchaseDate,
    replacementDate = replacementDate,
)