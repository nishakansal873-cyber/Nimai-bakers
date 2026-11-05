package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BakeryBottomNav
import com.example.ui.components.BakeryTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.BakeryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val bakeryViewModel: BakeryViewModel = viewModel()
                NimaiBakeryApp(viewModel = bakeryViewModel)
            }
        }
    }
}

@Composable
fun NimaiBakeryApp(
    viewModel: BakeryViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show transient feedback toasts / snackbars
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = msg,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearUserMessage()
            }
        }
    }

    // Hardware Back Button Handler
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BakeryTopBar(
                cartCount = cartCount,
                currentScreen = currentScreen,
                onCartClick = { viewModel.navigateTo(AppScreen.CART) },
                onAdminClick = { viewModel.navigateTo(AppScreen.ADMIN) },
                onContactClick = { viewModel.navigateTo(AppScreen.CONTACT) }
            )
        },
        bottomBar = {
            BakeryBottomNav(
                currentScreen = currentScreen,
                cartCount = cartCount,
                onScreenSelected = { viewModel.navigateTo(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                    AppScreen.MENU -> MenuScreen(viewModel = viewModel)
                    AppScreen.CUSTOM_CAKE -> CustomCakeScreen(viewModel = viewModel)
                    AppScreen.CART -> CartAndCheckoutScreen(viewModel = viewModel)
                    AppScreen.ORDERS -> OrderTrackingScreen(viewModel = viewModel)
                    AppScreen.CONTACT -> ContactScreen(viewModel = viewModel)
                    AppScreen.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
