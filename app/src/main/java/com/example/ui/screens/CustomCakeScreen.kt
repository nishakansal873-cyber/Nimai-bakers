package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.VegEggBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.BakeryViewModel

@Composable
fun CustomCakeScreen(
    viewModel: BakeryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.customCakeState.collectAsState()

    val flavours = listOf(
        "Belgian Dark Chocolate Truffle" to Color(0xFF4A2C2A),
        "Classic Red Velvet Cream Cheese" to Color(0xFF881B24),
        "Fresh Pineapple Blossom" to Color(0xFFEAB308),
        "Eggless Vanilla Bean & Berries" to Color(0xFFFCE7F3),
        "Butterscotch Caramel Crunch" to Color(0xFFD97706),
        "Fresh Alphonso Mango Supreme" to Color(0xFFF59E0B)
    )

    val weights = listOf(0.5, 1.0, 1.5, 2.0)

    val designThemes = listOf(
        "Classic Floral Buttercream",
        "Royal Golden Drip",
        "Minimalist Pastel Art",
        "Chocolate Lovers Overload",
        "Berry Garden Bloom"
    )

    val referenceStyles = listOf(
        "Vintage Lambeth Ruffles 🎀",
        "Modern Gold Leaf Aesthetic ✨",
        "Kids Cartoon Theme 🧸",
        "Fresh Edible Botanicals 🌸",
        "Custom Baker's Choice 🎨"
    )

    // Current cake layer color for the preview
    val currentCakeColor = flavours.find { it.first == state.flavour }?.second ?: Color(0xFF4A2C2A)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Description
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Customise Your Celebration Cake",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Text(
                    text = "Crafted by hand according to your exact flavour, size, and message.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Live Interactive Cake Preview Stage
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_cake_preview_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BakeryCreamSurface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LIVE CAKE PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Illustrated Cake graphic box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(PastelPinkLight, BakeryCreamVariant)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Candle with flickering flame
                            Text(text = "🕯️", fontSize = 24.sp)

                            // Cake Layer 1 (Top Tier)
                            Surface(
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                color = currentCakeColor,
                                modifier = Modifier
                                    .width(if (state.weightKg >= 1.5) 170.dp else 140.dp)
                                    .height(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "🍓 🌸 🍒",
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Cake Layer 2 (Bottom Tier)
                            Surface(
                                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                                color = currentCakeColor.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .width(if (state.weightKg >= 1.5) 210.dp else 170.dp)
                                    .height(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (state.messageOnCake.isNotBlank()) state.messageOnCake else "Your Message Here",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = ChocolateDark
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Cake Stand
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CaramelAccent,
                                modifier = Modifier
                                    .width(190.dp)
                                    .height(6.dp)
                            ) {}
                        }

                        // Dietary badge on preview
                        VegEggBadge(
                            isEggless = state.isEggless,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preview summary tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.weightKg} kg • ${state.flavour}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₹${state.calculatedPrice.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }

        // Step 1: Flavour Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Choose Cake Flavour",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    flavours.forEach { (flavourName, color) ->
                        val isSelected = state.flavour == flavourName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PastelPinkLight else Color.Transparent)
                                .clickable { viewModel.updateCustomCakeFlavour(flavourName) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = color,
                                    modifier = Modifier.size(20.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = flavourName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Weight & Servings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Select Weight / Size",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        weights.forEach { weight ->
                            val isSelected = state.weightKg == weight
                            val servings = when (weight) {
                                0.5 -> "3-4 Servings"
                                1.0 -> "6-8 Servings"
                                1.5 -> "10-12 Servings"
                                else -> "15-18 Servings"
                            }
                            OutlinedButton(
                                onClick = { viewModel.updateCustomCakeWeight(weight) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else WarmBorder
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$weight kg",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = servings,
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Vegetarian / Egg Preference
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Dietary Preference",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Eggless Option
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateCustomCakeEggless(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.isEggless) VegGreenBg else BakeryCreamBg
                            ),
                            border = BorderStroke(
                                if (state.isEggless) 1.8.dp else 1.dp,
                                if (state.isEggless) VegGreen else WarmBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🟢", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "100% Eggless",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = VegGreen
                                    )
                                )
                                Text(
                                    text = "Pure Vegetarian",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                )
                            }
                        }

                        // Contains Egg Option
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateCustomCakeEggless(false) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!state.isEggless) EggBrownBg else BakeryCreamBg
                            ),
                            border = BorderStroke(
                                if (!state.isEggless) 1.8.dp else 1.dp,
                                if (!state.isEggless) EggBrown else WarmBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🟤", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Contains Egg",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EggBrown
                                    )
                                )
                                Text(
                                    text = "Classic Recipe",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 4: Birthday / Celebration Message on Cake
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Celebration Message on Cake",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Piped by hand on sugar plaque (max 35 characters)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.messageOnCake,
                        onValueChange = { if (it.length <= 35) viewModel.updateCustomCakeMessage(it) },
                        placeholder = { Text("e.g., Happy 25th Birthday Rhea! 🎂") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_cake_message_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick suggestion chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val suggestions = listOf("Happy Birthday!", "Happy Anniversary! ❤️", "Best Wishes 🎉", "Congratulations! 🌟")
                        items(suggestions) { sugg ->
                            SuggestionChip(
                                onClick = { viewModel.updateCustomCakeMessage(sugg) },
                                label = { Text(sugg, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Step 5: Design Style & Reference Image Inspiration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Design & Reference Style",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Choose your preferred aesthetic style for our master cake artist:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(referenceStyles) { refStyle ->
                            val isSelected = state.designStyle == refStyle
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateCustomCakeDesign(refStyle) },
                                label = { Text(refStyle, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Special baking note
                    OutlinedTextField(
                        value = state.specialInstructions,
                        onValueChange = { viewModel.updateCustomCakeInstructions(it) },
                        placeholder = { Text("Special instructions (e.g. less sugar, no nuts, extra chocolate curls)...") },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Complimentary Accessories Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BakeryCreamSurface),
                border = BorderStroke(1.dp, WarmBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎂 Free Candle & Wooden Knife",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Complimentary celebration accessories pack",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Switch(
                        checked = state.includeCandleAndKnife,
                        onCheckedChange = { viewModel.updateCustomCakeCandleKnife(it) }
                    )
                }
            }
        }

        // Add to Cart Final Button
        item {
            Button(
                onClick = { viewModel.addCustomCakeToCart() },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("add_custom_cake_to_cart_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add Custom Cake to Cart",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Text(
                        text = "₹${state.calculatedPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PastelPinkPrimary
                        )
                    )
                }
            }
        }
    }
}
