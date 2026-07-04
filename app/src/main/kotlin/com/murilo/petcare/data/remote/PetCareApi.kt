package com.murilo.petcare.data.remote

import com.murilo.petcare.data.remote.dto.*
import retrofit2.http.*

/**
 * API REST do Sition Web consumida pelo PetCare.
 * Endpoints da fatia de pets exigem JWT (adicionado pelo AuthInterceptor).
 * Obs.: em Kotlin, comentários de bloco aninham — nunca escreva "barra-asterisco" dentro de KDoc.
 */
interface PetCareApi {

    // ── Autenticação ────────────────────────────────────────────────────────

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // ── Pets ────────────────────────────────────────────────────────────────

    @GET("api/v1/pets")
    suspend fun listMyPets(): List<PetDto>

    @GET("api/v1/pets/shared-with-me")
    suspend fun listSharedWithMe(): List<PetDto>

    @GET("api/v1/pets/{petId}")
    suspend fun getPet(@Path("petId") petId: String): PetDto

    @POST("api/v1/pets")
    suspend fun createPet(@Body request: PetRequest): PetDto

    @PUT("api/v1/pets/{petId}")
    suspend fun updatePet(@Path("petId") petId: String, @Body request: PetRequest): PetDto

    @DELETE("api/v1/pets/{petId}")
    suspend fun deletePet(@Path("petId") petId: String)

    // ── Saúde (vacinação/medicação) ─────────────────────────────────────────

    @GET("api/v1/pets/{petId}/health")
    suspend fun listHealthRecords(@Path("petId") petId: String): List<HealthRecordDto>

    @POST("api/v1/pets/{petId}/health")
    suspend fun createHealthRecord(
        @Path("petId") petId: String,
        @Body request: HealthRecordRequest,
    ): HealthRecordDto

    // ── Acesso de veterinário ───────────────────────────────────────────────

    @GET("api/v1/pets/{petId}/vet-access")
    suspend fun listVetAccess(@Path("petId") petId: String): List<VetAccessDto>

    @POST("api/v1/pets/{petId}/vet-access")
    suspend fun grantVetAccess(
        @Path("petId") petId: String,
        @Body request: VetAccessRequest,
    ): VetAccessDto

    @DELETE("api/v1/pets/{petId}/vet-access/{accessId}")
    suspend fun revokeVetAccess(
        @Path("petId") petId: String,
        @Path("accessId") accessId: String,
    )

    // ── Utensílios/Ração ────────────────────────────────────────────────────

    @GET("api/v1/pet-supplies")
    suspend fun listSupplies(): List<SupplyDto>

    @POST("api/v1/pet-supplies")
    suspend fun createSupply(@Body request: SupplyRequest): SupplyDto

    @PUT("api/v1/pet-supplies/{supplyId}")
    suspend fun updateSupply(@Path("supplyId") supplyId: String, @Body request: SupplyRequest): SupplyDto

    @DELETE("api/v1/pet-supplies/{supplyId}")
    suspend fun deleteSupply(@Path("supplyId") supplyId: String)

    // ── Financeiro ──────────────────────────────────────────────────────────

    @GET("api/v1/pet-expenses")
    suspend fun listExpenses(): List<ExpenseDto>

    @POST("api/v1/pet-expenses")
    suspend fun createExpense(@Body request: ExpenseRequest): ExpenseDto

    @DELETE("api/v1/pet-expenses/{expenseId}")
    suspend fun deleteExpense(@Path("expenseId") expenseId: String)
}