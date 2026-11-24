package com.example.myclosetapp.ui.itemdetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.ui.components.ClothingImageThumbnail
import com.example.myclosetapp.ui.components.ConfirmationDialog
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    viewModel: ItemEditViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val name by viewModel.name.collectAsState()
    val category by viewModel.category.collectAsState()
    val color by viewModel.color.collectAsState()
    val pattern by viewModel.pattern.collectAsState()
    val selectedStyles by viewModel.styles.collectAsState()
    val selectedOccasions by viewModel.occasions.collectAsState()
    val selectedSeasons by viewModel.seasons.collectAsState()
    val brand by viewModel.brand.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val imagePath by viewModel.imagePath.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showPhotoOptions by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImagePicked(context, it) }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            viewModel.onImagePicked(context, tempCameraUri!!)
        }
    }

    fun launchCamera() {
        try {
            val photoFile = File.createTempFile("camera_temp_", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Editar Peça" else "Nova Peça") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (viewModel.isEditing) {
                        IconButton(onClick = viewModel::onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir peça", tint = MaterialTheme.colorScheme.error)
                        }
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
            // Photo Picker Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { showPhotoOptions = true },
                contentAlignment = Alignment.Center
            ) {
                ClothingImageThumbnail(
                    imagePath = imagePath,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (imagePath == null) "Adicionar foto" else "Alterar foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
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

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome da peça *") },
                placeholder = { Text("Ex: Camisa Off-White de Seda") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // Category (Single Select)
            Column {
                Text("Categoria", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Category.values()) { cat ->
                        FilterChip(
                            selected = (category == cat),
                            onClick = { viewModel.onCategoryChange(cat) },
                            label = { Text(cat.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Color (Single Select)
            Column {
                Text("Cor principal", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ColorItem.values()) { c ->
                        FilterChip(
                            selected = (color == c),
                            onClick = { viewModel.onColorChange(c) },
                            label = { Text(c.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Pattern (Single Select)
            Column {
                Text("Estampa / Tecido", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PatternItem.values()) { p ->
                        FilterChip(
                            selected = (pattern == p),
                            onClick = { viewModel.onPatternChange(p) },
                            label = { Text(p.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Styles (Multi Select)
            Column {
                Text("Estilos", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(StyleItem.values()) { st ->
                        FilterChip(
                            selected = selectedStyles.contains(st),
                            onClick = { viewModel.toggleStyle(st) },
                            label = { Text(st.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Occasions (Multi Select)
            Column {
                Text("Ocasiões", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OccasionItem.values()) { occ ->
                        FilterChip(
                            selected = selectedOccasions.contains(occ),
                            onClick = { viewModel.toggleOccasion(occ) },
                            label = { Text(occ.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Seasons (Multi Select)
            Column {
                Text("Estações recomendadas", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SeasonItem.values()) { se ->
                        FilterChip(
                            selected = selectedSeasons.contains(se),
                            onClick = { viewModel.toggleSeason(se) },
                            label = { Text(se.label) },
                            shape = CircleShape
                        )
                    }
                }
            }

            // Brand
            OutlinedTextField(
                value = brand,
                onValueChange = viewModel::onBrandChange,
                label = { Text("Marca (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Observações / Dicas de lavagem") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp)
            )

            // Favorite Toggle
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("Marcar como peça favorita", style = MaterialTheme.typography.titleSmall)
                    }
                    Switch(
                        checked = isFavorite,
                        onCheckedChange = viewModel::onFavoriteChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveItem(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Salvar peça", style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    // Photo Source Dialog
    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Selecionar foto", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Escolha de onde deseja carregar a imagem da peça:") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    launchCamera()
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tirar foto (Câmera)")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Escolher da Galeria")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Excluir peça",
            message = "Tem certeza que deseja excluir esta peça? Ela será removida permanentemente do seu guarda-roupa.",
            onConfirm = { viewModel.confirmDelete(onNavigateBack) },
            onDismiss = viewModel::dismissDeleteDialog
        )
    }
}
