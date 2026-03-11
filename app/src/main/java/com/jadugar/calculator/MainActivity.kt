package com.jadugar.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jadugar.calculator.ui.screens.CalculatorScreen
import com.jadugar.calculator.ui.theme.JadugarTheme

/**
 * MainActivity – single-activity host.
 *
 * The ViewModel is scoped to this activity so all state (inputs, sliders,
 * theme preference) survives rotation automatically.
 */
class MainActivity : ComponentActivity() {

    // ViewModel scoped to the activity lifecycle
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observe theme state from ViewModel
            val isDark = viewModel.isDarkTheme

            // Wrap entire app in animated cross-fade when theme changes
            AnimatedContent(
                targetState = isDark,
                transitionSpec = {
                    fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                },
                label = "themeTransition",
            ) { dark ->
                JadugarTheme(darkTheme = dark) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        CalculatorScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
