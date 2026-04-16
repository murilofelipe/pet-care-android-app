package com.murilo.petcare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.murilo.petcare.model.Pet
import com.murilo.petcare.ui.components.PetCard
import com.murilo.petcare.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    viewModel: PetViewModel,
    onNavigateToForm: () -> Unit,
    onEditPet: (Pet) -> Unit
) {
    val petList by viewModel.pets.collectAsState()

    // Estados para seleção e diálogos
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var petToConfirmDelete by remember { mutableStateOf<Pet?>(null) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (selectedIds.isEmpty()) "Pets na Chácara 🐾" else "${selectedIds.size} selecionados")
                },
                actions = {
                    if (selectedIds.size > 1) {
                        IconButton(onClick = { showBulkDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir Selecionados", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (selectedIds.isEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = if (selectedIds.isEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Pet")
            }
        }
    ) { padding ->

        // Diálogo para exclusão individual
        petToConfirmDelete?.let { pet ->
            AlertDialog(
                onDismissRequest = { petToConfirmDelete = null },
                title = { Text("Excluir ${pet.name}?") },
                text = { Text("Tem certeza que deseja remover este pet da lista?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.removePet(pet.id)
                        petToConfirmDelete = null
                    }) { Text("Excluir", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { petToConfirmDelete = null }) { Text("Cancelar") }
                }
            )
        }

        // Diálogo para exclusão em lote
        if (showBulkDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showBulkDeleteDialog = false },
                title = { Text("Confirmar Exclusão em Lote") },
                text = { Text("Deseja excluir os ${selectedIds.size} pets selecionados?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedIds.forEach { viewModel.removePet(it) }
                        selectedIds = emptySet()
                        showBulkDeleteDialog = false
                    }) { Text("Excluir Todos", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showBulkDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(petList) { pet ->
                val isSelected = selectedIds.contains(pet.id)
                PetCard(
                    pet = pet,
                    isSelected = isSelected,
                    onToggleSelection = {
                        selectedIds = if (isSelected) selectedIds - pet.id else selectedIds + pet.id
                    },
                    onDeleteIndividual = { petToConfirmDelete = pet },
                    onEdit = { onEditPet(pet) }
                )
            }
        }
    }
}