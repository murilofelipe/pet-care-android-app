package com.murilo.petcare.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "petcare_auth")

/** Sessão do usuário autenticado, persistida em DataStore. */
data class Session(
    val token: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val role: String,
)

/**
 * Guarda o JWT e os dados básicos do usuário logado.
 * O token é cacheado em memória para o AuthInterceptor não bloquear a cada request.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val ROLE = stringPreferencesKey("role")
    }

    @Volatile
    private var cachedToken: String? = null

    val session: Flow<Session?> = context.authDataStore.data.map { prefs ->
        val token = prefs[Keys.TOKEN] ?: return@map null
        Session(
            token = token,
            userId = prefs[Keys.USER_ID].orEmpty(),
            userName = prefs[Keys.USER_NAME].orEmpty(),
            userEmail = prefs[Keys.USER_EMAIL].orEmpty(),
            role = prefs[Keys.ROLE].orEmpty(),
        )
    }

    /** Token atual, para uso síncrono no interceptor OkHttp. */
    fun currentToken(): String? {
        cachedToken?.let { return it }
        // Primeira leitura após processo iniciar: bloqueia uma única vez
        return runBlocking { context.authDataStore.data.first()[Keys.TOKEN] }
            .also { cachedToken = it }
    }

    suspend fun save(session: Session) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = session.token
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.USER_NAME] = session.userName
            prefs[Keys.USER_EMAIL] = session.userEmail
            prefs[Keys.ROLE] = session.role
        }
        cachedToken = session.token
    }

    suspend fun clear() {
        context.authDataStore.edit { it.clear() }
        cachedToken = null
    }
}