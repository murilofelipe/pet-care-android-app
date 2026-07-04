package com.murilo.petcare.data.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/** Anexa o JWT em toda chamada, exceto nos endpoints públicos de /auth/. */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val isAuthEndpoint = request.url.encodedPath.startsWith("/api/v1/auth/")
        val token = if (isAuthEndpoint) null else tokenStore.currentToken()

        val finalRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }
}