package com.example.gestlyapp.data.remote

import com.example.gestlyapp.data.model.AuthResult
import com.example.gestlyapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor() {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    // Observar el estado de autenticación
    val authState: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }
    
    // Obtener usuario actual
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    // Registrar usuario
    suspend fun registerUser(email: String, password: String, fullName: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                // Crear documento del usuario en Firestore
                val user = User(
                    uid = firebaseUser.uid,
                    fullName = fullName,
                    email = email
                )
                
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
                
                AuthResult.Success
            } else {
                AuthResult.Error("Error al crear el usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }
    
    // Iniciar sesión
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al iniciar sesión")
        }
    }
    
    // Cerrar sesión
    suspend fun signOut(): AuthResult {
        return try {
            auth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al cerrar sesión")
        }
    }
    
    // Obtener datos del usuario desde Firestore
    suspend fun getUserData(uid: String): User? {
        return try {
            val document = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    // Actualizar datos del usuario
    suspend fun updateUserData(user: User): AuthResult {
        return try {
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
            
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al actualizar usuario")
        }
    }
    
    // Eliminar cuenta
    suspend fun deleteAccount(): AuthResult {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Eliminar documento de Firestore
                firestore.collection("users")
                    .document(currentUser.uid)
                    .delete()
                    .await()
                
                // Eliminar cuenta de Authentication
                currentUser.delete().await()
                
                AuthResult.Success
            } else {
                AuthResult.Error("No hay usuario autenticado")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al eliminar cuenta")
        }
    }
}