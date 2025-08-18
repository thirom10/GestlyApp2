package com.example.gestlyapp.data.datasource

import com.example.gestlyapp.data.model.Product
import com.example.gestlyapp.data.model.WeightUnit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseProductDataSource : ProductDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")
    
    override suspend fun addProduct(product: Product): Result<String> {
        return try {
            val productData = mapOf(
                "name" to product.name,
                "purchasePrice" to product.purchasePrice,
                "sellingPrice" to product.sellingPrice,
                "stock" to product.stock,
                "netWeight" to product.netWeight,
                "weightUnit" to product.weightUnit.name,
                "branch" to product.branch,
                "purchaseDate" to product.purchaseDate,
                "uploadDate" to Date(),
                "editDate" to Date()
            )
            
            val documentRef = productsCollection.add(productData).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .orderBy("uploadDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val products = snapshot?.documents?.mapNotNull { document ->
                    try {
                        Product(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            purchasePrice = document.getDouble("purchasePrice") ?: 0.0,
                            sellingPrice = document.getDouble("sellingPrice") ?: 0.0,
                            stock = document.getLong("stock")?.toInt() ?: 0,
                            netWeight = (document.getDouble("netWeight") ?: 0.0).toFloat(),
                            weightUnit = WeightUnit.valueOf(document.getString("weightUnit") ?: "MG"),
                            branch = document.getString("branch") ?: "",
                            purchaseDate = document.getDate("purchaseDate") as Long?,
                            uploadDate = document.getDate("uploadDate") ?: Date(),
                            editDate = document.getDate("editDate") ?: Date()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(products)
            }
        
        awaitClose { listener.remove() }
    }
    
    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val productData = mapOf(
                "name" to product.name,
                "purchasePrice" to product.purchasePrice,
                "sellingPrice" to product.sellingPrice,
                "stock" to product.stock,
                "netWeight" to product.netWeight,
                "weightUnit" to product.weightUnit.name,
                "branch" to product.branch,
                "purchaseDate" to product.purchaseDate,
                "editDate" to Date()
            )
            
            productsCollection.document(product.id).update(productData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchProducts(query: String): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val products = snapshot?.documents?.mapNotNull { document ->
                    try {
                        Product(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            purchasePrice = document.getDouble("purchasePrice") ?: 0.0,
                            sellingPrice = document.getDouble("sellingPrice") ?: 0.0,
                            stock = document.getLong("stock")?.toInt() ?: 0,
                            netWeight = document.getDouble("netWeight") ?: 0.0,
                            weightUnit = WeightUnit.valueOf(document.getString("weightUnit") ?: "MG"),
                            branch = document.getString("branch") ?: "",
                            purchaseDate = document.getDate("purchaseDate"),
                            uploadDate = document.getDate("uploadDate") ?: Date(),
                            editDate = document.getDate("editDate") ?: Date()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(products)
            }
        
        awaitClose { listener.remove() }
    }
}