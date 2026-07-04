package com.murilo.petcare.data.repository

import com.murilo.petcare.data.auth.Session
import com.murilo.petcare.data.auth.TokenStore
import com.murilo.petcare.data.local.PetCareDatabase
import com.murilo.petcare.data.remote.PetCareApi
import com.murilo.petcare.data.remote.dto.LoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Login/logout reaproveitando o fluxo JWT do Sition Web. */
@Singleton
class AuthRepository @Inject constructor(
    private val api: PetCareApi,
    private val tokenStore: TokenStore,
    private val database: PetCareDatabase,
) {

    /** Sessão persistida (null = não logado). */
    val session: Flow<Session?> = tokenStore.session

    suspend fun login(emailOrUsername: String, password: String): Result<Session> =
        apiCall { api.login(LoginRequest(emailOrUsername.trim(), password)) }
            .map { response ->
                Session(
                    token = response.token,
                    userId = response.user.id,
                    userName = response.user.fullName,
                    userEmail = response.user.email,
                    role = response.user.role,
                ).also { tokenStore.save(it) }
            }

    suspend fun logout() {
        tokenStore.clear()
        database.clearAllTables()
    }
}