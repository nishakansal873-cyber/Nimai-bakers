package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun BakeryTopBar(
    cartCount: Int,
    currentScreen: AppScreen,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Tagline
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🎂",
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Nimai Bakery",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Freshly baked, lovingly made.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                        }
                    }
                }

                // Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Contact button
                    IconButton(
                        onClick = onContactClick,
                        modifier = Modifier.testTag("top_bar_contact_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = "Contact Bakery",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Admin shortcut
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("top_bar_admin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = "Owner Dashboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Cart Icon with Badge
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier.testTag("top_bar_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$cartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = "Shopping Cart",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BakeryBottomNav(
    currentScreen: AppScreen,
    cartCount: Int,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val navItems = listOf(
            Triple(AppScreen.HOME, Icons.Filled.Home, Icons.Outlined.Home),
            Triple(AppScreen.MENU, Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu),
            Triple(AppScreen.CUSTOM_CAKE, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
            Triple(AppScreen.CART, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
            Triple(AppScreen.ORDERS, Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping),
            Triple(AppScreen.ADMIN, Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
        )

        navItems.forEach { (screen, selectedIcon, unselectedIcon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    if (screen == AppScreen.CART && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$cartCount")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                contentDescription = screen.label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                            contentDescription = screen.label
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}

@Composable
fun VegEggBadge(isEggless: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isEggless) VegGreen else EggBrown
    val dotColor = if (isEggless) VegGreen else EggBrown
    val label = if (isEggless) "100% Eggless" else "Contains Egg"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isEggless) VegGreenBg else EggBrownBg)
            .border(0.8.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        // Standard food classification icon
        Box(
            modifier = Modifier
                .size(10.dp)
                .border(1.2.dp, borderColor, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = borderColor
            )
        )
    }
}

@Composable
fun DemoModeBanner(
    modifier: Modifier = Modifier,
    onExploreClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD)
        ),
        border = BorderStroke(1.dp, Color(0xFFFFEEBA))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🧪", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Demo Mode Active",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF856404)
                    )
                )
                Text(
                    text = "Backend & live payment gateway not configured. Orders are simulated for demonstration & workflow tracking.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = Color(0xFF856404)
                    )
                )
            }
            if (onExploreClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Sample", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit,
    onCustomise: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Header / Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PastelPinkLight,
                                BakeryCreamVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Category emoji graphic
                Text(
                    text = product.category.iconEmoji,
                    fontSize = 52.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bestseller badge
                if (product.isBestseller) {
                    Surface(
                        color = CaramelAccent,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "⭐ Bestseller",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Stock status tag
                val stockColor = when (product.stockStatus) {
                    StockStatus.IN_STOCK -> VegGreen
                    StockStatus.LOW_STOCK -> CaramelAccent
                    StockStatus.OUT_OF_STOCK -> MaterialTheme.colorScheme.error
                }
                Surface(
                    color = stockColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = product.stockStatus.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = stockColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Veg / Eggless tag
                VegEggBadge(
                    isEggless = product.isEggless,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }

            // Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.shortDesc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price and Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "₹${product.price.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = if (product.category == BakeryCategory.CAKES) "Starting price (0.5 kg)" else "Per unit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Row {
                        if (product.isCustomisableCake && onCustomise != null) {
                            OutlinedButton(
                                onClick = onCustomise,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(end = 4.dp)
                                    .testTag("customise_button_${product.id}")
                            ) {
                                Text(
                                    text = "Customise",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = onAddToCart,
                            enabled = product.stockStatus != StockStatus.OUT_OF_STOCK,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("add_to_cart_button_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.RECEIVED -> StatusReceived.copy(alpha = 0.15f) to StatusReceived
        OrderStatus.CONFIRMED -> StatusConfirmed.copy(alpha = 0.15f) to StatusConfirmed
        OrderStatus.BAKING -> StatusBaking.copy(alpha = 0.15f) to StatusBaking
        OrderStatus.READY -> StatusReady.copy(alpha = 0.15f) to StatusReady
        OrderStatus.OUT_FOR_DELIVERY -> StatusOutForDelivery.copy(alpha = 0.15f) to StatusOutForDelivery
        OrderStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.15f) to StatusCompleted
        OrderStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.15f) to StatusCancelled
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = status.title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ProductDetailDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onCustomise: (Product) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VegEggBadge(isEggless = product.isEggless)
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Graphic Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PastelPinkLight, BakeryCreamVariant)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.category.iconEmoji,
                        fontSize = 64.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "₹${product.price.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ingredients
                Text(
                    text = "Ingredients:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = product.ingredients.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Allergens
                Text(
                    text = "Allergens: ${product.allergens.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (product.isCustomisableCake) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onCustomise(product)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text("Customise 🎂", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Button(
                        onClick = {
                            onAddToCart(product)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Add to Cart", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    )
}
