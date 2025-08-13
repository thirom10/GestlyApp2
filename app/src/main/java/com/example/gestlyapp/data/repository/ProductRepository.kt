package com.example.gestlyapp.data.repository

import com.example.gestlyapp.data.datasource.ProductDataSource
import com.example.gestlyapp.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dataSource: ProductDataSource
) {
    suspend fun addProduct(product: Product): Result<String> {
        return dataSource.addProduct(product)
    }
    
    suspend fun getProducts(): Flow<List<Product>> {
        return dataSource.getProducts()
    }
    
    suspend fun updateProduct(product: Product): Result<Unit> {
        return dataSource.updateProduct(product)
    }
    
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return dataSource.deleteProduct(productId)
    }
    
    suspend fun searchProducts(query: String): Flow<List<Product>> {
        return dataSource.searchProducts(query)
    }
}