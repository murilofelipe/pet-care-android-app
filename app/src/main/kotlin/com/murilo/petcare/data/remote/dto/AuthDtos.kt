package com.murilo.petcare.data.remote.dto

import kotlinx.serialization.Serializable

/** Contratos de autenticação do Sition Web (POST /api/v1/auth/login). */

@Serializable
data class LoginRequest(
    val emailOrUsername: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
    val type: String = "Bearer",
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val fullName: String,
    val email: String,
    val cpf: String? = null,
    val phone: String? = null,
    val role: String,
    val active: Boolean = true,
    val emailVerified: Boolean = false,
    val createdAt: String? = null,
)
