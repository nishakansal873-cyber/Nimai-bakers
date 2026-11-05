package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.VegEggBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.BakeryViewModel
import java.util.UUID

enum class AdminTab(val title: String, val icon: String) {
    ORDERS("Orders", "📦"),
    PRODUCTS("Products", "🧁"),
    SETTINGS("Settings", "⚙️"),
    ANALYTICS("Analytics", "📊")
}

@Composable
fun AdminDashboardScreen(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    if (!isAdminLoggedIn) {
        AdminLoginView(viewModel = viewModel, modifier = modifier)
    } else {
        AdminDashboardContent(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
fun AdminLoginView(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, WarmBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🔐", fontSize = 28.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Bakery Owner Dashboard",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Sign in with administrator PIN",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6) {
                            pin = it
                            errorText = null
                        }
                    },
                    label = { Text("Enter 4-Digit Owner PIN") },
                    placeholder = { Text("1234") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorText!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Helpful demo hint
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PastelPinkLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Demo PIN: 1234",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val valid = viewModel.repository.verifyAdminPin(pin)
                        if (!valid) {
                            errorText = "Invalid PIN. Please enter '1234'"
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Text("Unlock Dashboard 👑", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardContent(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AdminTab.ORDERS) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var isNewProductDialog by remember { mutableStateOf(false) }

    val orders by viewModel.orders.collectAsState()
    val products by viewModel.products.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👑", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Owner Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Nimai Bakery Admin",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { viewModel.logoutAdmin() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PastelPinkLight,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Logout", fontSize = 12.sp)
                }
            }
        }

        // Tab Selector Bar
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            AdminTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = "${tab.icon} ${tab.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                AdminTab.ORDERS -> AdminOrdersTab(
                    orders = orders,
                    onUpdateStatus = { id, status -> viewModel.updateOrderStatus(id, status) }
                )
                AdminTab.PRODUCTS -> AdminProductsTab(
                    products = products,
                    onToggleStock = { id, status -> viewModel.updateProductStock(id, status) },
                    onEditProduct = { editingProduct = it; isNewProductDialog = false },
                    onAddNewProduct = {
                        editingProduct = Product(
                            id = "prod_${UUID.randomUUID().toString().take(8)}",
                            name = "",
                            category = BakeryCategory.CAKES,
                            price = 400.0,
                            isEggless = true,
                            shortDesc = "",
                            description = "",
                            ingredients = listOf("Flour", "Butter", "Sugar"),
                            allergens = listOf("Dairy", "Gluten"),
                            stockStatus = StockStatus.IN_STOCK,
                            isBestseller = false
                        )
                        isNewProductDialog = true
                    },
                    onDeleteProduct = { viewModel.deleteProduct(it) }
                )
                AdminTab.SETTINGS -> AdminSettingsTab(
                    settings = settings,
                    onSaveSettings = { viewModel.updateSettings(it) }
                )
                AdminTab.ANALYTICS -> AdminAnalyticsTab(
                    orders = orders,
                    products = products
                )
            }
        }
    }

    // Product Add/Edit Dialog
    editingProduct?.let { prod ->
        ProductEditDialog(
            initialProduct = prod,
            isNew = isNewProductDialog,
            onDismiss = { editingProduct = null },
            onSave = { updatedProd ->
                viewModel.saveProduct(updatedProd, isNewProductDialog)
                editingProduct = null
            }
        )
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<BakeryOrder>,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Incoming Orders (${orders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Tap to advance status",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${order.orderId} • ₹${order.total.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "${order.customerName} (${order.customerPhone})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        OrderStatusBadge(status = order.status)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Scheduled: ${order.deliveryDate}, ${order.deliveryTimeSlot}",
                        style = MaterialTheme.typography.bodySmall.copy(color = CaramelAccent, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Address: ${order.deliveryAddress}",
                        style = MaterialTheme.typography.bodySmall.copy(color = WarmTextSecondary)
                    )

                    // Items & Custom Cake Requests
                    Spacer(modifier = Modifier.height(8.dp))
                    order.items.forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "• ${item.quantity}x ${item.product.name} (₹${item.totalPrice.toInt()})", fontSize = 12.sp)
                        }
                        if (item.customisation != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PastelPinkLight,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "🎂 CUSTOM CAKE: ${item.customisation.weightKg}kg • ${item.customisation.flavour}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                    if (item.customisation.messageOnCake.isNotBlank()) {
                                        Text(
                                            text = "Message: “${item.customisation.messageOnCake}”",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                        )
                                    }
                                    if (item.customisation.specialInstructions.isNotBlank()) {
                                        Text(
                                            text = "Note: ${item.customisation.specialInstructions}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Changer Row
                    Text(
                        text = "Update Status:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val nextStatuses = when (order.status) {
                            OrderStatus.RECEIVED -> listOf(OrderStatus.CONFIRMED, OrderStatus.BAKING)
                            OrderStatus.CONFIRMED -> listOf(OrderStatus.BAKING, OrderStatus.READY)
                            OrderStatus.BAKING -> listOf(OrderStatus.READY, OrderStatus.OUT_FOR_DELIVERY)
                            OrderStatus.READY -> listOf(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.COMPLETED)
                            OrderStatus.OUT_FOR_DELIVERY -> listOf(OrderStatus.COMPLETED)
                            OrderStatus.COMPLETED -> emptyList()
                            OrderStatus.CANCELLED -> emptyList()
                        }

                        nextStatuses.forEach { nextStatus ->
                            FilledTonalButton(
                                onClick = { onUpdateStatus(order.orderId, nextStatus) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("→ ${nextStatus.title}", fontSize = 11.sp)
                            }
                        }

                        if (order.status != OrderStatus.COMPLETED && order.status != OrderStatus.CANCELLED) {
                            OutlinedButton(
                                onClick = { onUpdateStatus(order.orderId, OrderStatus.CANCELLED) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Cancel", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<Product>,
    onToggleStock: (String, StockStatus) -> Unit,
    onEditProduct: (Product) -> Unit,
    onAddNewProduct: () -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bakery Menu (${products.size} Items)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Button(
                    onClick = onAddNewProduct,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", fontSize = 12.sp)
                }
            }
        }

        items(products, key = { it.id }) { product ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = product.category.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${product.price.toInt()}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                            Text(
                                text = "${product.category.displayName} • ${if (product.isEggless) "Eggless" else "Contains Egg"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WarmTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Stock status cycle button
                        val (stockBg, stockLabel) = when (product.stockStatus) {
                            StockStatus.IN_STOCK -> VegGreenBg to "In Stock"
                            StockStatus.LOW_STOCK -> Color(0xFFFEF3C7) to "Few Left"
                            StockStatus.OUT_OF_STOCK -> Color(0xFFFFEBEE) to "Sold Out"
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = stockBg,
                            modifier = Modifier
                                .clickable {
                                    val nextStock = when (product.stockStatus) {
                                        StockStatus.IN_STOCK -> StockStatus.LOW_STOCK
                                        StockStatus.LOW_STOCK -> StockStatus.OUT_OF_STOCK
                                        StockStatus.OUT_OF_STOCK -> StockStatus.IN_STOCK
                                    }
                                    onToggleStock(product.id, nextStock)
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stockLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { onEditProduct(product) }, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { onDeleteProduct(product.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab(
    settings: BakerySettings,
    onSaveSettings: (BakerySettings) -> Unit
) {
    var deliveryFee by remember { mutableStateOf(settings.deliveryFee.toInt().toString()) }
    var freeThreshold by remember { mutableStateOf(settings.freeDeliveryThreshold.toInt().toString()) }
    var packagingFee by remember { mutableStateOf(settings.packagingFee.toInt().toString()) }
    var storePhone by remember { mutableStateOf(settings.storePhone) }
    var storeWhatsApp by remember { mutableStateOf(settings.storeWhatsApp) }
    var storeAddress by remember { mutableStateOf(settings.storeAddress) }
    var upiEnabled by remember { mutableStateOf(settings.isUpiEnabled) }
    var cardEnabled by remember { mutableStateOf(settings.isCardEnabled) }
    var codEnabled by remember { mutableStateOf(settings.isCodEnabled) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Bakery & Delivery Settings",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Delivery Charges & Thresholds", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryFee,
                        onValueChange = { deliveryFee = it },
                        label = { Text("Standard Delivery Fee (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = freeThreshold,
                        onValueChange = { freeThreshold = it },
                        label = { Text("Free Delivery Order Threshold (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = packagingFee,
                        onValueChange = { packagingFee = it },
                        label = { Text("Packaging / Cake Box Fee (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Payment Gateways Enabled
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Accepted Payment Methods", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable UPI Payments")
                        Switch(checked = upiEnabled, onCheckedChange = { upiEnabled = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Credit/Debit Cards")
                        Switch(checked = cardEnabled, onCheckedChange = { cardEnabled = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Cash on Delivery")
                        Switch(checked = codEnabled, onCheckedChange = { codEnabled = it })
                    }
                }
            }
        }

        // Store Contact Info
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Store Contact Information", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = storePhone,
                        onValueChange = { storePhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = storeWhatsApp,
                        onValueChange = { storeWhatsApp = it },
                        label = { Text("WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = storeAddress,
                        onValueChange = { storeAddress = it },
                        label = { Text("Store Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val updated = settings.copy(
                        deliveryFee = deliveryFee.toDoubleOrNull() ?: 50.0,
                        freeDeliveryThreshold = freeThreshold.toDoubleOrNull() ?: 600.0,
                        packagingFee = packagingFee.toDoubleOrNull() ?: 25.0,
                        storePhone = storePhone,
                        storeWhatsApp = storeWhatsApp,
                        storeAddress = storeAddress,
                        isUpiEnabled = upiEnabled,
                        isCardEnabled = cardEnabled,
                        isCodEnabled = codEnabled
                    )
                    onSaveSettings(updated)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Bakery Settings 💾", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(
    orders: List<BakeryOrder>,
    products: List<Product>
) {
    val totalRevenue = orders.sumOf { it.total }
    val activeOrdersCount = orders.count { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }
    val completedOrdersCount = orders.count { it.status == OrderStatus.COMPLETED }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Daily Bakery Metrics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Total Revenue",
                    value = "₹${totalRevenue.toInt()}",
                    icon = "💰",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Total Orders",
                    value = "${orders.size}",
                    icon = "🧾",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Active In Oven",
                    value = "$activeOrdersCount",
                    icon = "🔥",
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Delivered",
                    value = "$completedOrdersCount",
                    icon = "✅",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Top Selling Bakes",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    products.filter { it.isBestseller }.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${prod.category.iconEmoji} ${prod.name}", fontSize = 12.sp)
                            Text(text = "₹${prod.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, WarmBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun ProductEditDialog(
    initialProduct: Product,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct.name) }
    var category by remember { mutableStateOf(initialProduct.category) }
    var price by remember { mutableStateOf(initialProduct.price.toInt().toString()) }
    var isEggless by remember { mutableStateOf(initialProduct.isEggless) }
    var shortDesc by remember { mutableStateOf(initialProduct.shortDesc) }
    var description by remember { mutableStateOf(initialProduct.description) }
    var stockStatus by remember { mutableStateOf(initialProduct.stockStatus) }
    var isBestseller by remember { mutableStateOf(initialProduct.isBestseller) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val updated = initialProduct.copy(
                        name = name.ifBlank { "Bakery Item" },
                        category = category,
                        price = price.toDoubleOrNull() ?: 200.0,
                        isEggless = isEggless,
                        shortDesc = shortDesc,
                        description = description,
                        stockStatus = stockStatus,
                        isBestseller = isBestseller
                    )
                    onSave(updated)
                }
            ) {
                Text(if (isNew) "Add Item" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = {
            Text(if (isNew) "Add New Bakery Product" else "Edit Product")
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BakeryCategory.values().take(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BakeryCategory.values().drop(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("100% Eggless (Pure Veg)")
                        Switch(checked = isEggless, onCheckedChange = { isEggless = it })
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mark as Bestseller")
                        Switch(checked = isBestseller, onCheckedChange = { isBestseller = it })
                    }
                }

                item {
                    OutlinedTextField(
                        value = shortDesc,
                        onValueChange = { shortDesc = it },
                        label = { Text("Short Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Full Description") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
