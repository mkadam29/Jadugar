package com.jadugar.calculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jadugar.calculator.CalculatorViewModel
import com.jadugar.calculator.ui.theme.*
import com.jadugar.calculator.utils.NumberFormatter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val isDark = viewModel.isDarkTheme
    val clipboardManager = LocalClipboardManager.current

    // ── Snackbar for copy confirmation ────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    var showCopied by remember { mutableStateOf(false) }

    LaunchedEffect(showCopied) {
        if (showCopied) {
            snackbarHostState.showSnackbar("Final price copied!")
            showCopied = false
        }
    }

    // ── Background gradient ───────────────────────────────────────────────────
    val bgColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.background,
        animationSpec = tween(600),
        label = "bg"
    )
    val surfaceColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surface,
        animationSpec = tween(600),
        label = "surface"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── Header ────────────────────────────────────────────────────────
            HeaderSection(
                isDark       = isDark,
                onToggleTheme = { viewModel.toggleTheme() },
                onReset      = { viewModel.reset() },
            )

            // ── Cost Price Input ──────────────────────────────────────────────
            InputCard(
                title       = "Cost Price",
                subtitle    = "Enter the base cost (₹)",
                icon        = Icons.Rounded.CurrencyRupee,
                surfaceColor = surfaceColor,
                isDark      = isDark,
            ) {
                OutlinedTextField(
                    value         = viewModel.costPriceText,
                    onValueChange = { viewModel.onCostPriceChange(it) },
                    modifier      = Modifier.fillMaxWidth(),
                    placeholder   = { Text("e.g. 1000", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    isError       = viewModel.inputError != null,
                    supportingText = viewModel.inputError?.let { err ->
                        { Text(err, color = MaterialTheme.colorScheme.error) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine    = true,
                    shape         = RoundedCornerShape(16.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor    = MaterialTheme.colorScheme.primary,
                        cursorColor          = MaterialTheme.colorScheme.primary,
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.CurrencyRupee,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }

            // ── Profit % Slider ───────────────────────────────────────────────
            SliderCard(
                title        = "Profit Percentage (b)",
                value        = viewModel.profitPct,
                onValueChange = { viewModel.onProfitPctChange(it) },
                valueRange   = 10f..200f,
                steps        = 189,
                displayValue = NumberFormatter.formatPercent(viewModel.profitPct),
                icon         = Icons.Rounded.TrendingUp,
                surfaceColor = surfaceColor,
                isDark       = isDark,
                accentColor  = MaterialTheme.colorScheme.primary,
            )

            // ── Jadoo % Slider ────────────────────────────────────────────────
            SliderCard(
                title        = "Jadoo Percentage (c)",
                value        = viewModel.jadooPct,
                onValueChange = { viewModel.onJadooPctChange(it) },
                valueRange   = 10f..30f,
                steps        = 19,
                displayValue = NumberFormatter.formatPercent(viewModel.jadooPct),
                icon         = Icons.Rounded.AutoAwesome,
                surfaceColor = surfaceColor,
                isDark       = isDark,
                accentColor  = MaterialTheme.colorScheme.secondary,
            )

            // ── Result Card ───────────────────────────────────────────────────
            ResultCard(
                finalPrice   = viewModel.finalPrice,
                isDark       = isDark,
                onCopy = {
                    viewModel.finalPrice?.let { price ->
                        clipboardManager.setText(AnnotatedString(NumberFormatter.formatPrice(price)))
                        showCopied = true
                    }
                },
            )

            // ── Formula Breakdown (visible when result exists) ────────────────
            AnimatedVisibility(
                visible = viewModel.finalPrice != null,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically(),
            ) {
                FormulaBreakdown(
                    a            = viewModel.costPrice ?: 0.0,
                    b            = viewModel.profitPct.toDouble(),
                    c            = viewModel.jadooPct.toDouble(),
                    finalPrice   = viewModel.finalPrice ?: 0.0,
                    surfaceColor = surfaceColor,
                    isDark       = isDark,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeaderSection(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onReset: () -> Unit,
) {
    // Wand rotation animation on theme toggle
    var wandAngle by remember { mutableStateOf(0f) }
    val animatedAngle by animateFloatAsState(
        targetValue = wandAngle,
        animationSpec = tween(500, easing = EaseInOutBack),
        label = "wand",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Title block
        Column {
            Text(
                text  = "Jadugar",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 30.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text  = "Price Calculator",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Reset button
            IconButton(
                onClick  = onReset,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Magic wand theme toggle
            val bgBrush = if (isDark) {
                Brush.linearGradient(listOf(DarkAccentPrimary, DarkAccentSecondary))
            } else {
                Brush.linearGradient(listOf(LightAccentPrimary, LightAccentSecondary))
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(brush = bgBrush)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        wandAngle += 360f
                        onToggleTheme()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoFixHigh,
                    contentDescription = "Toggle theme",
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(animatedAngle),
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Card wrappers
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun InputCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    surfaceColor: Color,
    isDark: Boolean,
    content: @Composable ColumnScope.() -> Unit,
) {
    val elevation = if (isDark) 0.dp else 2.dp
    Surface(
        modifier      = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(24.dp)),
        shape         = RoundedCornerShape(24.dp),
        color         = surfaceColor,
        tonalElevation = if (isDark) 4.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(imageVector = icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
            content()
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Slider Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SliderCard(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    displayValue: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    surfaceColor: Color,
    isDark: Boolean,
    accentColor: Color,
) {
    val elevation = if (isDark) 0.dp else 2.dp
    Surface(
        modifier      = Modifier
            .fillMaxWidth()
            .shadow(elevation, RoundedCornerShape(24.dp)),
        shape         = RoundedCornerShape(24.dp),
        color         = surfaceColor,
        tonalElevation = if (isDark) 4.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(imageVector = icon, contentDescription = null,
                        tint = accentColor, modifier = Modifier.size(20.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface)
                }

                // Animated pill showing current value
                AnimatedContent(
                    targetState = displayValue,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                        slideOutVertically { it } + fadeOut()
                    },
                    label = "sliderVal",
                ) { v ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = accentColor.copy(alpha = 0.15f),
                    ) {
                        Text(
                            text = v,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                        )
                    }
                }
            }

            // Range labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${valueRange.start.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                )
                Text(
                    "${valueRange.endInclusive.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                )
            }

            Slider(
                value         = value,
                onValueChange = onValueChange,
                valueRange    = valueRange,
                steps         = steps,
                modifier      = Modifier.fillMaxWidth(),
                colors        = SliderDefaults.colors(
                    thumbColor            = accentColor,
                    activeTrackColor      = accentColor,
                    inactiveTrackColor    = if (isDark) DarkSliderTrack else LightSliderTrack,
                    activeTickColor       = Color.Transparent,
                    inactiveTickColor     = Color.Transparent,
                ),
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Result Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResultCard(
    finalPrice: Double?,
    isDark: Boolean,
    onCopy: () -> Unit,
) {
    // Animate the displayed number smoothly
    val animatedPrice by animateFloatAsState(
        targetValue = finalPrice?.toFloat() ?: 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "price",
    )

    val gradientColors = if (isDark) {
        listOf(Color(0xFF1A1A3E), Color(0xFF1E1030))
    } else {
        listOf(Color(0xFFEEEEFF), Color(0xFFE6E0FF))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(gradientColors))
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text  = "Final Price (x)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                )
            }

            // The big number
            AnimatedContent(
                targetState = finalPrice != null,
                transitionSpec = {
                    fadeIn(tween(400)) + scaleIn(initialScale = 0.85f) togetherWith
                    fadeOut(tween(200))
                },
                label = "resultVisibility",
            ) { hasResult ->
                if (hasResult) {
                    Text(
                        text  = "₹ ${NumberFormatter.formatPrice(animatedPrice.toDouble())}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize   = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    Text(
                        text  = "—",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize   = 42.sp,
                            fontWeight = FontWeight.Light,
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Copy button
            AnimatedVisibility(
                visible = finalPrice != null,
                enter   = fadeIn() + scaleIn(),
                exit    = fadeOut() + scaleOut(),
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape   = RoundedCornerShape(50),
                    border  = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Copy Price")
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Formula Breakdown
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun FormulaBreakdown(
    a: Double,
    b: Double,
    c: Double,
    finalPrice: Double,
    surfaceColor: Color,
    isDark: Boolean,
) {
    val profit    = a * (b / 100.0)
    val basePrice = a + profit

    Surface(
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(20.dp),
        color         = surfaceColor.copy(alpha = 0.7f),
        tonalElevation = if (isDark) 2.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Calculation Breakdown",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            BreakdownRow("Cost Price (a)", "₹ ${NumberFormatter.formatPrice(a)}")
            BreakdownRow("Profit (${b.toInt()}% of a)", "+ ₹ ${NumberFormatter.formatPrice(profit)}")
            BreakdownRow("Base Price", "₹ ${NumberFormatter.formatPrice(basePrice)}", highlight = true)
            BreakdownRow("Jadoo % (c)", "${c.toInt()}%")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            BreakdownRow("Final Price (x)", "₹ ${NumberFormatter.formatPrice(finalPrice)}",
                highlight = true, large = true)
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    large: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text  = label,
            style = if (large) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
            color = if (highlight) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Normal,
        )
        Text(
            text  = value,
            style = if (large) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodySmall,
            color = if (highlight) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
