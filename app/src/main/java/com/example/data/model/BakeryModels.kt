package com.example.data.model

enum class BakeryCategory(val displayName: String, val iconEmoji: String) {
    CAKES("Cakes", "🎂"),
    CUPCAKES("Cupcakes", "🧁"),
    PASTRIES("Pastries", "🥐"),
    COOKIES("Cookies", "🍪"),
    BREADS("Breads", "🍞"),
    HAMPERS("Celebration Hampers", "🎁")
}

enum class StockStatus(val label: String) {
    IN_STOCK("In Stock"),
    LOW_STOCK("Few Left"),
    OUT_OF_STOCK("Sold Out")
}

data class Product(
    val id: String,
    val name: String,
    val category: BakeryCategory,
    val price: Double, // in Indian Rupees (₹)
    val isEggless: Boolean,
    val shortDesc: String,
    val description: String,
    val ingredients: List<String>,
    val allergens: List<String>,
    val stockStatus: StockStatus,
    val isBestseller: Boolean = false,
    val weightOptions: List<String> = listOf("0.5 kg", "1.0 kg", "1.5 kg"),
    val isCustomisableCake: Boolean = false,
    val rating: Double = 4.8,
    val reviewCount: Int = 34,
    val prepTimeHours: Int = 2
)

data class CakeCustomisation(
    val flavour: String,
    val weightKg: Double,
    val isEggless: Boolean,
    val messageOnCake: String,
    val designStyle: String,
    val specialInstructions: String,
    val includeCandleAndKnife: Boolean = true,
    val calculatedPrice: Double
)

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int = 1,
    val customisation: CakeCustomisation? = null,
    val unitPrice: Double
) {
    val totalPrice: Double
        get() = unitPrice * quantity
}

enum class DeliveryType(val label: String) {
    HOME_DELIVERY("Home Delivery 🛵"),
    STORE_PICKUP("Store Pickup 🏬")
}

enum class PaymentMethod(val label: String, val desc: String) {
    UPI("UPI (GPay / PhonePe / Paytm)", "Fast & contactless instant payment"),
    CARD("Credit / Debit Card", "Visa, Mastercard, RuPay"),
    CASH_ON_DELIVERY("Cash on Delivery / Pickup", "Pay when your fresh bake arrives")
}

enum class OrderStatus(val title: String, val description: String, val stepIndex: Int) {
    RECEIVED("Received", "We have received your order request", 0),
    CONFIRMED("Confirmed", "Bakery team reviewed & scheduled your order", 1),
    BAKING("Baking in Oven", "Master chefs are mixing & baking your delicacies", 2),
    READY("Ready & Packed", "Carefully packed with temperature-safe seal", 3),
    OUT_FOR_DELIVERY("Out for Delivery", "Our delivery partner is on the way to you", 4),
    COMPLETED("Delivered", "Delivered fresh to your doorstep! Enjoy", 5),
    CANCELLED("Cancelled", "Order was cancelled", -1)
}

data class BakeryOrder(
    val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryType: DeliveryType,
    val deliveryAddress: String,
    val deliveryDate: String,
    val deliveryTimeSlot: String,
    val instructions: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val packagingFee: Double,
    val discount: Double,
    val promoCodeApplied: String?,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val isDemoOrder: Boolean = true, // Clearly labels demo state when live gateway isn't connected
    val status: OrderStatus = OrderStatus.RECEIVED,
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedArrival: String = "Today, 5:30 PM"
)

data class PromoOffer(
    val code: String,
    val discountPercent: Int,
    val description: String,
    val minOrderValue: Double
)

data class BakerySettings(
    val storeName: String = "Nimai Bakery",
    val tagline: String = "Freshly baked, lovingly made.",
    val storeAddress: String = "Shop 14, Heritage Arcade, Sector 18, Connaught Place, New Delhi 110001",
    val storePhone: String = "+91 98765 43210",
    val storeWhatsApp: String = "+919876543210",
    val openingHours: String = "Mon - Sun: 8:00 AM – 10:30 PM",
    val deliveryFee: Double = 50.0,
    val freeDeliveryThreshold: Double = 600.0,
    val packagingFee: Double = 25.0,
    val minStandardPrepTimeHours: Int = 2,
    val minCustomCakePrepHours: Int = 12,
    val deliveryAreas: List<String> = listOf(
        "Connaught Place & Central Delhi (0 - 5 km)",
        "Sector 14 - 25 & Civil Lines (5 - 10 km)",
        "Golf Course Road & DLF Phase 1-5 (10 - 18 km)",
        "South Extension & Greater Kailash (10 - 15 km)"
    ),
    val availableTimeSlots: List<String> = listOf(
        "10:00 AM - 1:00 PM",
        "1:00 PM - 4:00 PM",
        "4:00 PM - 7:00 PM",
        "7:00 PM - 9:30 PM"
    ),
    val isUpiEnabled: Boolean = true,
    val isCardEnabled: Boolean = true,
    val isCodEnabled: Boolean = true,
    val activeOffers: List<PromoOffer> = listOf(
        PromoOffer("SWEET15", 15, "15% off on all cake & celebration orders above ₹499", 499.0),
        PromoOffer("NIMAI10", 10, "10% off for first-time bakery orders above ₹299", 299.0),
        PromoOffer("FESTIVE20", 20, "20% off on Celebration Hampers & bulk treats", 999.0)
    )
)
