package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.DemoModeBanner
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BakeryViewModel

@Composable
fun HomeScreen(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    val products = viewModel.products.value
    val bestsellers = products.filter { it.isBestseller }
    val offers = viewModel.settings.value.activeOffers

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Banner with Bakery Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.img_hero_bakery),
                    contentDescription = "Nimai Bakery Counter",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // Hero Copy and CTA
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        color = PastelPinkPrimary,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Artisan Bakehouse & Patisserie",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChocolateDark,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Nimai Bakery",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = BakeryCreamBg,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "“Freshly baked, lovingly made.”",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = BakeryCreamVariant,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.MENU) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelPinkPrimary,
                                contentColor = ChocolateDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("hero_order_now_button")
                        ) {
                            Icon(imageVector = Icons.Filled.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Order Now", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.startCustomisingCake() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = BakeryCreamBg
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BakeryCreamBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("hero_customise_cake_button")
                        ) {
                            Text("Design a Cake ✨", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Demo Notice
        item {
            DemoModeBanner(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Special Offers Carousel
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                PaddingValues(horizontal = 16.dp).let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Special Offers & Deals",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Save More",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(offers) { offer ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .clickable {
                                    viewModel.applyPromoCode(offer.code)
                                    viewModel.navigateTo(AppScreen.CART)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PastelPinkLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                PastelPinkContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "🎁", fontSize = 22.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = offer.code,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = CaramelAccent,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "${offer.discountPercent}% OFF",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = offer.description,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmTextSecondary,
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Product Categories Quick Selector
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = "Browse Categories",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(BakeryCategory.values()) { category ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectCategory(category)
                                    viewModel.navigateTo(AppScreen.MENU)
                                }
                                .testTag("home_category_${category.name.lowercase()}")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .size(62.dp)
                                    .border(1.dp, WarmBorder, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = category.iconEmoji, fontSize = 28.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = category.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bestsellers Section
        item {
            Column(modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Bestsellers",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Handcrafted favorites our customers love",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    TextButton(onClick = { viewModel.navigateTo(AppScreen.MENU) }) {
                        Text(
                            text = "View All →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bestsellers Cards Grid / Column
                bestsellers.take(4).forEach { product ->
                    ProductCard(
                        product = product,
                        onCardClick = { viewModel.openProductDetail(product) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onCustomise = if (product.isCustomisableCake) {
                            { viewModel.startCustomisingCake(product) }
                        } else null,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }

        // Bakery Quality Badges
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BakeryCreamSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "The Nimai Bakery Promise",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QualityBadge(icon = "🌿", title = "100% Pure", subtitle = "No preservatives")
                        QualityBadge(icon = "🟢", title = "Pure Veg", subtitle = "Dedicated eggless")
                        QualityBadge(icon = "🔥", title = "Fresh Daily", subtitle = "Baked at 6 AM")
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityBadge(icon: String, title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
