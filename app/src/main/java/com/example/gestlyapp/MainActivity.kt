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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestlyapp.data.remote.FirebaseDataSource
import com.example.gestlyapp.data.repository.AuthRepository
import com.example.gestlyapp.data.datasource.FirebaseProductDataSource
import com.example.gestlyapp.data.repository.ProductRepository
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
    var currentScreen by remember { mutableStateOf("login") }
    
    // Crear instancias de los repositorios
    val firebaseDataSource = remember { FirebaseDataSource() }
    val authRepository = remember { AuthRepository(firebaseDataSource) }
    
    // Crear instancias para productos
    val firebaseProductDataSource = remember { FirebaseProductDataSource() }
    val productRepository = remember { ProductRepository(firebaseProductDataSource) }
    
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
            
            MainScreen(
                productViewModel = productViewModel,
                onLogout = {
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