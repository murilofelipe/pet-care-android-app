package com.murilo.petcare.ui.pets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.data.local.HealthRecordEntity
import com.murilo.petcare.ui.common.ConfirmDeleteDialog
import com.murilo.petcare.ui.common.ErrorDialog
import com.murilo.petcare.ui.common.Labels
import com.murilo.petcare.ui.common.toAgeLabel
import com.murilo.petcare.ui.common.toBrDate
import com.murilo.petcare.ui.common.toMoney

/**
 * Detalhe do pet: ficha, histórico de saúde (vacinação/medicação) e,
 * para o tutor, gestão do acesso de veterinários.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: PetDetailViewModel = hiltViewModel(),
) {
    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val records by viewModel.healthRecords.collectAsStateWithLifecycle()
    val vetAccess by viewModel.vetAccess.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val deleted by viewModel.deleted.collectAsStateWithLifecycle()

    var recordDialogType by remember { mutableStateOf<String?>(null) } // VACINA | MEDICACAO
    var confirmDelete by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("TODOS") }

    val isShared = pet?.shared == true // aberto pela aba "Como vet"

    LaunchedEffect(deleted) {
        if (deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Pet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (!isShared) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { confirmDelete = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Ficha ────────────────────────────────────────────────────
            item {
                pet?.let { p ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoRow("Espécie", p.species)
                            if (!p.breed.isNullOrBlank()) InfoRow("Raça", p.breed)
                            if (!p.gender.isNullOrBlank()) InfoRow("Sexo", Labels.of(Labels.genders, p.gender))
                            p.birthDate.toAgeLabel()?.let { InfoRow("Idade", it) }
                            if (!p.birthDate.isNullOrBlank()) InfoRow("Nascimento", p.birthDate.toBrDate())
                            p.weight?.let { InfoRow("Peso", "%.1f kg".format(it)) }
                            if (isShared && !p.ownerName.isNullOrBlank()) InfoRow("Tutor", p.ownerName)
                            if (!p.notes.isNullOrBlank()) InfoRow("Obs.", p.notes)
                        }
                    }
                }
            }

            // ── Saúde ────────────────────────────────────────────────────
            item {
                Text("Saúde", style = MaterialTheme.typography.titleLarge)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { recordDialogType = "VACINA" },
                        enabled = !busy,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Vaccines, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Vacina")
                    }
                    Button(
                        onClick = { recordDialogType = "MEDICACAO" },
                        enabled = !busy,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Medicação")
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TODOS" to "Todos", "VACINA" to "Vacinas", "MEDICACAO" to "Medicações").forEach { (value, label) ->
                        FilterChip(
                            selected = filter == value,
                            onClick = { filter = value },
                            label = { Text(label) },
                        )
                    }
                }
            }

            val visibleRecords = if (filter == "TODOS") records else records.filter { it.type == filter }
            if (visibleRecords.isEmpty()) {
                item {
                    Text(
                        "Nenhum registro de saúde ainda.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                items(visibleRecords, key = { it.id }) { record ->
                    HealthRecordCard(record)
                }
            }

            // ── Veterinários (somente tutor) ─────────────────────────────
            if (!isShared) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Veterinários com acesso", style = MaterialTheme.typography.titleLarge)
                }
                item {
                    GrantVetAccessField(
                        enabled = !busy,
                        onGrant = viewModel::grantVetAccess,
                    )
                }
                if (vetAccess.isEmpty()) {
                    item {
                        Text(
                            "Nenhum veterinário vinculado. Informe o e-mail da conta do veterinário para conceder acesso.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                } else {
                    items(vetAccess, key = { it.id }) { access ->
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(access.vetName ?: access.vetEmail, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        access.vetEmail,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                IconButton(onClick = { viewModel.revokeVetAccess(access.id) }, enabled = !busy) {
                                    Icon(Icons.Default.PersonRemove, contentDescription = "Revogar acesso")
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    recordDialogType?.let { type ->
        HealthRecordDialog(
            type = type,
            onDismiss = { recordDialogType = null },
            onSave = { request ->
                recordDialogType = null
                viewModel.addHealthRecord(request)
            },
        )
    }

    ConfirmDeleteDialog(
        visible = confirmDelete,
        title = "Excluir pet",
        text = "O pet e seu histórico deixarão de aparecer no app. Confirmar?",
        onConfirm = {
            confirmDelete = false
            viewModel.deletePet()
        },
        onDismiss = { confirmDelete = false },
    )

    ErrorDialog(message = error, onDismiss = viewModel::clearError)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun HealthRecordCard(record: HealthRecordEntity) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(
                    onClick = {},
                    label = { Text(Labels.healthType(record.type)) },
                    leadingIcon = {
                        Icon(
                            if (record.type == "VACINA") Icons.Default.Vaccines else Icons.Default.Medication,
                            contentDescription = null,
                            Modifier.size(16.dp),
                        )
                    },
                )
                Spacer(Modifier.weight(1f))
                Text(
                    record.date.toBrDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(record.description, style = MaterialTheme.typography.titleSmall)

            record.nextDueDate?.takeIf { it.isNotBlank() }?.let {
                Detail("Próxima dose", it.toBrDate())
            }
            record.vaccineBatch?.takeIf { it.isNotBlank() }?.let { Detail("Lote", it) }
            record.appliedBy?.takeIf { it.isNotBlank() }?.let { Detail("Aplicado por", it) }
            record.dosage?.takeIf { it.isNotBlank() }?.let { Detail("Dosagem", it) }
            record.frequency?.takeIf { it.isNotBlank() }?.let { Detail("Frequência", it) }
            record.endDate?.takeIf { it.isNotBlank() }?.let { Detail("Fim do tratamento", it.toBrDate()) }
            record.cost?.let { Detail("Custo", it.toMoney()) }
            record.notes?.takeIf { it.isNotBlank() }?.let { Detail("Obs.", it) }
            record.recordedByName?.takeIf { it.isNotBlank() }?.let {
                Text(
                    "Registrado por $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun Detail(label: String, value: String) {
    Text(
        "$label: $value",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun GrantVetAccessField(enabled: Boolean, onGrant: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail do veterinário") },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        Button(
            onClick = {
                onGrant(email)
                email = ""
            },
            enabled = enabled && email.isNotBlank(),
        ) { Text("Convidar") }
    }
}