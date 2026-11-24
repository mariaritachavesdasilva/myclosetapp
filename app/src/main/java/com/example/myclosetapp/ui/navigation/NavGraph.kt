package com.example.myclosetapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myclosetapp.data.model.OccasionItem
import com.example.myclosetapp.data.model.WeatherItem
import com.example.myclosetapp.di.AppContainer
import com.example.myclosetapp.ui.createoutfit.CreateOutfitScreen
import com.example.myclosetapp.ui.createoutfit.CreateOutfitViewModel
import com.example.myclosetapp.ui.favorites.FavoritesScreen
import com.example.myclosetapp.ui.favorites.FavoritesViewModel
import com.example.myclosetapp.ui.history.HistoryScreen
import com.example.myclosetapp.ui.history.HistoryViewModel
import com.example.myclosetapp.ui.home.HomeScreen
import com.example.myclosetapp.ui.home.HomeViewModel
import com.example.myclosetapp.ui.itemdetail.ItemEditScreen
import com.example.myclosetapp.ui.itemdetail.ItemEditViewModel
import com.example.myclosetapp.ui.items.ItemsScreen
import com.example.myclosetapp.ui.items.ItemsViewModel
import com.example.myclosetapp.ui.login.LoginScreen
import com.example.myclosetapp.ui.login.LoginViewModel
import com.example.myclosetapp.ui.outfitdetail.OutfitDetailScreen
import com.example.myclosetapp.ui.outfitdetail.OutfitDetailViewModel
import com.example.myclosetapp.ui.outfits.OutfitsScreen
import com.example.myclosetapp.ui.outfits.OutfitsViewModel
import com.example.myclosetapp.ui.profile.ProfileScreen
import com.example.myclosetapp.ui.profile.ProfileViewModel

@Composable
fun NavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoggedIn by container.authRepository.isLoggedIn.collectAsState(initial = false)
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    val showBottomBar = currentRoute in Screen.bottomNavScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Screen 1: Login
            composable(Screen.Login.route) {
                val viewModel: LoginViewModel = viewModel(
                    factory = LoginViewModel.Factory(container.authRepository)
                )
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Screen 2: Home
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(container.authRepository, container.closetRepository)
                )
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToNewItem = { navController.navigate(Screen.ItemEdit.createRoute(null)) },
                    onNavigateToCreateOutfit = { weather, occasion ->
                        navController.navigate(Screen.CreateOutfit.createRoute(weather.name, occasion.name))
                    },
                    onNavigateToItemDetail = { id -> navController.navigate(Screen.ItemEdit.createRoute(id)) },
                    onNavigateToCloset = { navController.navigate(Screen.Items.route) },
                    onNavigateToOutfits = { navController.navigate(Screen.Outfits.route) },
                    onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) }
                )
            }

            // Screen 3: Items (Roupas / Closet)
            composable(Screen.Items.route) {
                val viewModel: ItemsViewModel = viewModel(
                    factory = ItemsViewModel.Factory(container.closetRepository)
                )
                ItemsScreen(
                    viewModel = viewModel,
                    onNavigateToNewItem = { navController.navigate(Screen.ItemEdit.createRoute(null)) },
                    onNavigateToItemDetail = { id -> navController.navigate(Screen.ItemEdit.createRoute(id)) }
                )
            }

            // Screen 4: Add / Edit Item
            composable(
                route = Screen.ItemEdit.route,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val itemIdArg = backStackEntry.arguments?.getLong("itemId")
                val itemId = if (itemIdArg != null && itemIdArg > 0) itemIdArg else null

                val viewModel: ItemEditViewModel = viewModel(
                    factory = ItemEditViewModel.Factory(itemId, container.closetRepository)
                )
                ItemEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Screen 5: Outfits
            composable(Screen.Outfits.route) {
                val viewModel: OutfitsViewModel = viewModel(
                    factory = OutfitsViewModel.Factory(container.closetRepository)
                )
                OutfitsScreen(
                    viewModel = viewModel,
                    onNavigateToCreateOutfit = { navController.navigate(Screen.CreateOutfit.createRoute()) },
                    onNavigateToOutfitDetail = { id -> navController.navigate(Screen.OutfitDetail.createRoute(id)) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            // Screen 6: Create Outfit
            composable(
                route = Screen.CreateOutfit.route,
                arguments = listOf(
                    navArgument("weather") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("occasion") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val weatherStr = backStackEntry.arguments?.getString("weather")
                val occasionStr = backStackEntry.arguments?.getString("occasion")

                val weather = weatherStr?.let { try { WeatherItem.valueOf(it) } catch (_: Exception) { null } }
                val occasion = occasionStr?.let { try { OccasionItem.valueOf(it) } catch (_: Exception) { null } }

                val viewModel: CreateOutfitViewModel = viewModel(
                    factory = CreateOutfitViewModel.Factory(
                        initialWeather = weather,
                        initialOccasion = occasion,
                        authRepository = container.authRepository,
                        closetRepository = container.closetRepository,
                        stylistEngine = container.stylistEngine
                    )
                )
                CreateOutfitScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOutfitSaved = { newOutfitId ->
                        navController.navigate(Screen.OutfitDetail.createRoute(newOutfitId)) {
                            popUpTo(Screen.CreateOutfit.route) { inclusive = true }
                        }
                    }
                )
            }

            // Screen 7: Outfit Detail
            composable(
                route = Screen.OutfitDetail.route,
                arguments = listOf(
                    navArgument("outfitId") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val outfitId = backStackEntry.arguments?.getLong("outfitId") ?: 0L

                val viewModel: OutfitDetailViewModel = viewModel(
                    factory = OutfitDetailViewModel.Factory(outfitId, container.closetRepository)
                )
                OutfitDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToItemDetail = { id -> navController.navigate(Screen.ItemEdit.createRoute(id)) }
                )
            }

            // Screen 8: History
            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = viewModel(
                    factory = HistoryViewModel.Factory(container.closetRepository)
                )
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Screen 9: Favorites
            composable(Screen.Favorites.route) {
                val viewModel: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModel.Factory(container.closetRepository)
                )
                FavoritesScreen(
                    viewModel = viewModel,
                    onNavigateToItemDetail = { id -> navController.navigate(Screen.ItemEdit.createRoute(id)) },
                    onNavigateToOutfitDetail = { id -> navController.navigate(Screen.OutfitDetail.createRoute(id)) }
                )
            }

            // Screen 10: Profile
            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(container.authRepository, container.closetRepository)
                )
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
