package com.example.myclosetapp.ui.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myclosetapp.ui.components.OutfitCard

@Composable
fun OutfitsScreen(
    viewModel: OutfitsViewModel,
    onNavigateToCreateOutfit: () -> Unit,
    onNavigateToOutfitDetail: (Long) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val outfitsWithItems by viewModel.outfitsWithItems.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateOutfit,
                icon = { Icon(Icons.Default.Add, contentDescription = "Montar look") },
                text = { Text("Montar look") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row with Title & History Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Meus Looks",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Histórico de uso",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (outfitsWithItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Você ainda não salvou nenhum look.\nToque em 'Montar look' para combinar suas peças!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(outfitsWithItems, key = { it.outfit.id }) { item ->
                        OutfitCard(
                            outfit = item.outfit,
                            items = item.items,
                            onClick = { onNavigateToOutfitDetail(item.outfit.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(item.outfit) }
                        )
                    }
                }
            }
        }
    }
}
