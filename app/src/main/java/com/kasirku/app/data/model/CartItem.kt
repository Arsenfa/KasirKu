package com.kasirku.app.data.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    var note: String = "",
    var discountPercent: Double = 0.0
) {
    val discountAmount: Double get() = product.price * quantity * discountPercent / 100
    val totalPrice: Double get() = (product.price * quantity) - discountAmount
}
