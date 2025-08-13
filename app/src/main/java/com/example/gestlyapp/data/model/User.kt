package com.example.gestlyapp.data.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

sealed class AuthResult {
    data object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    data object Loading : AuthResult()
}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
}