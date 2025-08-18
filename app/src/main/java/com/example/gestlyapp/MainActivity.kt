package com.example.gestlyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.gestlyapp.data.datasource.FirebaseProductDataSource
import com.example.gestlyapp.data.remote.FirebaseDataSource
import com.example.gestlyapp.data.repository.AuthRepository
import com.example.gestlyapp.data.repository.ProductRepository
import com.example.gestlyapp.data.repository.CachedProductRepository
import com.example.gestlyapp.data.local.AppDatabase
import com.example.gestlyapp.data.session.UserSessionManager
import com.example.gestlyapp.data.preloader.DataPreloader
import com.example.gestlyapp.data.cache.MemoryOptimizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import com.example.gestlyapp.ui.feactures.login.LoginScreen
import com.example.gestlyapp.ui.feactures.login.LoginViewModel
import com.example.gestlyapp.ui.feactures.register.RegisterScreen
import com.example.gestlyapp.ui.feactures.register.RegisterViewModel
import com.example.gestlyapp.ui.feactures.products.ProductViewModel
import com.example.gestlyapp.ui.main.MainScreen
import com.example.gestlyapp.ui.theme.GestlyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestlyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavigation()
                }
            }
        }
    }
}

@Composable
fun AuthNavigation() {
    val context = LocalContext.current
    
    // Crear instancias de los repositorios
    val firebaseDataSource = remember { FirebaseDataSource() }
    val sessionManager = remember { UserSessionManager(context) }
    val authRepository = remember { AuthRepository(firebaseDataSource, sessionManager) }
    
    // Verificar si hay una sesión activa al iniciar
    var currentScreen by remember { 
        mutableStateOf(
            if (sessionManager.isUserLoggedIn()) "main" else "login"
        )
    }
    
    // Crear instancias para productos con caché
    val firebaseProductDataSource = remember { FirebaseProductDataSource() }
    val database = remember { 
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gestly_database"
        ).build()
    }
    val productDao = remember { database.productDao() }
    val coroutineScope = remember { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    val productRepository = remember { 
        CachedProductRepository(firebaseProductDataSource, productDao, coroutineScope) 
    }
    
    // Crear preloader
    val dataPreloader = remember { DataPreloader(productRepository, sessionManager, context) }
    
    // Crear optimizador de memoria
    val memoryOptimizer = remember { MemoryOptimizer(context) }
    
    // Registrar el repositorio con caché en el optimizador
    LaunchedEffect(productRepository) {
        memoryOptimizer.registerCachedRepository(productRepository)
    }
    
    when (currentScreen) {
        "login" -> {
            val loginViewModel = viewModel<LoginViewModel> {
                LoginViewModel(authRepository)
            }
            
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    currentScreen = "register"
                },
                onLoginSuccess = {
                    // Actualizar última actividad
                    authRepository.updateLastActivity()
                    // Iniciar preloading después del login
                    dataPreloader.preloadOnLogin()
                    currentScreen = "main"
                }
            )
        }
        "register" -> {
            val registerViewModel = viewModel<RegisterViewModel> {
                RegisterViewModel(authRepository)
            }
            
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = {
                    currentScreen = "login"
                },
                onNavigateToLogin = {
                    currentScreen = "login"
                },
                onRegistrationSuccess = {
                    currentScreen = "login"
                }
            )
        }
        "main" -> {
            val productViewModel = viewModel<ProductViewModel> {
                ProductViewModel(productRepository)
            }
            
            // Iniciar preloading en background y optimización de memoria
            LaunchedEffect(Unit) {
                dataPreloader.startPreloading()
                memoryOptimizer.checkAndOptimizeIfNeeded()
            }
            
            MainScreen(
                productViewModel = productViewModel,
                userName = authRepository.getCurrentUserName(),
                onLogout = {
                    // Cerrar sesión usando el repositorio
                    kotlinx.coroutines.runBlocking {
                        authRepository.signOut()
                    }
                    dataPreloader.cleanup()
                    memoryOptimizer.optimizeMemory()
                    currentScreen = "login"
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    GestlyAppTheme {
        LoginScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    GestlyAppTheme {
        RegisterScreen()
    }
}