package com.murilo.petcare.data.repository

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject
import retrofit2.HttpException
import java.io.IOException

private val lenientJson = Json { ignoreUnknownKeys = true }

/**
 * Executa uma chamada de API traduzindo falhas para mensagens amigáveis
 * (extrai o campo "message" do ErrorResponseDTO do Sition Web quando houver).
 */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    Result.failure(Exception(extractMessage(e)))
} catch (e: IOException) {
    Result.failure(Exception("Sem conexão com o servidor. Verifique sua internet."))
} catch (e: Exception) {
    Result.failure(Exception(e.message ?: "Erro inesperado"))
}

private fun extractMessage(e: HttpException): String {
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
    val serverMessage = body?.let {
        runCatching {
            lenientJson.parseToJsonElement(it).jsonObject["message"]?.jsonPrimitive?.content
        }.getOrNull()
    }
    if (serverMessage != null) return serverMessage

    return when (e.code()) {
        401 -> "Sessão expirada ou credenciais inválidas"
        403 -> "Você não tem permissão para esta ação"
        404 -> "Registro não encontrado"
        else -> "Erro no servidor (HTTP ${e.code()})"
    }
}