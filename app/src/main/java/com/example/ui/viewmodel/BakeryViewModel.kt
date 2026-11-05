package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.BakeryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen(val label: String) {
    HOME("Home"),
    MENU("Menu"),
    CUSTOM_CAKE("Custom Cake"),
    CART("Cart"),
    ORDERS("My Orders"),
    CONTACT("Contact"),
    ADMIN("Owner")
}

data class CustomCakeState(
    val baseProduct: Product? = null,
    val flavour: String = "Belgian Dark Chocolate Truffle",
    val weightKg: Double = 1.0,
    val isEggless: Boolean = true,
    val messageOnCake: String = "Happy Birthday!",
    val designStyle: String = "Classic Floral Buttercream",
    val specialInstructions: String = "",
    val includeCandleAndKnife: Boolean = true
) {
    val calculatedPrice: Double
        get() {
            val baseRatePerKg = when (flavour) {
                "Classic Red Velvet Cream Cheese" -> 1100.0
                "Belgian Dark Chocolate Truffle" -> 1000.0
                "Fresh Alphonso Mango Supreme" -> 950.0
                "Eggless Vanilla Bean & Berries" -> 900.0
                "Fresh Pineapple Blossom" -> 850.0
                "Butterscotch Caramel Crunch" -> 850.0
                else -> 900.0
            }
            val weightMultiplier = when (weightKg) {
                0.5 -> 0.55
                1.0 -> 1.0
                1.5 -> 1.45
                2.0 -> 1.9
                else -> weightKg
            }
            val egglessAdjustment = if (isEggless) 0.0 else 0.0 // no extra charge, pure choice
            return (baseRatePerKg * weightMultiplier) + egglessAdjustment
        }
}

