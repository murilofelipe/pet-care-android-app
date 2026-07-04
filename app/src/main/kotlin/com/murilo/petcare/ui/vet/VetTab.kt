package com.murilo.petcare.ui.vet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.ui.common.ErrorDialog
import com.murilo.petcare.ui.pets.PetCard

/**
 * Aba "Como vet": pets de outros tutores compartilhados com este usuário
 * via VetAnimalAccess. Ao abrir um pet, é possível registrar vacina/medicação.
 */
@Composable
fun VetTab(
    modifier: Modifier = Modifier,
    onOpenPet: (String) -> Unit,
    viewModel: VetSharedViewModel = hiltViewModel(),
) {
    val pets by viewModel.pets.collectAsStateWithLifecycle()
    val refreshing by viewModel.refreshing.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (pets.isEmpty() && !refreshing) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Nenhum pet compartilhado com você.\nQuando um tutor conceder acesso pelo seu e-mail, os pets aparecem aqui.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(pets, key = { it.id }) { pet ->
                    PetCard(
                        pet = pet,
                        onClick = { onOpenPet(pet.id) },
                        subtitle = pet.ownerName?.let { "Tutor: $it" },
                    )
                }
            }
        }

        if (refreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    ErrorDialog(message = error, onDismiss = viewModel::clearError)
}