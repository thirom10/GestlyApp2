package com.example.gestlyapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class Product(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val stock: Int = 0,
    val netWeight: Float = 0.0f, // Cambiado a Float para mejor rendimiento
    val weightUnit: WeightUnit = WeightUnit.MG,
    val branch: String = "",
    val purchaseDate: Long? = null, // Timestamp en lugar de Date
    val uploadDate: Long = System.currentTimeMillis(),
    val editDate: Long = System.currentTimeMillis()
) {
    // Propiedades calculadas para mejor rendimiento
    val profit: Double get() = sellingPrice - purchasePrice
    val profitMargin: Double get() = if (purchasePrice > 0) (profit / purchasePrice) * 100 else 0.0
    val isLowStock: Boolean get() = stock < 5
    val stockStatus: StockStatus get() = when {
        stock <= 0 -> StockStatus.OUT_OF_STOCK
        stock <= 2 -> StockStatus.CRITICAL
        stock <= 5 -> StockStatus.LOW
        else -> StockStatus.NORMAL
    }
}

enum class WeightUnit(val displayName: String) {
    MG("mg"),
    ML("ml")
}

enum class StockStatus {
    OUT_OF_STOCK,
    CRITICAL,
    LOW,
    NORMAL
}