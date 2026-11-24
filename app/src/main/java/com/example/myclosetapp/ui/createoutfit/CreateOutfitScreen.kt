package com.example.myclosetapp.ui.createoutfit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.ui.components.ClothingItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOutfitScreen(
    viewModel: CreateOutfitViewModel,
    onNavigateBack: () -> Unit,
    onOutfitSaved: (Long) -> Unit
) {
    val selectedOccasion by viewModel.selectedOccasion.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedWeather by viewModel.selectedWeather.collectAsState()
    val outfitName by viewModel.outfitName.collectAsState()
    val selectedItemIds by viewModel.selectedItemIds.collectAsState()
    val suggestions by viewModel.suggestions.collectAsState()
    val selectedSuggestion by viewModel.selectedSuggestion.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Montar Look") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Notice Card: Local Rules (Not AI)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "As sugestões são geradas por regras locais no aparelho, analisando paleta de cores, ocasião e seu histórico de uso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Occasion Selector
            Column {
                Text("Ocasião do look", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OccasionItem.values()) { occ ->
                        FilterChip(
                            selected = (selectedOccasion == occ),
                            onClick = { viewModel.onOccasionSelect(occ) },
                            label = { Text(occ.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Style Selector
            Column {
                Text("Estilo desejado", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(StyleItem.values()) { st ->
                        FilterChip(
                            selected = (selectedStyle == st),
                            onClick = { viewModel.onStyleSelect(st) },
                            label = { Text(st.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Weather Selector
            Column {
                Text("Clima / Estação", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(WeatherItem.values()) { w ->
                        FilterChip(
                            selected = (selectedWeather == w),
                            onClick = { viewModel.onWeatherSelect(w) },
                            label = { Text(w.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Suggest Button
            Button(
                onClick = viewModel::generateSuggestions,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sugerir Combinações do Stylist")
            }

            // Suggestions Carousel
            if (suggestions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sugestões Geradas", style = MaterialTheme.typography.titleLarge)

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(suggestions) { suggestion ->
                            val isSelected = suggestion == selectedSuggestion
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { viewModel.selectSuggestion(suggestion) }
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Score: ${suggestion.score} pts",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = "Selecionado", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "${suggestion.items.size} peças no look",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    suggestion.matchReasons.take(2).forEach { reason ->
                                        Text(
                                            text = "• $reason",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // Manual Item Selection by Category
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Seleção Manual de Peças (${selectedItemIds.size} selecionadas)",
                    style = MaterialTheme.typography.titleLarge
                )

                Category.values().forEach { cat ->
                    val categoryItems = allItems.filter { it.category == cat }
                    if (categoryItems.isNotEmpty()) {
                        Column {
                            Text(
                                text = cat.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(categoryItems, key = { it.id }) { item ->
                                    val isSelected = selectedItemIds.contains(item.id)
                                    ClothingItemCard(
                                        item = item,
                                        isSelected = isSelected,
                                        onClick = { viewModel.toggleItemSelection(item) },
                                        modifier = Modifier.width(140.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Name & Save Section
            OutlinedTextField(
                value = outfitName,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome do Look") },
                placeholder = { Text("Ex: Look Trabalho Casual") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = { viewModel.saveOutfit(onOutfitSaved) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Salvar Look", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
