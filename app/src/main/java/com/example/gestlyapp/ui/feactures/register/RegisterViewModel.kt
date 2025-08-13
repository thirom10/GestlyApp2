package com.example.gestlyapp.ui.feactures.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestlyapp.data.model.AuthResult
import com.example.gestlyapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun updateFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName, errorMessage = null)
        validateForm()
    }
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
        validateForm()
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
        validateForm()
    }
    
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
        validateForm()
    }
    
    private fun validateForm() {
        val currentState = _uiState.value
        val isValid = currentState.fullName.isNotBlank() &&
                currentState.email.isNotBlank() &&
                currentState.password.isNotBlank() &&
                currentState.confirmPassword.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() &&
                currentState.password == currentState.confirmPassword &&
                currentState.password.length >= 6
        
        _uiState.value = currentState.copy(isFormValid = isValid)
    }
    
    fun register() {
        val currentState = _uiState.value
        
        // Validaciones
        if (currentState.fullName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre completo es requerido")
            return
        }
        
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El email es requerido")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(errorMessage = "Email inválido")
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña es requerida")
            return
        }
        
        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        
        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }
        
        // Iniciar proceso de registro
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            when (val result = authRepository.registerUser(
                email = currentState.email,
                password = currentState.password,
                fullName = currentState.fullName
            )) {
                is AuthResult.Success -> {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true,
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
    
    fun resetRegistrationSuccess() {
        _uiState.value = _uiState.value.copy(isRegistrationSuccessful = false)
    }
}