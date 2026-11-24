package com.example.myclosetapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myclosetapp.data.model.ColorItem
import com.example.myclosetapp.data.model.StyleItem
import com.example.myclosetapp.data.model.WeatherItem

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val name by viewModel.name.collectAsState()
    val favoriteStyles by viewModel.favoriteStyles.collectAsState()
    val favoriteColors by viewModel.favoriteColors.collectAsState()
    val defaultWeather by viewModel.defaultWeather.collectAsState()
    val avoidDaysThreshold by viewModel.avoidDaysThreshold.collectAsState()
    val itemsCount by viewModel.itemsCount.collectAsState()
    val outfitsCount by viewModel.outfitsCount.collectAsState()
    val isSavedMessageShown by viewModel.isSavedMessageShown.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isSavedMessageShown) {
        if (isSavedMessageShown) {
            snackbarHostState.showSnackbar("Perfil atualizado com sucesso! ✨")
            viewModel.dismissSavedMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Meu Perfil",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome do perfil") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // Counters summary
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$itemsCount", style = MaterialTheme.typography.titleLarge)
                        Text(text = "Peças no Closet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$outfitsCount", style = MaterialTheme.typography.titleLarge)
                        Text(text = "Looks Criados", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Favorite Styles
            Column {
                Text("Estilos de Roupas Favoritos", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(StyleItem.values()) { st ->
                        FilterChip(
                            selected = favoriteStyles.contains(st),
                            onClick = { viewModel.toggleStyle(st) },
                            label = { Text(st.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Favorite Colors
            Column {
                Text("Cores Preferidas", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ColorItem.values()) { c ->
                        FilterChip(
                            selected = favoriteColors.contains(c),
                            onClick = { viewModel.toggleColor(c) },
                            label = { Text(c.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Default Weather
            Column {
                Text("Clima Padrão Preferido", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(WeatherItem.values()) { w ->
                        FilterChip(
                            selected = (defaultWeather == w),
                            onClick = { viewModel.onWeatherSelect(w) },
                            label = { Text(w.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Avoid Repeat Days Slider / Selector (0 to 7)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Evitar repetir peças nos últimos N dias",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "O Stylist dará penalidade para peças usadas recentemente ($avoidDaysThreshold ${if (avoidDaysThreshold == 1) "dia" else "dias"}).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = avoidDaysThreshold.toFloat(),
                        onValueChange = { viewModel.onAvoidDaysChange(it.toInt()) },
                        valueRange = 0f..7f,
                        steps = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // History Shortcut
            OutlinedButton(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Histórico de Uso de Looks")
            }

            // Save Profile Button
            Button(
                onClick = viewModel::saveProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salvar Preferências")
            }

            // Logout Button
            TextButton(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sair da conta", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
