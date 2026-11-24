package com.example.myclosetapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Items : Screen("items", "Roupas", Icons.Default.Checkroom)
    object Outfits : Screen("outfits", "Looks", Icons.Default.Style)
    object Favorites : Screen("favorites", "Favoritos", Icons.Default.Favorite)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    object ItemEdit : Screen("item_edit?itemId={itemId}") {
        fun createRoute(itemId: Long? = null) = if (itemId != null) "item_edit?itemId=$itemId" else "item_edit"
    }

    object CreateOutfit : Screen("create_outfit?weather={weather}&occasion={occasion}") {
        fun createRoute(weatherName: String? = null, occasionName: String? = null): String {
            return "create_outfit?weather=${weatherName ?: ""}&occasion=${occasionName ?: ""}"
        }
    }

    object OutfitDetail : Screen("outfit_detail/{outfitId}") {
        fun createRoute(outfitId: Long) = "outfit_detail/$outfitId"
    }

    object History : Screen("history")

    companion object {
        val bottomNavScreens = listOf(Home, Items, Outfits, Favorites, Profile)
    }
}
