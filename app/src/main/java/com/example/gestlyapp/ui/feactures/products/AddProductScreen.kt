package com.example.gestlyapp.ui.feactures.products

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestlyapp.data.model.WeightUnit
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.addProductUiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    // Colores consistentes con la app
    val backgroundColor = Color(0xFF2C2C2C)
    val textColor = Color.White
    val primaryBlue = Color(0xFF007AFF)
    val textFieldBackground = Color(0xFF3A3A3C)
    val hintColor = Color(0xFF8E8E93)
    
    // Estados para dropdowns y date picker
    var showWeightUnitDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Agregar Producto",
                            color = textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Nombre del producto
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    placeholder = {
                        Text(
                            text = "Nombre del producto",
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
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Precio de compra
                OutlinedTextField(
                    value = uiState.purchasePrice,
                    onValueChange = viewModel::updatePurchasePrice,
                    placeholder = {
                        Text(
                            text = "Precio de compra",
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
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Precio de venta
                OutlinedTextField(
                    value = uiState.sellingPrice,
                    onValueChange = viewModel::updateSellingPrice,
                    placeholder = {
                        Text(
                            text = "Precio de venta",
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
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Stock
                OutlinedTextField(
                    value = uiState.stock,
                    onValueChange = viewModel::updateStock,
                    placeholder = {
                        Text(
                            text = "Stock inicial",
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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Peso neto y unidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.netWeight,
                        onValueChange = viewModel::updateNetWeight,
                        placeholder = {
                            Text(
                                text = "Peso neto",
                                color = hintColor
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
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
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true
                    )
                    
                    // Dropdown para unidad de peso
                    ExposedDropdownMenuBox(
                        expanded = showWeightUnitDropdown,
                        onExpandedChange = { showWeightUnitDropdown = !showWeightUnitDropdown },
                        modifier = Modifier.width(100.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.weightUnit.displayName,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir",
                                    tint = hintColor
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
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
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showWeightUnitDropdown,
                            onDismissRequest = { showWeightUnitDropdown = false },
                            modifier = Modifier.background(textFieldBackground)
                        ) {
                            WeightUnit.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = unit.displayName,
                                            color = textColor
                                        )
                                    },
                                    onClick = {
                                        viewModel.updateWeightUnit(unit)
                                        showWeightUnitDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Sucursal
                OutlinedTextField(
                    value = uiState.branch,
                    onValueChange = viewModel::updateBranch,
                    placeholder = {
                        Text(
                            text = "Sucursal",
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
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Fecha de compra (opcional)
                OutlinedTextField(
                    value = uiState.purchaseDate?.let { 
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) 
                    } ?: "",
                    onValueChange = { },
                    placeholder = {
                        Text(
                            text = "Fecha de compra (opcional)",
                            color = hintColor
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = hintColor,
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { showDatePicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedContainerColor = textFieldBackground,
                        unfocusedContainerColor = textFieldBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botón Agregar Producto
                Button(
                    onClick = { viewModel.addProduct() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.isFormValid) primaryBlue else hintColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.isFormValid && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Agregar Producto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        
        // Notificación personalizada arriba de todo
        AnimatedVisibility(
            visible = uiState.errorMessage != null || uiState.successMessage != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.errorMessage != null) Color.Red else primaryBlue
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
                        imageVector = if (uiState.errorMessage != null) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.errorMessage ?: uiState.successMessage ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.updatePurchaseDate(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Confirmar", color = primaryBlue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancelar", color = hintColor)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = backgroundColor
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    headlineContentColor = textColor,
                    weekdayContentColor = textColor,
                    subheadContentColor = textColor,
                    dayContentColor = textColor,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = primaryBlue,
                    todayContentColor = primaryBlue,
                    todayDateBorderColor = primaryBlue
                )
            )
        }
    }
    
    // Limpiar mensajes después de 3 segundos
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
    
    uiState.successMessage?.let { success ->
        LaunchedEffect(success) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearSuccess()
            onNavigateBack()
        }
    }
}