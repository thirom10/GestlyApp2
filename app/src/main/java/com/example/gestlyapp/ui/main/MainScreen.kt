package com.example.gestlyapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestlyapp.ui.feactures.home.HomeScreen
import com.example.gestlyapp.ui.feactures.products.ProductsScreen
import com.example.gestlyapp.ui.feactures.products.ProductViewModel
import com.example.gestlyapp.ui.feactures.products.AddProductScreen
import com.example.gestlyapp.ui.feactures.reports.ReportsScreen
import com.example.gestlyapp.ui.feactures.sell.SellScreen
import com.example.gestlyapp.ui.feactures.user.UserScreen

// Definir las rutas de navegación
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Inicio", Icons.Filled.Home)
    object Products : BottomNavItem("products", "Productos", Icons.Filled.ShoppingCart)
    object Sell : BottomNavItem("sell", "Vender", Icons.Filled.Add)
    object Reports : BottomNavItem("reports", "Reporte", Icons.Filled.Person)
    object User : BottomNavItem("user", "Usuario", Icons.Filled.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    onLogout: () -> Unit = {},
    userName: String = "Usuario"
) {
    val navController = rememberNavController()
    val backgroundColor = Color(0xFF2C2C2C)
    val primaryBlue = Color(0xFF007AFF)
    
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Products,
        BottomNavItem.Sell,
        BottomNavItem.Reports,
        BottomNavItem.User
    )
    
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF1C1C1E),
                contentColor = primaryBlue,
                modifier = Modifier.height(110.dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                items.forEach { item ->
                    if (item.route == "sell") {
                        // Botón especial para "Vender"
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                containerColor = primaryBlue,
                                contentColor = Color.White,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    } else {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = primaryBlue,
                                selectedTextColor = primaryBlue,
                                unselectedIconColor = Color(0xFF8E8E93),
                                unselectedTextColor = Color(0xFF8E8E93),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(userName = userName)
            }
            composable(BottomNavItem.Products.route) {
                ProductsScreen(
                    viewModel = productViewModel,
                    onNavigateToAddProduct = {
                        navController.navigate("add_product")
                    }
                )
            }
            composable("add_product") {
                AddProductScreen(
                    viewModel = productViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(BottomNavItem.Sell.route) {
                SellScreen()
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen()
            }
            composable(BottomNavItem.User.route) {
                UserScreen(onLogout = onLogout)
            }
        }
    }
}