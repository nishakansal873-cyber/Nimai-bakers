package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BakeryRepository {

    private val _products = MutableStateFlow<List<Product>>(getInitialProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedPromo = MutableStateFlow<PromoOffer?>(null)
    val appliedPromo: StateFlow<PromoOffer?> = _appliedPromo.asStateFlow()

    private val _orders = MutableStateFlow<List<BakeryOrder>>(getInitialOrders())
    val orders: StateFlow<List<BakeryOrder>> = _orders.asStateFlow()

    private val _settings = MutableStateFlow(BakerySettings())
    val settings: StateFlow<BakerySettings> = _settings.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Products Management
    fun addProduct(product: Product) {
        _products.value = listOf(product) + _products.value
    }

    fun updateProduct(updated: Product) {
        _products.value = _products.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    fun toggleProductStock(productId: String, newStatus: StockStatus) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stockStatus = newStatus) else it
        }
    }

    // Cart Management
    fun addToCart(product: Product, quantity: Int = 1, customisation: CakeCustomisation? = null) {
        val unitPrice = customisation?.calculatedPrice ?: product.price
        val existing = _cartItems.value.find { 
            it.product.id == product.id && it.customisation == customisation 
        }

        if (existing != null) {
            _cartItems.value = _cartItems.value.map {
                if (it.id == existing.id) it.copy(quantity = it.quantity + quantity) else it
            }
        } else {
            val newItem = CartItem(
                id = UUID.randomUUID().toString(),
                product = product,
                quantity = quantity,
                customisation = customisation,
                unitPrice = unitPrice
            )
            _cartItems.value = _cartItems.value + newItem
        }
    }

    fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
        } else {
            _cartItems.value = _cartItems.value.map {
                if (it.id == cartItemId) it.copy(quantity = newQuantity) else it
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.id == cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedPromo.value = null
    }

    fun applyPromoCode(code: String): String? {
        val normalized = code.trim().uppercase()
        val offer = _settings.value.activeOffers.find { it.code.uppercase() == normalized }
        return if (offer != null) {
            val subtotal = _cartItems.value.sumOf { it.totalPrice }
            if (subtotal >= offer.minOrderValue) {
                _appliedPromo.value = offer
                null // Success
            } else {
                "Minimum order value for ${offer.code} is ₹${offer.minOrderValue.toInt()}"
            }
        } else {
            "Invalid promo code '$code'"
        }
    }

    fun removePromoCode() {
        _appliedPromo.value = null
    }

    // Orders Management
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryType: DeliveryType,
        deliveryAddress: String,
        deliveryDate: String,
        deliveryTimeSlot: String,
        instructions: String,
        paymentMethod: PaymentMethod
    ): BakeryOrder {
        val currentCart = _cartItems.value
        val subtotal = currentCart.sumOf { it.totalPrice }
        val promo = _appliedPromo.value
        val discount = if (promo != null) (subtotal * promo.discountPercent / 100.0) else 0.0
        val deliveryFee = if (deliveryType == DeliveryType.HOME_DELIVERY) {
            if (subtotal >= _settings.value.freeDeliveryThreshold) 0.0 else _settings.value.deliveryFee
        } else 0.0
        val packagingFee = _settings.value.packagingFee
        val grandTotal = (subtotal - discount + deliveryFee + packagingFee).coerceAtLeast(0.0)

        val randomIdSuffix = (1000..9999).random()
        val newOrder = BakeryOrder(
            orderId = "#NB-$randomIdSuffix",
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryType = deliveryType,
            deliveryAddress = if (deliveryType == DeliveryType.HOME_DELIVERY) deliveryAddress else "Store Pickup at Nimai Bakery, CP",
            deliveryDate = deliveryDate,
            deliveryTimeSlot = deliveryTimeSlot,
            instructions = instructions,
            items = currentCart,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            packagingFee = packagingFee,
            discount = discount,
            promoCodeApplied = promo?.code,
            total = grandTotal,
            paymentMethod = paymentMethod,
            isDemoOrder = true, // Clear demo indication
            status = OrderStatus.RECEIVED,
            timestamp = System.currentTimeMillis(),
            estimatedArrival = "$deliveryDate, $deliveryTimeSlot"
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.value = _orders.value.map {
            if (it.orderId == orderId) it.copy(status = newStatus) else it
        }
    }

    // Bakery Owner Settings
    fun updateSettings(newSettings: BakerySettings) {
        _settings.value = newSettings
    }

    fun verifyAdminPin(pin: String): Boolean {
        // Standard admin PIN for demo/bakery owner
        val isValid = pin.trim() == "1234"
        if (isValid) {
            _isAdminLoggedIn.value = true
        }
        return isValid
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    companion object {
        private fun getInitialProducts(): List<Product> = listOf(
            Product(
                id = "prod_choc_truffle",
                name = "Belgian Dark Chocolate Cake",
                category = BakeryCategory.CAKES,
                price = 550.0,
                isEggless = true,
                shortDesc = "Rich 70% Belgian chocolate ganache with moist sponge.",
                description = "Our signature cake crafted with single-origin 70% dark Belgian cocoa, layered with silky chocolate mousse and finished with handmade chocolate curls.",
                ingredients = listOf("Belgian Dark Chocolate 70%", "Flour", "Pure Butter", "Brown Sugar", "Fresh Cream", "Vanilla Extract"),
                allergens = listOf("Dairy", "Gluten", "May contain traces of tree nuts"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                isCustomisableCake = true,
                rating = 4.9,
                reviewCount = 86,
                prepTimeHours = 3
            ),
            Product(
                id = "prod_red_velvet",
                name = "Classic Red Velvet Cake",
                category = BakeryCategory.CAKES,
                price = 650.0,
                isEggless = false,
                shortDesc = "Velvety cocoa crumb paired with Philadelphia cream cheese.",
                description = "Silky textured buttermilk sponge layered generously with authentic Philadelphia cream cheese frosting, topped with ruby red velvet crumbs.",
                ingredients = listOf("Buttermilk", "Philadelphia Cream Cheese", "Cocoa Powder", "Pure Butter", "Eggs", "Madagascar Vanilla"),
                allergens = listOf("Dairy", "Gluten", "Egg"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                isCustomisableCake = true,
                rating = 4.8,
                reviewCount = 64,
                prepTimeHours = 4
            ),
            Product(
                id = "prod_pineapple",
                name = "Fresh Pineapple Cake",
                category = BakeryCategory.CAKES,
                price = 450.0,
                isEggless = true,
                shortDesc = "Juicy tropical pineapple compote with light vanilla cream.",
                description = "Delicate vanilla sponge infused with fresh pineapple crush, layered with hand-whipped dairy cream and finished with candied pineapple petals and cherries.",
                ingredients = listOf("Fresh Pineapple", "Light Dairy Cream", "Vanilla Sponge", "Raw Cane Sugar", "Cherry Glaze"),
                allergens = listOf("Dairy", "Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                isCustomisableCake = true,
                rating = 4.7,
                reviewCount = 52,
                prepTimeHours = 3
            ),
            Product(
                id = "prod_eggless_vanilla",
                name = "Eggless Vanilla Blossom Cake",
                category = BakeryCategory.CAKES,
                price = 480.0,
                isEggless = true,
                shortDesc = "Pure vanilla bean sponge with fluffy buttercream florals.",
                description = "100% vegetarian artisan celebration cake infused with natural Bourbon vanilla beans and adorned with pastel buttercream floral piping.",
                ingredients = listOf("Condensed Milk", "Pure Butter", "Bourbon Vanilla Pods", "Wheat Flour", "Raw Sugar"),
                allergens = listOf("Dairy", "Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = false,
                isCustomisableCake = true,
                rating = 4.8,
                reviewCount = 41,
                prepTimeHours = 3
            ),
            Product(
                id = "prod_cupcake_box",
                name = "Pink Berry Cupcake Box (4 Pcs)",
                category = BakeryCategory.CUPCAKES,
                price = 320.0,
                isEggless = true,
                shortDesc = "Assorted strawberry frosting & vanilla cupcakes.",
                description = "A delightful box of 4 gourmet cupcakes featuring strawberry swirl, Madagascar vanilla, chocolate ganache, and salted caramel toppings.",
                ingredients = listOf("Flour", "Strawberry Coulis", "Buttercream", "Sugar", "Milk"),
                allergens = listOf("Dairy", "Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                rating = 4.9,
                reviewCount = 95,
                prepTimeHours = 2
            ),
            Product(
                id = "prod_choc_cupcake",
                name = "Belgian Chocolate Cupcakes (4 Pcs)",
                category = BakeryCategory.CUPCAKES,
                price = 360.0,
                isEggless = false,
                shortDesc = "Deep cocoa sponge with molten dark chocolate center.",
                description = "Decadent dark chocolate cupcakes piped with truffle fudge frosting and dusted with 24k gold chocolate flakes.",
                ingredients = listOf("Belgian Chocolate", "Whole Eggs", "Dairy Cream", "Wheat Flour"),
                allergens = listOf("Dairy", "Gluten", "Egg"),
                stockStatus = StockStatus.IN_STOCK,
                rating = 4.8,
                reviewCount = 38,
                prepTimeHours = 2
            ),
            Product(
                id = "prod_croissant",
                name = "French Butter Croissant",
                category = BakeryCategory.PASTRIES,
                price = 140.0,
                isEggless = false,
                shortDesc = "Laminated 27 flaky layers with Normandy butter.",
                description = "Traditional French Viennoiserie baked at dawn. Crisp, shatteringly flaky crust giving way to an airy, honeycomb butter interior.",
                ingredients = listOf("French Unsalted Butter", "Flour", "Yeast", "Sea Salt", "Egg Wash"),
                allergens = listOf("Dairy", "Gluten", "Egg"),
                stockStatus = StockStatus.LOW_STOCK,
                isBestseller = true,
                rating = 4.9,
                reviewCount = 112,
                prepTimeHours = 1
            ),
            Product(
                id = "prod_eclair",
                name = "Dark Chocolate Eclair",
                category = BakeryCategory.PASTRIES,
                price = 180.0,
                isEggless = false,
                shortDesc = "Choux pastry filled with vanilla bean diplomat cream.",
                description = "Crisp choux bun generously piped with silky diplomat cream and dipped in glossy dark chocolate ganache glaze.",
                ingredients = listOf("Choux Pastry", "Custard", "Fresh Cream", "Dark Chocolate"),
                allergens = listOf("Dairy", "Gluten", "Egg"),
                stockStatus = StockStatus.IN_STOCK,
                rating = 4.7,
                reviewCount = 29,
                prepTimeHours = 2
            ),
            Product(
                id = "prod_cookies_jar",
                name = "Choco-Chip Butter Cookies",
                category = BakeryCategory.COOKIES,
                price = 220.0,
                isEggless = true,
                shortDesc = "Crisp golden edges, soft buttery center, 250g Jar.",
                description = "Baked to golden perfection with loaded semi-sweet chocolate morsels and creamy country butter in an airtight keepsake jar.",
                ingredients = listOf("Country Butter", "Brown Sugar", "Dark Choco Chips", "Wheat Flour"),
                allergens = listOf("Dairy", "Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                rating = 4.9,
                reviewCount = 140,
                prepTimeHours = 1
            ),
            Product(
                id = "prod_biscotti",
                name = "Almond Pistachio Biscotti",
                category = BakeryCategory.COOKIES,
                price = 260.0,
                isEggless = false,
                shortDesc = "Twice-baked Tuscan Italian biscotti with roasted nuts (200g).",
                description = "Crunchy twice-baked artisan cantucci studded with California almonds, Iranian pistachios, and a whisper of orange zest. Perfect with coffee.",
                ingredients = listOf("Roasted Almonds", "Pistachios", "Flour", "Eggs", "Orange Zest"),
                allergens = listOf("Tree Nuts", "Gluten", "Egg"),
                stockStatus = StockStatus.IN_STOCK,
                rating = 4.8,
                reviewCount = 47,
                prepTimeHours = 1
            ),
            Product(
                id = "prod_sourdough",
                name = "Artisan Sourdough Country Loaf",
                category = BakeryCategory.BREADS,
                price = 190.0,
                isEggless = true,
                shortDesc = "Naturally fermented 36-hr sourdough with blistered crust.",
                description = "Crafted with wild yeast starter nurtured for 5 years. Crisp caramelized crust, open airy crumb, and a pleasant gentle tang.",
                ingredients = listOf("Stoneground Whole Wheat Flour", "Spring Water", "Natural Sourdough Starter", "Sea Salt"),
                allergens = listOf("Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                rating = 4.9,
                reviewCount = 78,
                prepTimeHours = 2
            ),
            Product(
                id = "prod_focaccia",
                name = "Rosemary Garlic Focaccia",
                category = BakeryCategory.BREADS,
                price = 160.0,
                isEggless = true,
                shortDesc = "Extra virgin olive oil soaked bread with sea salt & herbs.",
                description = "Dimpled, pillowy Italian flatbread lavishly baked with cold-pressed olive oil, roasted garlic cloves, fresh garden rosemary, and Maldon sea salt flakes.",
                ingredients = listOf("Flour", "Extra Virgin Olive Oil", "Fresh Rosemary", "Roasted Garlic", "Flaky Sea Salt"),
                allergens = listOf("Gluten"),
                stockStatus = StockStatus.IN_STOCK,
                rating = 4.8,
                reviewCount = 35,
                prepTimeHours = 2
            ),
            Product(
                id = "prod_hamper",
                name = "Royal Celebration Hamper",
                category = BakeryCategory.HAMPERS,
                price = 1250.0,
                isEggless = true,
                shortDesc = "Luxury woven basket with cake jar, cookies, sourdough & jam.",
                description = "The ultimate gift box: includes 1 Belgian Truffle Cake Jar, 1 Choco-Chip Cookie Jar, 1 Artisan Sourdough Loaf, 1 Strawberry Conserve, and a handmade greeting card in a keepsake ribbon basket.",
                ingredients = listOf("Assorted artisan treats, jams and butter cookies"),
                allergens = listOf("Dairy", "Gluten", "May contain nuts"),
                stockStatus = StockStatus.IN_STOCK,
                isBestseller = true,
                rating = 5.0,
                reviewCount = 59,
                prepTimeHours = 4
            )
        )

        private fun getInitialOrders(): List<BakeryOrder> = listOf(
            BakeryOrder(
                orderId = "#NB-8421",
                customerName = "Rhea Sharma",
                customerPhone = "+91 98112 34567",
                deliveryType = DeliveryType.HOME_DELIVERY,
                deliveryAddress = "Flat 402, Royal Palms, Sector 18, CP",
                deliveryDate = "Today",
                deliveryTimeSlot = "4:00 PM - 7:00 PM",
                instructions = "Please send fresh birthday candle and ensure eggless tag.",
                items = listOf(
                    CartItem(
                        id = "cart_item_demo_1",
                        product = getInitialProducts()[1], // Red Velvet
                        quantity = 1,
                        customisation = CakeCustomisation(
                            flavour = "Classic Red Velvet Cream Cheese",
                            weightKg = 1.0,
                            isEggless = true,
                            messageOnCake = "Happy 25th Birthday Rhea! 🎂",
                            designStyle = "Minimalist Floral",
                            specialInstructions = "Less sugar in frosting, extra piping",
                            includeCandleAndKnife = true,
                            calculatedPrice = 1200.0
                        ),
                        unitPrice = 1200.0
                    )
                ),
                subtotal = 1200.0,
                deliveryFee = 0.0, // Free above 600
                packagingFee = 25.0,
                discount = 180.0, // SWEET15
                promoCodeApplied = "SWEET15",
                total = 1045.0,
                paymentMethod = PaymentMethod.UPI,
                isDemoOrder = true,
                status = OrderStatus.BAKING,
                timestamp = System.currentTimeMillis() - 7200000,
                estimatedArrival = "Today, 5:30 PM"
            ),
            BakeryOrder(
                orderId = "#NB-8418",
                customerName = "Vikram Malhotra",
                customerPhone = "+91 98711 99882",
                deliveryType = DeliveryType.HOME_DELIVERY,
                deliveryAddress = "House 12, Golf Course Road, DLF Phase 5",
                deliveryDate = "Today",
                deliveryTimeSlot = "1:00 PM - 4:00 PM",
                instructions = "Ring doorbell twice, please handle with care.",
                items = listOf(
                    CartItem(
                        id = "cart_item_demo_2",
                        product = getInitialProducts()[4], // Cupcakes
                        quantity = 2,
                        unitPrice = 320.0
                    ),
                    CartItem(
                        id = "cart_item_demo_3",
                        product = getInitialProducts()[6], // Croissant
                        quantity = 3,
                        unitPrice = 140.0
                    )
                ),
                subtotal = 1060.0,
                deliveryFee = 0.0,
                packagingFee = 25.0,
                discount = 106.0,
                promoCodeApplied = "NIMAI10",
                total = 979.0,
                paymentMethod = PaymentMethod.CARD,
                isDemoOrder = true,
                status = OrderStatus.READY,
                timestamp = System.currentTimeMillis() - 14400000,
                estimatedArrival = "Today, 2:45 PM"
            )
        )
    }
}
