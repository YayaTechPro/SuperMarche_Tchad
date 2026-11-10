package com.supermarche.tchad

// The three possible stock situations of a product
enum class StockStatus { IN_STOCK, LOW_STOCK, OUT_OF_STOCK }

// One row of the "products" table
data class Product(
    val id: Int = 0,
    val name: String,
    val category: String,   // category KEY (e.g. "Food"), translated only when displayed
    val price: Double,      // price in FCFA, stored as a number
    val quantity: Int,
    val minimumStock: Int
) {
    // Out of stock: quantity is 0. Low stock: quantity <= minimum (but > 0).
    fun status(): StockStatus = when {
        quantity <= 0 -> StockStatus.OUT_OF_STOCK
        quantity <= minimumStock -> StockStatus.LOW_STOCK
        else -> StockStatus.IN_STOCK
    }
}
