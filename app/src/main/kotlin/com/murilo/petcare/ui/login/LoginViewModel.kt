package com.murilo.petcare.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.petcare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val emailOrUsername: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(emailOrUsername = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.emailOrUsername.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Informe e-mail/usuário e senha")
            return
        }
        _uiState.value = state.copy(loading = true, error = null)
        viewModelScope.launch {
            authRepository.login(state.emailOrUsername, state.password)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(loading = false, error = e.message)
                }
            // Sucesso: o SessionViewModel observa o TokenStore e troca para o app
        }
    }
}