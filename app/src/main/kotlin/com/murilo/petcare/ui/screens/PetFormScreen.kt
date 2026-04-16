package com.murilo.petcare.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.murilo.petcare.model.Pet
import com.murilo.petcare.model.CreationCategory
import com.murilo.petcare.viewmodel.PetViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    viewModel: PetViewModel,
    petToEdit: Pet? = null,
    onNavigateBack: () -> Unit
) {
    // Estados redefinidos sempre que petToEdit mudar (Edit vs Create)
    var name by remember(petToEdit) { mutableStateOf(petToEdit?.name ?: "") }
    var species by remember(petToEdit) { mutableStateOf(petToEdit?.species ?: "") }
    var breed by remember(petToEdit) { mutableStateOf(petToEdit?.breed ?: "") }
    var category by remember(petToEdit) { mutableStateOf(petToEdit?.category ?: CreationCategory.DOMESTICO) }
    var isGroup by remember(petToEdit) { mutableStateOf(petToEdit?.isGroup ?: false) }
    var quantity by remember(petToEdit) { mutableStateOf(petToEdit?.quantity?.toString() ?: "1") }
    var photoUri by remember(petToEdit) { mutableStateOf(petToEdit?.photoUri) }
    var birthDate by remember(petToEdit) { mutableStateOf(petToEdit?.birthDate) }

    // Parentesco
    var fatherId by remember(petToEdit) { mutableStateOf(petToEdit?.fatherId ?: "") }
    var motherId by remember(petToEdit) { mutableStateOf(petToEdit?.motherId ?: "") }

    val scrollState = rememberScrollState()
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUri = it.toString() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (petToEdit == null) "Novo Registro" else "Editar ${petToEdit.name ?: "Animal"}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Seção de Foto
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Adicionar Foto", modifier = Modifier.size(40.dp))
                }
            }

            // Interruptor: Individual vs Lote
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Registro de Lote?")
                Spacer(Modifier.weight(1f))
                Switch(checked = isGroup, onCheckedChange = { isGroup = it })
            }

            if (isGroup) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantidade de animais") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Campos principais
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome ou Identificação (Ex: Luna ou Lote 01)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Espécie (Ex: Gado, Galinha, Cão)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Seleção de Categoria (Finalidade)
            Text("Finalidade da Criação", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CreationCategory.values().forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat.label) }
                    )
                }
            }

            // Genealogia (Pai e Mãe)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fatherId,
                    onValueChange = { fatherId = it },
                    label = { Text("ID do Pai") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = motherId,
                    onValueChange = { motherId = it },
                    label = { Text("ID da Mãe") },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    val petData = Pet(
                        id = petToEdit?.id ?: UUID.randomUUID().toString(),
                        name = name.ifBlank { null },
                        species = species,
                        category = category,
                        isGroup = isGroup,
                        quantity = quantity.toIntOrNull() ?: 1,
                        photoUri = photoUri,
                        birthDate = birthDate,
                        fatherId = fatherId.ifBlank { null },
                        motherId = motherId.ifBlank { null }
                    )
                    if (petToEdit == null) viewModel.addPet(petData) else viewModel.updatePet(petData)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = species.isNotBlank()
            ) {
                Text("Salvar Registro")
            }
        }
    }
}