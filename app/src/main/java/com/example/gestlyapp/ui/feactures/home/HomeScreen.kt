package com.example.gestlyapp.ui.feactures.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String = "Usuario"
) {
    // Colores consistentes con la app memoizados
    val backgroundColor = remember { Color(0xFF2C2C2C) }
    val textColor = remember { Color.White }
    val primaryBlue = remember { Color(0xFF007AFF) }
    val cardColor = remember { Color(0xFF1C1C1E) }
    
    // Datos ficticios para las ganancias memoizados
    val monthlyEarnings = remember { 12450.0 }
    val weeklyEarnings = remember { 3120.0 }
    val monthlyPercentage = remember { 12 }
    val weeklyPercentage = remember { -3 }
    
    // Productos ficticios más vendidos memoizados
    val topProducts = remember {
        listOf(
            Product("1", "Alfajor de Chocolate", 150.0, 25),
            Product("2", "Galletas Oreo", 89.0, 30),
            Product("3", "Coca Cola 500ml", 45.0, 15)
        )
    }
    
    // Productos con stock bajo (menos de 5) memoizados
    val lowStockProducts = remember {
        listOf(
            Product("4", "Galletitas", 25.0, 5),
            Product("5", "Gaseosa Cola", 35.0, 2),
            Product("6", "Chocolate Blanco", 80.0, 3),
            Product("7", "Agua Mineral", 20.0, 4)
        )
    }
    
    // Estado para el carrusel automático optimizado
    var carouselTick by remember { mutableStateOf(0) }
    val currentProductIndex by remember {
        derivedStateOf { carouselTick % topProducts.size }
    }
    
    // Efecto para el carrusel automático optimizado
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Cambiar cada 3 segundos
            carouselTick++
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .windowInsetsPadding(WindowInsets.systemBars),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con saludo y notificación memoizado
        item {
            WelcomeHeader(
                userName = userName,
                textColor = textColor
            )
        }
        
        // Cards de ganancias optimizadas
        item {
            EarningsCards(
                monthlyEarnings = monthlyEarnings,
                weeklyEarnings = weeklyEarnings,
                monthlyPercentage = monthlyPercentage,
                weeklyPercentage = weeklyPercentage,
                cardColor = cardColor,
                textColor = textColor
            )
        }
        
        // Producto más vendido (Carrusel)
        item {
            Text(
                text = "Producto Más Vendido",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(topProducts) { product ->
                    Card(
                        modifier = Modifier.width(280.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Placeholder para imagen del producto
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        primaryBlue.copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = product.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor
                                )
                                Text(
                                    text = "${product.stock} vendidos este mes",
                                    fontSize = 14.sp,
                                    color = Color(0xFF8E8E93)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Bajo Stock
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bajo Stock",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                TextButton(
                    onClick = { /* TODO: Navegar a productos */ }
                ) {
                    Text(
                        text = "Ver todo",
                        color = primaryBlue
                    )
                }
            }
        }
        
        // Lista de productos con stock bajo
        items(lowStockProducts) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono del producto
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    when {
                                        product.stock <= 2 -> Color(0xFFFF3B30).copy(alpha = 0.2f)
                                        product.stock <= 4 -> Color(0xFFFF9500).copy(alpha = 0.2f)
                                        else -> primaryBlue.copy(alpha = 0.2f)
                                    },
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = when {
                                    product.stock <= 2 -> Color(0xFFFF3B30)
                                    product.stock <= 4 -> Color(0xFFFF9500)
                                    else -> primaryBlue
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = product.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                            Text(
                                text = "Quedan: ${product.stock}",
                                fontSize = 14.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }
                    }
                    
                    Button(
                        onClick = { /* TODO: Implementar reposición */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Reponer",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// Componentes memoizados para evitar recomposiciones
@Composable
private fun WelcomeHeader(
    userName: String,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Bienvenido de nuevo,",
                fontSize = 16.sp,
                color = Color(0xFF8E8E93)
            )
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        
        Box {
            IconButton(
                onClick = { /* TODO: Implementar notificaciones */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Indicador de notificación
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.Red, RoundedCornerShape(4.dp))
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun EarningsCards(
    monthlyEarnings: Double,
    weeklyEarnings: Double,
    monthlyPercentage: Int,
    weeklyPercentage: Int,
    cardColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ganancias del mes
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Ganancias (Mes)",
                    fontSize = 14.sp,
                    color = Color(0xFF8E8E93)
                )
                Text(
                    text = "$${String.format("%.0f", monthlyEarnings)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "↑ ${monthlyPercentage}%",
                        fontSize = 12.sp,
                        color = Color(0xFF34C759)
                    )
                }
            }
        }
        
        // Ganancias de la semana
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Ganancias (Semana)",
                    fontSize = 14.sp,
                    color = Color(0xFF8E8E93)
                )
                Text(
                    text = "$${String.format("%.0f", weeklyEarnings)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "↓ ${Math.abs(weeklyPercentage)}%",
                        fontSize = 12.sp,
                        color = Color(0xFFFF3B30)
                    )
                }
            }
        }
    }
}