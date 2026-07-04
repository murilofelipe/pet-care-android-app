package com.murilo.petcare.ui.pets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.ui.common.EnumDropdown
import com.murilo.petcare.ui.common.Labels

/** Cadastro/edição de pet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    onBack: () -> Unit,
    viewModel: PetFormViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isEdit = viewModel.petId != null

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar pet" else "Novo pet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (state.loading) {
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                CircularProgressIndicator(Modifier.align(androidx.compose.ui.Alignment.Center))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { v -> viewModel.update { copy(name = v) } },
                label = { Text("Nome *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.species,
                onValueChange = { v -> viewModel.update { copy(species = v) } },
                label = { Text("Espécie * (ex.: Cão, Gato)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.breed,
                onValueChange = { v -> viewModel.update { copy(breed = v) } },
                label = { Text("Raça") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            EnumDropdown(
                label = "Sexo",
                options = Labels.genders,
                selected = state.gender,
                onSelect = { v -> viewModel.update { copy(gender = v) } },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.birthDate,
                onValueChange = { v -> viewModel.update { copy(birthDate = v) } },
                label = { Text("Nascimento (aaaa-mm-dd)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.weight,
                onValueChange = { v -> viewModel.update { copy(weight = v) } },
                label = { Text("Peso (kg)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.photoUri,
                onValueChange = { v -> viewModel.update { copy(photoUri = v) } },
                label = { Text("URL da foto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { v -> viewModel.update { copy(notes = v) } },
                label = { Text("Observações") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.error != null) {
                Text(
                    state.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Button(
                onClick = viewModel::save,
                enabled = !state.saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                if (state.saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(if (isEdit) "Salvar alterações" else "Cadastrar pet")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}