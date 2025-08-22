package com.example.gestlyapp.ui.feactures.products

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestlyapp.data.model.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductsScreen(
    viewModel: ProductViewModel,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToEditProduct: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    var debouncedSearchQuery by remember { mutableStateOf("") }
    
    // Debounce para la búsqueda (optimización de rendimiento)
    LaunchedEffect(uiState.searchQuery) {
        kotlinx.coroutines.delay(300) // Esperar 300ms antes de buscar
        debouncedSearchQuery = uiState.searchQuery
    }
    
    // Detectar cuando se llega al final de la lista para paginación
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= uiState.products.size - 5 // Cargar más cuando quedan 5 items
        }
    }
    
    // Efecto para cargar más productos cuando sea necesario
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoading && uiState.products.isNotEmpty()) {
            viewModel.loadMoreProducts()
        }
    }
    
    // Colores consistentes con la app
    val backgroundColor = Color(0xFF2C2C2C)
    val textColor = Color.White
    val primaryBlue = Color(0xFF007AFF)
    val cardBackground = Color(0xFF1C1C1E)
    val textFieldBackground = Color(0xFF3A3A3C)
    val hintColor = Color(0xFF8E8E93)
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = backgroundColor,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAddProduct,
                    containerColor = primaryBlue,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "Productos",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Barra de búsqueda
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::searchProducts,
                    placeholder = {
                        Text(
                            text = "Buscar productos...",
                            color = hintColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = hintColor
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
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Lista de productos
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryBlue,
                            strokeWidth = 3.dp
                        )
                    }
                } else if (uiState.products.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (uiState.searchQuery.isNotBlank()) "No se encontraron productos" else "No hay productos",
                                fontSize = 18.sp,
                                color = hintColor,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotBlank()) "Intenta con otra búsqueda" else "Agrega tu primer producto",
                                fontSize = 14.sp,
                                color = hintColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.products,
                            key = { product -> product.id } // Key para mejor rendimiento
                        ) { product ->
                            ProductCard(
                                product = product,
                                cardBackground = cardBackground,
                                textColor = textColor,
                                primaryBlue = primaryBlue,
                                hintColor = hintColor,
                                onEditProduct = { onNavigateToEditProduct(product.id) }
                            )
                        }
                        
                        // Indicador de carga al final de la lista
                        if (uiState.isLoading && uiState.products.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = primaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        
                        // Espacio extra para el FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
        
        // Notificación personalizada arriba de todo
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
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
                    containerColor = Color.Red
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
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Limpiar error después de 3 segundos
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    cardBackground: Color,
    textColor: Color,
    primaryBlue: Color,
    hintColor: Color,
    onEditProduct: () -> Unit
) {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditProduct() },
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre del producto
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Precio",
                        fontSize = 12.sp,
                        color = hintColor
                    )
                    Text(
                        text = numberFormat.format(product.sellingPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryBlue
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Stock",
                        fontSize = 12.sp,
                        color = hintColor
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (product.stock > 10) Color.Green
                                    else if (product.stock > 0) Color.Yellow
                                    else Color.Red
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.stock} unidades",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sucursal: ${product.branch}",
                    fontSize = 12.sp,
                    color = hintColor
                )
                Text(
                    text = "${product.netWeight} ${product.weightUnit.displayName}",
                    fontSize = 12.sp,
                    color = hintColor
                )
            }
        }
    }
}