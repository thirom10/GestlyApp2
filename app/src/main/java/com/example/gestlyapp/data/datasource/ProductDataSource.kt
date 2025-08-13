package com.example.gestlyapp.data.datasource

import com.example.gestlyapp.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductDataSource {
    suspend fun addProduct(product: Product): Result<String>
    suspend fun getProducts(): Flow<List<Product>>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun searchProducts(query: String): Flow<List<Product>>
}