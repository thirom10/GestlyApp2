package com.example.gestlyapp.data.repository

import com.example.gestlyapp.data.model.AuthResult
import com.example.gestlyapp.data.model.AuthState
import com.example.gestlyapp.data.model.User
import com.example.gestlyapp.data.remote.FirebaseDataSource
import com.example.gestlyapp.data.session.UserSessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val sessionManager: UserSessionManager
) {
    
    // Observar el estado de autenticación combinando Firebase y sesión local
    val authState: Flow<AuthState> = combine(
        firebaseDataSource.authState,
        sessionManager.isLoggedIn
    ) { firebaseUser, isLocallyLoggedIn ->
        when {
            firebaseUser != null -> AuthState.Authenticated
            isLocallyLoggedIn -> AuthState.Authenticated
            else -> AuthState.Unauthenticated
        }
    }
    
    // Registrar usuario
    suspend fun registerUser(email: String, password: String, fullName: String): AuthResult {
        return firebaseDataSource.registerUser(email, password, fullName)
    }
    
    // Iniciar sesión
    suspend fun signIn(email: String, password: String): AuthResult {
        val result = firebaseDataSource.signIn(email, password)
        
        if (result is AuthResult.Success) {
            // Obtener datos del usuario y guardar sesión
            val currentUser = firebaseDataSource.getCurrentUser()
            currentUser?.let { firebaseUser ->
                val userData = firebaseDataSource.getUserData(firebaseUser.uid)
                userData?.let { user ->
                    sessionManager.saveUserSession(user)
                }
            }
        }
        
        return result
    }
    
    // Cerrar sesión
    suspend fun signOut(): AuthResult {
        val result = firebaseDataSource.signOut()
        
        // Limpiar sesión local independientemente del resultado de Firebase
        sessionManager.clearUserSession()
        
        return result
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
        return firebaseDataSource.getCurrentUser() != null || sessionManager.isUserLoggedIn()
    }
    
    // Obtener UID del usuario actual
    fun getCurrentUserId(): String? {
        return firebaseDataSource.getCurrentUser()?.uid ?: sessionManager.getCurrentUserId()
    }
    
    // Obtener nombre del usuario actual
    fun getCurrentUserName(): String {
        return sessionManager.getCurrentUserName()
    }
    
    // Obtener usuario actual desde sesión
    fun getCurrentUserFromSession(): User? {
        return sessionManager.currentUser.value
    }
    
    // Actualizar última actividad
    fun updateLastActivity() {
        sessionManager.updateLastActivity()
    }
    
    // Actualizar datos del usuario
    suspend fun updateUserData(user: User): AuthResult {
        val result = firebaseDataSource.updateUserData(user)
        
        if (result is AuthResult.Success) {
            // Actualizar datos en sesión local
            sessionManager.updateUserData(user)
        }
        
        return result
    }
    
    // Eliminar cuenta
    suspend fun deleteAccount(): AuthResult {
        return firebaseDataSource.deleteAccount()
    }
}