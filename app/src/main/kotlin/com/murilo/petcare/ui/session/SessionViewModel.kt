package com.murilo.petcare.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.auth.Session
import com.murilo.petcare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val session: Session) : SessionState
}

/** Decide entre tela de login e app autenticado, observando o TokenStore. */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val state: StateFlow<SessionState> = authRepository.session
        .map { session ->
            if (session == null) SessionState.LoggedOut else SessionState.LoggedIn(session)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SessionState.Loading)

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}