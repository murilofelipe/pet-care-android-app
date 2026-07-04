package com.murilo.petcare.data.repository

import com.murilo.petcare.data.local.ExpenseDao
import com.murilo.petcare.data.local.ExpenseEntity
import com.murilo.petcare.data.remote.PetCareApi
import com.murilo.petcare.data.remote.dto.ExpenseDto
import com.murilo.petcare.data.remote.dto.ExpenseRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Financeiro doméstico — Room como cache offline, API como fonte de verdade. */
@Singleton
class ExpenseRepository @Inject constructor(
    private val api: PetCareApi,
    private val expenseDao: ExpenseDao,
) {

    val expenses: Flow<List<ExpenseEntity>> = expenseDao.observeAll()

    suspend fun refresh(): Result<Unit> = apiCall {
        expenseDao.replaceAll(api.listExpenses().map { it.toEntity() })
    }

    suspend fun create(request: ExpenseRequest): Result<Unit> = apiCall {
        expenseDao.upsertAll(listOf(api.createExpense(request).toEntity()))
    }

    suspend fun delete(expenseId: String): Result<Unit> = apiCall {
        api.deleteExpense(expenseId)
        refresh()
    }
}

private fun ExpenseDto.toEntity() = ExpenseEntity(
    id = id,
    petId = animalId,
    petName = animalName,
    description = description,
    amount = amount,
    category = category,
    date = date,
    recurring = recurring,
)