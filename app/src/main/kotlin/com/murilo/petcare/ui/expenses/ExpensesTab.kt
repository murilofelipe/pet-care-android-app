package com.murilo.petcare.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.data.local.ExpenseEntity
import com.murilo.petcare.data.local.PetEntity
import com.murilo.petcare.data.remote.dto.ExpenseRequest
import com.murilo.petcare.ui.common.ConfirmDeleteDialog
import com.murilo.petcare.ui.common.EnumDropdown
import com.murilo.petcare.ui.common.ErrorDialog
import com.murilo.petcare.ui.common.Labels
import com.murilo.petcare.ui.common.isValidIsoDateOrBlank
import com.murilo.petcare.ui.common.toBrDate
import com.murilo.petcare.ui.common.toMoney
import java.time.LocalDate

/** Aba "Gastos": financeiro doméstico com os pets. */
@Composable
fun ExpensesTab(
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = hiltViewModel(),
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val pets by viewModel.pets.collectAsStateWithLifecycle()
    val monthTotal by viewModel.monthTotal.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var showForm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<ExpenseEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Total do mês",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(monthTotal.toMoney(), style = MaterialTheme.typography.headlineMedium)
                }
            }

            if (expenses.isEmpty() && !busy) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Nenhum gasto registrado.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseCard(expense = expense, onDelete = { deleting = expense })
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }

        if (busy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        FloatingActionButton(
            onClick = { showForm = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar gasto")
        }
    }

    if (showForm) {
        ExpenseFormDialog(
            pets = pets,
            onDismiss = { showForm = false },
            onSave = { request ->
                showForm = false
                viewModel.create(request)
            },
        )
    }

    ConfirmDeleteDialog(
        visible = deleting != null,
        title = "Excluir gasto",
        text = "Remover \"${deleting?.description}\"?",
        onConfirm = {
            deleting?.let { viewModel.delete(it.id) }
            deleting = null
        },
        onDismiss = { deleting = null },
    )

    ErrorDialog(message = error, onDismiss = viewModel::clearError)
}

@Composable
private fun ExpenseCard(expense: ExpenseEntity, onDelete: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(expense.description, style = MaterialTheme.typography.titleMedium)
                    if (expense.recurring) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Repeat,
                            contentDescription = "Recorrente",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                val details = listOfNotNull(
                    Labels.of(Labels.expenseCategories, expense.category),
                    expense.date.toBrDate(),
                    expense.petName?.takeIf { it.isNotBlank() },
                ).joinToString(" • ")
                Text(
                    details,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(expense.amount.toMoney(), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir")
            }
        }
    }
}

@Composable
private fun ExpenseFormDialog(
    pets: List<PetEntity>,
    onDismiss: () -> Unit,
    onSave: (ExpenseRequest) -> Unit,
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("RACAO") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var recurring by remember { mutableStateOf(false) }
    var petId by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val petOptions = listOf("" to "Nenhum") + pets.map { it.id to it.name }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo gasto") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Valor (R$) *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                EnumDropdown(
                    label = "Categoria",
                    options = Labels.expenseCategories,
                    selected = category,
                    onSelect = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data * (aaaa-mm-dd)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                EnumDropdown(
                    label = "Pet (opcional)",
                    options = petOptions,
                    selected = petId.orEmpty(),
                    onSelect = { petId = it.ifBlank { null } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = recurring, onCheckedChange = { recurring = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Gasto recorrente")
                }
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
                val amountValue = amount.trim().replace(',', '.').toDoubleOrNull()
                error = when {
                    description.isBlank() -> "Informe a descrição"
                    amountValue == null || amountValue <= 0 -> "Valor inválido"
                    date.isBlank() || !date.isValidIsoDateOrBlank() -> "Data inválida (use aaaa-mm-dd)"
                    else -> null
                }
                if (error == null) {
                    onSave(
                        ExpenseRequest(
                            description = description.trim(),
                            amount = amountValue!!,
                            category = category,
                            date = date.trim(),
                            recurring = recurring,
                            animalId = petId,
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