package com.murilo.petcare.ui.pets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.murilo.petcare.data.local.PetEntity
import com.murilo.petcare.ui.common.ErrorDialog
import com.murilo.petcare.ui.common.Labels
import com.murilo.petcare.ui.common.toAgeLabel

/** Aba "Pets": lista dos pets do tutor logado. */
@Composable
fun PetListTab(
    modifier: Modifier = Modifier,
    onAddPet: () -> Unit,
    onOpenPet: (String) -> Unit,
    viewModel: PetListViewModel = hiltViewModel(),
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
                    Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Nenhum pet cadastrado ainda.\nToque em + para adicionar o primeiro.",
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
                    PetCard(pet = pet, onClick = { onOpenPet(pet.id) })
                }
                item { Spacer(Modifier.height(72.dp)) } // espaço para o FAB
            }
        }

        if (refreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        FloatingActionButton(
            onClick = onAddPet,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar pet")
        }
    }

    ErrorDialog(message = error, onDismiss = viewModel::clearError)
}

@Composable
fun PetCard(pet: PetEntity, onClick: () -> Unit, subtitle: String? = null) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (pet.photoUri.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                    )
                }
            } else {
                AsyncImage(
                    model = pet.photoUri,
                    contentDescription = pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium)
                val details = listOfNotNull(
                    pet.species,
                    pet.breed?.takeIf { it.isNotBlank() },
                    Labels.of(Labels.genders, pet.gender).takeIf { !pet.gender.isNullOrBlank() },
                ).joinToString(" • ")
                Text(
                    details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val extra = subtitle ?: pet.birthDate.toAgeLabel()
                if (!extra.isNullOrBlank()) {
                    Text(
                        extra,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}