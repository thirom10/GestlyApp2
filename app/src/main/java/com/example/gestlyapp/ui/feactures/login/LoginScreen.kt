package com.example.gestlyapp.ui.feactures.login

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.* // Keep this for other Material 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Manejar el éxito del login
    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            // La notificación personalizada se encarga de esto
            kotlinx.coroutines.delay(1500) // Esperar un poco para que se vea la notificación personalizada
            onLoginSuccess()
            viewModel.resetLoginSuccess()
            // No necesitamos mostrar Snackbar aquí si tenemos la notificación personalizada
            // snackbarHostState.showSnackbar(message = "¡Login exitoso! Bienvenido a Gestly", duration = SnackbarDuration.Short)

        }
    }
    
    // Colores basados en la captura
    val backgroundColor = Color(0xFF2C2C2C) // Gris oscuro del fondo
    val primaryBlue = Color(0xFF007AFF) // Azul del botón
    val textFieldBackground = Color(0xFF3C3C3C) // Gris más claro para campos
    val textColor = Color.White
    val hintColor = Color(0xFF8E8E93) // Gris para placeholders
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .padding(top = 60.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Spacer(modifier = Modifier.weight(1f))

                    // Título "Gestly"
                    Text(
                        text = "Gestly",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtítulo
                    Text(
                        text = "Administra tu negocio con facilidad.",
                        fontSize = 16.sp,
                        color = hintColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Campo Email
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail,
                        placeholder = {
                            Text(
                                text = "Email",
                                color = hintColor
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = textFieldBackground,
                            unfocusedContainerColor = textFieldBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::updatePassword,
                        placeholder = {
                            Text(
                                text = "Contraseña",
                                color = hintColor
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = textFieldBackground,
                            unfocusedContainerColor = textFieldBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón Iniciar Sesión
                    Button(
                        onClick = { viewModel.login() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Texto de registro
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            color = hintColor,
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = onNavigateToRegister,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Regístrate",
                                color = primaryBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }

                // Mostrar error si existe usando la notificación personalizada
                // No necesitamos el LaunchedEffect aquí si la notificación personalizada maneja la visibilidad del error
                /*
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // La notificación personalizada se encarga de esto
                // snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
                kotlinx.coroutines.delay(3000) // Duración de la notificación de error
                viewModel.clearError()
            }
        }
        */
            }
            // Notificación personalizada arriba de todo
            AnimatedVisibility(
                visible = uiState.isLoginSuccessful || !uiState.errorMessage.isNullOrEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(300)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!uiState.errorMessage.isNullOrEmpty()) Color(0xFFFF3B30) else primaryBlue
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (!uiState.errorMessage.isNullOrEmpty()) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (!uiState.errorMessage.isNullOrEmpty()) uiState.errorMessage!! else "¡Login exitoso! Bienvenido a Gestly",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Efecto para limpiar el error después de un tiempo si la notificación está visible
            LaunchedEffect(uiState.errorMessage) {
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    kotlinx.coroutines.delay(3000) // Mantener la notificación de error visible por 3 segundos
                    if (!uiState.errorMessage.isNullOrEmpty()) { // Comprobar de nuevo por si cambió
                        viewModel.clearError()
                    }
                }
            }
        }
    }
}