package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.data.model.BakeryOrder
import com.example.data.model.OrderStatus
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BakeryViewModel

@Composable
fun OrderTrackingScreen(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val trackedOrderId by viewModel.trackedOrderId.collectAsState()

    val currentOrder = orders.find { it.orderId == trackedOrderId } ?: orders.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Order Tracking",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Live updates from the bakery oven to your hands.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Demo Notice
        item {
            DemoModeBanner()
        }

        if (currentOrder != null) {
            // Live Status Stepper Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, WarmBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentOrder.orderId,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (currentOrder.isDemoOrder) {
                                        Surface(
                                            color = Color(0xFFFFF3CD),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(0.8.dp, Color(0xFFFFEEBA))
                                        ) {
                                            Text(
                                                text = "DEMO SIMULATION",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF856404)
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Estimated Time: ${currentOrder.estimatedArrival}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CaramelAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            OrderStatusBadge(status = currentOrder.status)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Status Stepper Visual
                        OrderStatusStepper(currentStatus = currentOrder.status)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Delivery details
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BakeryCreamBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📍", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${currentOrder.deliveryType.label} • ${currentOrder.customerName}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    text = currentOrder.deliveryAddress,
                                    style = MaterialTheme.typography.bodySmall.copy(color = WarmTextSecondary),
                                    modifier = Modifier.padding(start = 22.dp)
                                )
                                if (currentOrder.instructions.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Instructions: “${currentOrder.instructions}”",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        ),
                                        modifier = Modifier.padding(start = 22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Ordered Items Summary
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        currentOrder.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(text = "${item.quantity}x", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = item.product.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        if (item.customisation != null) {
                                            Text(
                                                text = "${item.customisation.weightKg}kg • ${item.customisation.flavour}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                            if (item.customisation.messageOnCake.isNotBlank()) {
                                                Text(
                                                    text = "Message: “${item.customisation.messageOnCake}”",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = "₹${item.totalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = WarmBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Paid (${currentOrder.paymentMethod.name})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "₹${currentOrder.total.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Download & Share Receipt Buttons
                        var showReceiptDialog by remember { mutableStateOf(false) }
                        val context = androidx.compose.ui.platform.LocalContext.current

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showReceiptDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("download_receipt_button")
                            ) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Invoice", style = MaterialTheme.typography.labelMedium)
                            }

                            Button(
                                onClick = {
                                    val receiptText = """
                                        ==============================
                                        🎂 NIMAI BAKERY INVOICE 🎂
                                        "Freshly baked, lovingly made."
                                        Order ID: ${currentOrder.orderId}
                                        Date: ${currentOrder.deliveryDate}, ${currentOrder.deliveryTimeSlot}
                                        Customer: ${currentOrder.customerName} (${currentOrder.customerPhone})
                                        Delivery: ${currentOrder.deliveryType.label}
                                        Address: ${currentOrder.deliveryAddress}
                                        ------------------------------
                                        ${currentOrder.items.joinToString("\n") { "• ${it.quantity}x ${it.product.name} - ₹${it.totalPrice.toInt()}" }}
                                        ------------------------------
                                        Subtotal: ₹${currentOrder.subtotal.toInt()}
                                        Packaging: ₹${currentOrder.packagingFee.toInt()}
                                        Delivery: ₹${currentOrder.deliveryFee.toInt()}
                                        Discount: -₹${currentOrder.discount.toInt()}
                                        GRAND TOTAL: ₹${currentOrder.total.toInt()}
                                        Payment: ${currentOrder.paymentMethod.label}
                                        Status: ${currentOrder.status.title}
                                        ==============================
                                        Thank you for choosing Nimai Bakery!
                                    """.trimIndent()

                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, receiptText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Download / Share Invoice"))
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("share_download_button")
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download Slip", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        if (showReceiptDialog) {
                            AlertDialog(
                                onDismissRequest = { showReceiptDialog = false },
                                confirmButton = {
                                    Button(onClick = { showReceiptDialog = false }) {
                                        Text("Done")
                                    }
                                },
                                title = {
                                    Text("Official Order Invoice", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                                },
                                text = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BakeryCreamBg)
                                            .padding(12.dp)
                                    ) {
                                        Text("🎂 Nimai Bakery", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("Order ${currentOrder.orderId}", fontSize = 12.sp, color = WarmTextSecondary)
                                        Text("Date: ${currentOrder.deliveryDate} (${currentOrder.deliveryTimeSlot})", fontSize = 11.sp)
                                        Divider(modifier = Modifier.padding(vertical = 6.dp), color = WarmBorder)
                                        currentOrder.items.forEach { itm ->
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("${itm.quantity}x ${itm.product.name}", fontSize = 11.sp)
                                                Text("₹${itm.totalPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Divider(modifier = Modifier.padding(vertical = 6.dp), color = WarmBorder)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Grand Total", fontWeight = FontWeight.Bold)
                                            Text("₹${currentOrder.total.toInt()}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // List of all orders to switch between
        if (orders.size > 1) {
            item {
                Text(
                    text = "All Recent Orders",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            items(orders) { order ->
                val isSelected = order.orderId == currentOrder?.orderId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectTrackedOrder(order.orderId) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PastelPinkLight else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) MaterialTheme.colorScheme.secondary else WarmBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = order.orderId,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ₹${order.total.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                            Text(
                                text = "${order.items.size} item(s) • ${order.deliveryDate}, ${order.deliveryTimeSlot}",
                                style = MaterialTheme.typography.bodySmall.copy(color = WarmTextSecondary)
                            )
                        }
                        OrderStatusBadge(status = order.status)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusStepper(currentStatus: OrderStatus, modifier: Modifier = Modifier) {
    val steps = listOf(
        OrderStatus.RECEIVED to "Received",
        OrderStatus.CONFIRMED to "Confirmed",
        OrderStatus.BAKING to "Baking",
        OrderStatus.READY to "Ready",
        OrderStatus.OUT_FOR_DELIVERY to "Out for Delivery",
        OrderStatus.COMPLETED to "Delivered"
    )

    val currentStepIndex = currentStatus.stepIndex

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        steps.forEachIndexed { index, (status, label) ->
            val isDone = index < currentStepIndex
            val isCurrent = index == currentStepIndex
            val isFuture = index > currentStepIndex

            val iconColor = when {
                isDone -> VegGreen
                isCurrent -> CaramelAccent
                else -> Color.LightGray
            }

            val stepEmoji = when (status) {
                OrderStatus.RECEIVED -> "📥"
                OrderStatus.CONFIRMED -> "📋"
                OrderStatus.BAKING -> "🧑‍🍳"
                OrderStatus.READY -> "🎂"
                OrderStatus.OUT_FOR_DELIVERY -> "🛵"
                OrderStatus.COMPLETED -> "🎉"
                else -> "📦"
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circle with step icon or checkmark
                Surface(
                    shape = CircleShape,
                    color = when {
                        isDone -> VegGreen
                        isCurrent -> CaramelAccent
                        else -> BakeryCreamVariant
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = stepEmoji,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isDone -> VegGreen
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    )
                    if (isCurrent) {
                        Text(
                            text = status.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = CaramelAccent
                            )
                        )
                    }
                }
            }
        }
    }
}
