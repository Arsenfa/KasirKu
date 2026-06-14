package com.kasirku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kasirku.app.ui.screens.admin.*
import com.kasirku.app.ui.screens.dashboard.DashboardScreen
import com.kasirku.app.ui.screens.history.HistoryScreen
import com.kasirku.app.ui.screens.login.LoginScreen
import com.kasirku.app.ui.screens.onboarding.OnboardingScreen
import com.kasirku.app.ui.screens.payment.PaymentScreen
import com.kasirku.app.ui.screens.receipt.ReceiptScreen
import com.kasirku.app.ui.screens.reports.ReportsScreen
import com.kasirku.app.ui.screens.settings.SettingsScreen
import com.kasirku.app.ui.screens.splash.SplashScreen
import com.kasirku.app.ui.theme.KasirKuTheme
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.KasirViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: KasirViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val isDark = themeMode == "dark"
            val useSystem = themeMode == "auto"
            KasirKuTheme(darkMode = isDark, useSystemTheme = useSystem) {
                KasirKuApp(viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val screen: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun KasirKuApp(viewModel: KasirViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentCashier by viewModel.currentCashier.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.seedSampleProducts()
    }

    val isAdmin = currentCashier?.isAdmin() == true

    // Check onboarding status
    val onboardingCompleted = viewModel.storeConfig.onboardingCompleted
    val showOnboarding = !onboardingCompleted && currentScreen == "splash"

    val bottomNavItems = if (isAdmin) {
        listOf(
            BottomNavItem("admin_hub", "Admin", Icons.Default.AdminPanelSettings),
            BottomNavItem("dashboard", "Kasir", Icons.Default.PointOfSale),
            BottomNavItem("history", "Riwayat", Icons.Default.Receipt),
            BottomNavItem("reports", "Laporan", Icons.Default.Analytics)
        )
    } else {
        listOf(
            BottomNavItem("dashboard", "Kasir", Icons.Default.PointOfSale),
            BottomNavItem("history", "Riwayat", Icons.Default.Receipt),
            BottomNavItem("reports", "Laporan", Icons.Default.Analytics),
            BottomNavItem("settings", "Lainnya", Icons.Default.MoreHoriz)
        )
    }

    val cashierScreens = listOf("dashboard", "history", "reports", "settings")
    val adminScreens = listOf("admin_hub", "dashboard", "history", "reports")
    val showBottomNav = currentScreen in (if (isAdmin) adminScreens else cashierScreens)

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentScreen == item.screen,
                            onClick = { viewModel.navigateTo(item.screen) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TealPrimary,
                                selectedTextColor = TealPrimary,
                                indicatorColor = TealPrimary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Show onboarding overlay if not completed
            if (showOnboarding) {
                OnboardingScreen(
                    onComplete = {
                        viewModel.storeConfig.onboardingCompleted = true
                        viewModel.navigateTo("login")
                    },
                    onSkip = {
                        viewModel.storeConfig.onboardingCompleted = true
                        viewModel.navigateTo("login")
                    }
                )
            } else {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width } + fadeOut())
                    },
                    label = "ScreenTransition"
                ) { screen ->
                    when (screen) {
                        "splash" -> SplashScreen(onTimeout = { viewModel.navigateTo("login") })
                        "login" -> LoginScreen(viewModel)
                        "admin_hub" -> AdminHubScreen(viewModel)
                        "dashboard" -> DashboardScreen(viewModel = viewModel, onNavigateToPayment = { viewModel.navigateTo("payment") })
                        "payment" -> PaymentScreen(viewModel)
                        "receipt" -> ReceiptScreen(viewModel)
                        "history" -> HistoryScreen(viewModel)
                        "reports" -> ReportsScreen(viewModel)
                        "settings" -> SettingsScreen(viewModel)
                        "manage_products" -> ManageProductsScreen(viewModel)
                        "manage_cashiers" -> ManageCashiersScreen(viewModel)
                        "manage_promos" -> ManagePromosScreen(viewModel)
                        "manage_categories" -> ManageCategoriesScreen(viewModel)
                        "store_settings" -> StoreSettingsScreen(viewModel)
                        "shift" -> ShiftScreen(viewModel)
                    }
                }
            }
        }
    }
}
