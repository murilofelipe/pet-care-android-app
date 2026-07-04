package com.murilo.petcare.ui.supplies

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.data.local.SupplyEntity
import com.murilo.petcare.data.remote.dto.SupplyRequest
import com.murilo.petcare.ui.common.ConfirmDeleteDialog
import com.murilo.petcare.ui.common.EnumDropdown
import com.murilo.petcare.ui.common.ErrorDialog
import com.murilo.petcare.ui.common.Labels
import com.murilo.petcare.ui.common.isValidIsoDateOrBlank
import com.murilo.petcare.ui.common.toBrDate
import java.time.LocalDate

/** Aba "Itens": utensílios e ração, com data prevista de reposição. */
@Composable
fun SuppliesTab(
    modifier: Modifier = Modifier,
    viewModel: SuppliesViewModel = hiltViewModel(),
) {
    val supplies by viewModel.supplies.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var editing by remember { mutableStateOf<SupplyEntity?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<SupplyEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (supplies.isEmpty() && !busy) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Nenhum item cadastrado.\nControle ração e utensílios por aqui.",
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
                items(supplies, key = { it.id }) { supply ->
                    SupplyCard(
                        supply = supply,
                        onClick = {
                            editing = supply
                            showForm = true
                        },
                        onDelete = { deleting = supply },
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }

        if (busy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        FloatingActionButton(
            onClick = {
                editing = null
                showForm = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar item")
        }
    }

    if (showForm) {
        SupplyFormDialog(
            supply = editing,
            onDismiss = { showForm = false },
            onSave = { request ->
                showForm = false
                viewModel.save(editing?.id, request)
            },
        )
    }

    ConfirmDeleteDialog(
        visible = deleting != null,
        title = "Excluir item",
        text = "Remover \"${deleting?.name}\" da lista?",
        onConfirm = {
            deleting?.let { viewModel.delete(it.id) }
            deleting = null
        },
        onDismiss = { deleting = null },
    )

    ErrorDialog(message = error, onDismiss = viewModel::clearError)
}

@Composable
private fun SupplyCard(supply: SupplyEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    val dueSoon = supply.replacementDate
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?.let { !it.isAfter(LocalDate.now().plusDays(7)) } == true

    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(supply.name, style = MaterialTheme.typography.titleMedium)
                val details = listOfNotNull(
                    Labels.of(Labels.supplyCategories, supply.category),
                    supply.brand?.takeIf { it.isNotBlank() },
                    "Qtd: ${if (supply.quantity % 1.0 == 0.0) supply.quantity.toInt() else supply.quantity}",
                ).joinToString(" • ")
                Text(
                    details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!supply.replacementDate.isNullOrBlank()) {
                    Text(
                        "Repor em ${supply.replacementDate.toBrDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dueSoon) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun SupplyFormDialog(
    supply: SupplyEntity?,
    onDismiss: () -> Unit,
    onSave: (SupplyRequest) -> Unit,
) {
    var name by remember { mutableStateOf(supply?.name.orEmpty()) }
    var category by remember { mutableStateOf(supply?.category ?: "RACAO") }
    var brand by remember { mutableStateOf(supply?.brand.orEmpty()) }
    var quantity by remember { mutableStateOf(supply?.quantity?.toString().orEmpty()) }
    var purchaseDate by remember { mutableStateOf(supply?.purchaseDate.orEmpty()) }
    var replacementDate by remember { mutableStateOf(supply?.replacementDate.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (supply == null) "Novo item" else "Editar item") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                EnumDropdown(
                    label = "Categoria",
                    options = Labels.supplyCategories,
                    selected = category,
                    onSelect = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marca") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantidade") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = purchaseDate,
                    onValueChange = { purchaseDate = it },
                    label = { Text("Data de compra (aaaa-mm-dd)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = replacementDate,
                    onValueChange = { replacementDate = it },
                    label = { Text("Reposição prevista (aaaa-mm-dd)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(
                        error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val quantityValue = quantity.trim().replace(',', '.')
                error = when {
                    name.isBlank() -> "Informe o nome do item"
                    quantityValue.isNotBlank() && quantityValue.toDoubleOrNull() == null -> "Quantidade inválida"
                    !purchaseDate.isValidIsoDateOrBlank() -> "Data de compra inválida (use aaaa-mm-dd)"
                    !replacementDate.isValidIsoDateOrBlank() -> "Data de reposição inválida (use aaaa-mm-dd)"
                    else -> null
                }
                if (error == null) {
                    onSave(
                        SupplyRequest(
                            name = name.trim(),
                            category = category,
                            brand = brand.trim().ifBlank { null },
                            quantity = quantityValue.toDoubleOrNull(),
                            purchaseDate = purchaseDate.trim().ifBlank { null },
                            replacementDate = replacementDate.trim().ifBlank { null },
                        )
                    )
                }
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}