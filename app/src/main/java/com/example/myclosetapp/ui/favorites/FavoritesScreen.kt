package com.example.myclosetapp.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myclosetapp.ui.components.ClothingItemCard
import com.example.myclosetapp.ui.components.OutfitCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToItemDetail: (Long) -> Unit,
    onNavigateToOutfitDetail: (Long) -> Unit
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val favoriteItems by viewModel.favoriteItems.collectAsState()
    val favoriteOutfitsWithItems by viewModel.favoriteOutfitsWithItems.collectAsState()

    val tabs = listOf("Peças Favoritas", "Looks Favoritos")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Favoritos",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTabIndex == index),
                    onClick = { viewModel.selectTab(index) },
                    text = { Text(title, style = MaterialTheme.typography.titleSmall) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> {
                // Favorite Clothing Items
                if (favoriteItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma peça favoritada ainda.\nClique no coração nas peças para salvá-las aqui!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoriteItems, key = { it.id }) { item ->
                            ClothingItemCard(
                                item = item,
                                onClick = { onNavigateToItemDetail(item.id) },
                                onFavoriteToggle = { viewModel.toggleItemFavorite(item) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Favorite Outfits
                if (favoriteOutfitsWithItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum look favoritado ainda.\nClique no coração nos seus looks para salvá-los aqui!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoriteOutfitsWithItems, key = { it.outfit.id }) { item ->
                            OutfitCard(
                                outfit = item.outfit,
                                items = item.items,
                                onClick = { onNavigateToOutfitDetail(item.outfit.id) },
                                onFavoriteToggle = { viewModel.toggleOutfitFavorite(item.outfit) }
                            )
                        }
                    }
                }
            }
        }
    }
}
