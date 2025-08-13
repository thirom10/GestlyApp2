package com.example.gestlyapp.data.repository

import com.example.gestlyapp.data.model.AuthResult
import com.example.gestlyapp.data.model.AuthState
import com.example.gestlyapp.data.model.User
import com.example.gestlyapp.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    
    // Observar el estado de autenticación
    val authState: Flow<AuthState> = firebaseDataSource.authState.map { firebaseUser ->
        if (firebaseUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }
    
    // Registrar usuario
    suspend fun registerUser(email: String, password: String, fullName: String): AuthResult {
        return firebaseDataSource.registerUser(email, password, fullName)
    }
    
    // Iniciar sesión
    suspend fun signIn(email: String, password: String): AuthResult {
        return firebaseDataSource.signIn(email, password)
    }
    
    // Cerrar sesión
    suspend fun signOut(): AuthResult {
        return firebaseDataSource.signOut()
    }
    
    // Obtener datos del usuario actual
    suspend fun getCurrentUserData(): User? {
        val currentUser = firebaseDataSource.getCurrentUser()
        return if (currentUser != null) {
            firebaseDataSource.getUserData(currentUser.uid)
        } else {
            null
        }
    }
    
    // Verificar si el usuario está autenticado
    fun isUserAuthenticated(): Boolean {
        return firebaseDataSource.getCurrentUser() != null
    }
    
    // Obtener UID del usuario actual
    fun getCurrentUserId(): String? {
        return firebaseDataSource.getCurrentUser()?.uid
    }
    
    // Actualizar datos del usuario
    suspend fun updateUserData(user: User): AuthResult {
        return firebaseDataSource.updateUserData(user)
    }
    
    // Eliminar cuenta
    suspend fun deleteAccount(): AuthResult {
        return firebaseDataSource.deleteAccount()
    }
}