package com.example.gestlyapp.ui.feactures.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestlyapp.data.model.AuthResult
import com.example.gestlyapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }
    
    fun login() {
        val currentState = _uiState.value
        
        // Validaciones básicas
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El email es requerido")
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña es requerida")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(errorMessage = "Email inválido")
            return
        }
        
        // Iniciar proceso de login
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            when (val result = authRepository.signIn(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // Ya está en loading
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false)
    }
}