class BakeryViewModel(
    val repository: BakeryRepository = BakeryRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    val products: StateFlow<List<Product>> = repository.products
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val appliedPromo: StateFlow<PromoOffer?> = repository.appliedPromo
    val orders: StateFlow<List<BakeryOrder>> = repository.orders
    val settings: StateFlow<BakerySettings> = repository.settings
    val isAdminLoggedIn: StateFlow<Boolean> = repository.isAdminLoggedIn

    // Menu filters
    private val _selectedCategory = MutableStateFlow<BakeryCategory?>(null)
    val selectedCategory: StateFlow<BakeryCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _egglessOnlyFilter = MutableStateFlow(false)
    val egglessOnlyFilter: StateFlow<Boolean> = _egglessOnlyFilter.asStateFlow()

    // Selected product for detail dialog
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Custom cake builder state
    private val _customCakeState = MutableStateFlow(CustomCakeState())
    val customCakeState: StateFlow<CustomCakeState> = _customCakeState.asStateFlow()

    // Selected order for tracking detail
    private val _trackedOrderId = MutableStateFlow<String?>(null)
    val trackedOrderId: StateFlow<String?> = _trackedOrderId.asStateFlow()

    // Snackbar / Alert message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Admin login dialog state
    private val _showAdminLogin = MutableStateFlow(false)
    val showAdminLogin: StateFlow<Boolean> = _showAdminLogin.asStateFlow()

    private val _adminPinInput = MutableStateFlow("")
    val adminPinInput: StateFlow<String> = _adminPinInput.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    // Show checkout modal
    private val _showCheckout = MutableStateFlow(false)
    val showCheckout: StateFlow<Boolean> = _showCheckout.asStateFlow()

    // Filtered products flow
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        _selectedCategory,
        _searchQuery,
        _egglessOnlyFilter
    ) { allProducts, cat, query, egglessOnly ->
        allProducts.filter { product ->
            val matchesCategory = cat == null || product.category == cat
            val matchesQuery = query.isBlank() || 
                product.name.contains(query, ignoreCase = true) || 
                product.shortDesc.contains(query, ignoreCase = true) ||
                product.ingredients.any { it.contains(query, ignoreCase = true) }
            val matchesEggless = !egglessOnly || product.isEggless
            matchesCategory && matchesQuery && matchesEggless
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cartCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val cartSubtotal: StateFlow<Double> = cartItems.map { list ->
        list.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectCategory(category: BakeryCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setEgglessFilter(egglessOnly: Boolean) {
        _egglessOnlyFilter.value = egglessOnly
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
    }

    fun closeProductDetail() {
        _selectedProduct.value = null
    }

    fun addToCart(product: Product, quantity: Int = 1, customisation: CakeCustomisation? = null) {
        repository.addToCart(product, quantity, customisation)
        _userMessage.value = "Added '${product.name}' to cart! 🍰"
    }

    fun updateCartQuantity(cartItemId: String, newQty: Int) {
        repository.updateCartQuantity(cartItemId, newQty)
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
        _userMessage.value = "Item removed from cart"
    }

    fun applyPromoCode(code: String) {
        val error = repository.applyPromoCode(code)
        if (error != null) {
            _userMessage.value = error
        } else {
            _userMessage.value = "Promo code '$code' applied! 🎉"
        }
    }

    fun removePromoCode() {
        repository.removePromoCode()
        _userMessage.value = "Promo code removed"
    }

    // Custom cake actions
    fun startCustomisingCake(baseProduct: Product? = null) {
        _customCakeState.value = CustomCakeState(
            baseProduct = baseProduct,
            flavour = if (baseProduct != null && baseProduct.category == BakeryCategory.CAKES) baseProduct.name else "Belgian Dark Chocolate Truffle",
            isEggless = baseProduct?.isEggless ?: true
        )
        _currentScreen.value = AppScreen.CUSTOM_CAKE
    }

    fun updateCustomCakeFlavour(flavour: String) {
        _customCakeState.value = _customCakeState.value.copy(flavour = flavour)
    }

    fun updateCustomCakeWeight(weightKg: Double) {
        _customCakeState.value = _customCakeState.value.copy(weightKg = weightKg)
    }

    fun updateCustomCakeEggless(isEggless: Boolean) {
        _customCakeState.value = _customCakeState.value.copy(isEggless = isEggless)
    }

    fun updateCustomCakeMessage(message: String) {
        _customCakeState.value = _customCakeState.value.copy(messageOnCake = message)
    }

    fun updateCustomCakeDesign(designStyle: String) {
        _customCakeState.value = _customCakeState.value.copy(designStyle = designStyle)
    }

    fun updateCustomCakeInstructions(instructions: String) {
        _customCakeState.value = _customCakeState.value.copy(specialInstructions = instructions)
    }

    fun updateCustomCakeCandleKnife(include: Boolean) {
        _customCakeState.value = _customCakeState.value.copy(includeCandleAndKnife = include)
    }

    fun addCustomCakeToCart() {
        val state = _customCakeState.value
        val product = state.baseProduct ?: repository.products.value.first { it.category == BakeryCategory.CAKES }
        val customisation = CakeCustomisation(
            flavour = state.flavour,
            weightKg = state.weightKg,
            isEggless = state.isEggless,
            messageOnCake = state.messageOnCake.trim(),
            designStyle = state.designStyle,
            specialInstructions = state.specialInstructions.trim(),
            includeCandleAndKnife = state.includeCandleAndKnife,
            calculatedPrice = state.calculatedPrice
        )
        repository.addToCart(product, quantity = 1, customisation = customisation)
        _userMessage.value = "Custom cake added to cart! (₹${state.calculatedPrice.toInt()})"
        _currentScreen.value = AppScreen.CART
    }

    fun openCheckout() {
        if (cartItems.value.isEmpty()) {
            _userMessage.value = "Your cart is empty!"
            return
        }
        _showCheckout.value = true
    }

    fun closeCheckout() {
        _showCheckout.value = false
    }

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
        val order = repository.placeOrder(
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryType = deliveryType,
            deliveryAddress = deliveryAddress,
            deliveryDate = deliveryDate,
            deliveryTimeSlot = deliveryTimeSlot,
            instructions = instructions,
            paymentMethod = paymentMethod
        )
        _showCheckout.value = false
        _trackedOrderId.value = order.orderId
        _currentScreen.value = AppScreen.ORDERS
        _userMessage.value = "Demo Order placed: ${order.orderId} (Unprocessed Demo)"
        return order
    }

    fun selectTrackedOrder(orderId: String) {
        _trackedOrderId.value = orderId
    }

    // Admin
    fun openAdminLogin() {
        _adminPinInput.value = ""
        _adminLoginError.value = null
        _showAdminLogin.value = true
    }

    fun closeAdminLogin() {
        _showAdminLogin.value = false
    }

    fun setAdminPin(pin: String) {
        _adminPinInput.value = pin
    }

    fun submitAdminPin() {
        val success = repository.verifyAdminPin(_adminPinInput.value)
        if (success) {
            _showAdminLogin.value = false
            _adminLoginError.value = null
            _currentScreen.value = AppScreen.ADMIN
            _userMessage.value = "Welcome, Bakery Manager! 👑"
        } else {
            _adminLoginError.value = "Incorrect PIN. Default demo PIN is 1234"
        }
    }

    fun logoutAdmin() {
        repository.logoutAdmin()
        _currentScreen.value = AppScreen.HOME
        _userMessage.value = "Logged out from Owner Dashboard"
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.updateOrderStatus(orderId, newStatus)
        _userMessage.value = "Order $orderId updated to ${newStatus.title}"
    }

    fun updateProductStock(productId: String, status: StockStatus) {
        repository.toggleProductStock(productId, status)
        _userMessage.value = "Product stock updated"
    }

    fun saveProduct(product: Product, isNew: Boolean) {
        if (isNew) {
            repository.addProduct(product)
            _userMessage.value = "New product '${product.name}' added! 🥐"
        } else {
            repository.updateProduct(product)
            _userMessage.value = "Product '${product.name}' updated!"
        }
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId)
        _userMessage.value = "Product removed from bakery"
    }

    fun updateSettings(newSettings: BakerySettings) {
        repository.updateSettings(newSettings)
        _userMessage.value = "Bakery settings saved successfully"
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
