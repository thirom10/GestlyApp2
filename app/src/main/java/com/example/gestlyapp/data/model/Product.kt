package com.example.gestlyapp.data.model

import java.util.Date

data class Product(
    val id: String = "",
    val name: String = "",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val stock: Int = 0,
    val netWeight: Double = 0.0,
    val weightUnit: WeightUnit = WeightUnit.MG,
    val branch: String = "",
    val purchaseDate: Date? = null,
    val uploadDate: Date = Date(),
    val editDate: Date = Date()
)

enum class WeightUnit(val displayName: String) {
    MG("mg"),
    ML("ml")
}