package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.VegEggBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BakeryViewModel

@Composable
fun CartAndCheckoutScreen(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedPromo by viewModel.appliedPromo.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val showCheckout by viewModel.showCheckout.collectAsState()

    var promoInput by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val discount = if (appliedPromo != null) (subtotal * appliedPromo!!.discountPercent / 100.0) else 0.0
    val deliveryFee = if (subtotal >= settings.freeDeliveryThreshold || subtotal == 0.0) 0.0 else settings.deliveryFee
    val packagingFee = if (subtotal > 0) settings.packagingFee else 0.0
    val grandTotal = (subtotal - discount + deliveryFee + packagingFee).coerceAtLeast(0.0)

    if (showCheckout) {
        CheckoutModalSheet(
            viewModel = viewModel,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            packagingFee = packagingFee,
            grandTotal = grandTotal,
            appliedPromo = appliedPromo,
            onDismiss = { viewModel.closeCheckout() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (cartItems.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🛒", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your Cart is Empty",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Add some freshly baked goodies from our oven!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.MENU) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("empty_cart_browse_button")
                    ) {
                        Text("Browse Bakery Menu 🍰")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Your Bakery Bag (${cartItems.sumOf { it.quantity }} items)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Demo Notice Banner
                item {
                    DemoModeBanner()
                }

                // Cart Items List
                items(cartItems, key = { it.id }) { item ->
                    CartItemRow(
                        cartItem = item,
                        onQuantityChange = { newQty -> viewModel.updateCartQuantity(item.id, newQty) },
                        onRemove = { viewModel.removeFromCart(item.id) }
                    )
                }

                // Promo Code Voucher Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Coupons & Offers",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (appliedPromo != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VegGreenBg)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎉", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${appliedPromo!!.code} Applied!",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = VegGreen
                                                )
                                            )
                                            Text(
                                                text = "You save ₹${discount.toInt()} (${appliedPromo!!.discountPercent}% off)",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = VegGreen,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                    TextButton(onClick = { viewModel.removePromoCode() }) {
                                        Text("Remove", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = promoInput,
                                        onValueChange = { promoInput = it },
                                        placeholder = { Text("Enter SWEET15 or NIMAI10") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("promo_code_input")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (promoInput.isNotBlank()) {
                                                viewModel.applyPromoCode(promoInput)
                                                promoInput = ""
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.testTag("apply_promo_button")
                                    ) {
                                        Text("Apply")
                                    }
                                }
                            }
                        }
                    }
                }

                // Price Breakdown Bill Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, WarmBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Bill Details",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            BillRow(label = "Item Total", amount = "₹${subtotal.toInt()}")
                            if (discount > 0) {
                                BillRow(label = "Discount (${appliedPromo?.code})", amount = "-₹${discount.toInt()}", isGreen = true)
                            }
                            BillRow(label = "Artisan Packaging & Safe Box", amount = "₹${packagingFee.toInt()}")
                            BillRow(
                                label = "Delivery Partner Fee",
                                amount = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                                isGreen = deliveryFee == 0.0
                            )

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = WarmBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "To Pay",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        text = "Inclusive of all taxes",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                Text(
                                    text = "₹${grandTotal.toInt()}",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Checkout Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "₹${grandTotal.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Button(
                        onClick = { viewModel.openCheckout() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("proceed_to_checkout_button")
                    ) {
                        Text(
                            text = "Proceed to Checkout →",
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, WarmBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Text(text = cartItem.product.category.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cartItem.product.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        VegEggBadge(isEggless = cartItem.customisation?.isEggless ?: cartItem.product.isEggless)

                        if (cartItem.customisation != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "✨ Custom: ${cartItem.customisation.weightKg} kg • ${cartItem.customisation.flavour}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            if (cartItem.customisation.messageOnCake.isNotBlank()) {
                                Text(
                                    text = "🎂 Message: “${cartItem.customisation.messageOnCake}”",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = WarmTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                // Delete item button
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quantity and Subtotal Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Stepper Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BakeryCreamVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(text = "-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Text(
                        text = "${cartItem.quantity}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Text(
                    text = "₹${cartItem.totalPrice.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun BillRow(label: String, amount: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isGreen) VegGreen else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutModalSheet(
    viewModel: BakeryViewModel,
    subtotal: Double,
    discount: Double,
    deliveryFee: Double,
    packagingFee: Double,
    grandTotal: Double,
    appliedPromo: PromoOffer?,
    onDismiss: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    var name by remember { mutableStateOf("Nisha Kansal") }
    var phone by remember { mutableStateOf("+91 98765 12345") }
    var address by remember { mutableStateOf("Flat 301, Tower B, Cyber Greens, Sector 24") }
    var instructions by remember { mutableStateOf("Please ring bell, package with eco-friendly carry bag") }
    var deliveryType by remember { mutableStateOf(DeliveryType.HOME_DELIVERY) }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedTimeSlot by remember { mutableStateOf(settings.availableTimeSlots.firstOrNull() ?: "4:00 PM - 7:00 PM") }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.UPI) }

    val dateOptions = listOf("Today", "Tomorrow", "In 2 Days")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Checkout & Schedule",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            // CRITICAL DEMO COMPLIANCE BANNER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    border = BorderStroke(1.dp, Color(0xFFFFEEBA))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚠️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Notice: Unconfigured Payment & Backend (Demo Mode)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF856404)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Live payment gateway is not connected. This order is placed in DEMO SIMULATION MODE for testing order tracking and bakery operations. No real financial transaction will occur.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = Color(0xFF856404)
                            )
                        )
                    }
                }
            }

            // 1. Delivery or Pickup Choice
            item {
                Text(
                    text = "Delivery Method",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DeliveryType.values().forEach { type ->
                        val isSelected = deliveryType == type
                        OutlinedButton(
                            onClick = { deliveryType = type },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Text(type.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Date & Slot Selection
            item {
                Text(
                    text = "Select Delivery / Pickup Slot",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Date Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dateOptions.forEach { date ->
                        val isSelected = selectedDate == date
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDate = date },
                            label = { Text(date) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Time Slots Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    settings.availableTimeSlots.chunked(2).forEach { rowSlots ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowSlots.forEach { slot ->
                                val isSelected = selectedTimeSlot == slot
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTimeSlot = slot },
                                    label = { Text(slot, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Customer Contact Details
            item {
                Text(
                    text = "Contact Details",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_name_input")
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_phone_input")
                )
            }

            // Delivery Address (if home delivery)
            if (deliveryType == DeliveryType.HOME_DELIVERY) {
                item {
                    Text(
                        text = "Delivery Address",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address with Landmark & Flat #") },
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )
                }
            }

            // Special instructions
            item {
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Order & Delivery Instructions") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Payment Method
            item {
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PaymentMethod.values().forEach { method ->
                        val isSelected = selectedPayment == method
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayment = method },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PastelPinkLight else BakeryCreamBg
                            ),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) MaterialTheme.colorScheme.secondary else WarmBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = method.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Text(
                                        text = method.desc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPayment = method }
                                )
                            }
                        }
                    }
                }
            }

            // Final Confirm Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.placeOrder(
                            customerName = name.ifBlank { "Customer" },
                            customerPhone = phone.ifBlank { "+91 98765 00000" },
                            deliveryType = deliveryType,
                            deliveryAddress = address,
                            deliveryDate = selectedDate,
                            deliveryTimeSlot = selectedTimeSlot,
                            instructions = instructions,
                            paymentMethod = selectedPayment
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_order_button")
                ) {
                    Text(
                        text = "Place Demo Order (₹${grandTotal.toInt()})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
