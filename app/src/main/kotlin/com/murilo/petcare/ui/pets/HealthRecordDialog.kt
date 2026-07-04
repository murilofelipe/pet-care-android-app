package com.murilo.petcare.ui.pets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.murilo.petcare.data.remote.dto.HealthRecordRequest
import com.murilo.petcare.ui.common.isValidIsoDateOrBlank
import java.time.LocalDate

/**
 * Formulário de registro de saúde.
 * type = "VACINA": descrição + data + próxima dose + lote + aplicado por.
 * type = "MEDICACAO": nome + dosagem + frequência + início + fim.
 */
@Composable
fun HealthRecordDialog(
    type: String,
    onDismiss: () -> Unit,
    onSave: (HealthRecordRequest) -> Unit,
) {
    val isVaccine = type == "VACINA"

    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var nextDueDate by remember { mutableStateOf("") }
    var vaccineBatch by remember { mutableStateOf("") }
    var appliedBy by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isVaccine) "Registrar vacina" else "Registrar medicação") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isVaccine) "Tipo de vacina *" else "Nome da medicação *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(if (isVaccine) "Data de aplicação * (aaaa-mm-dd)" else "Início * (aaaa-mm-dd)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                if (isVaccine) {
                    OutlinedTextField(
                        value = nextDueDate,
                        onValueChange = { nextDueDate = it },
                        label = { Text("Próxima dose (aaaa-mm-dd)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = vaccineBatch,
                        onValueChange = { vaccineBatch = it },
                        label = { Text("Lote") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = appliedBy,
                        onValueChange = { appliedBy = it },
                        label = { Text("Local/veterinário responsável") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosagem (ex.: 5 mg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text("Frequência (ex.: a cada 12h)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fim do tratamento (aaaa-mm-dd)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("Custo (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações") },
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
                val costValue = cost.trim().replace(',', '.')
                error = when {
                    description.isBlank() -> "Informe a descrição"
                    date.isBlank() || !date.isValidIsoDateOrBlank() -> "Data inválida (use aaaa-mm-dd)"
                    !nextDueDate.isValidIsoDateOrBlank() -> "Próxima dose inválida (use aaaa-mm-dd)"
                    !endDate.isValidIsoDateOrBlank() -> "Data fim inválida (use aaaa-mm-dd)"
                    costValue.isNotBlank() && costValue.toDoubleOrNull() == null -> "Custo inválido"
                    else -> null
                }
                if (error == null) {
                    onSave(
                        HealthRecordRequest(
                            type = type,
                            description = description.trim(),
                            date = date.trim(),
                            nextDueDate = nextDueDate.trim().ifBlank { null },
                            cost = costValue.toDoubleOrNull(),
                            notes = notes.trim().ifBlank { null },
                            vaccineBatch = vaccineBatch.trim().ifBlank { null },
                            appliedBy = appliedBy.trim().ifBlank { null },
                            dosage = dosage.trim().ifBlank { null },
                            frequency = frequency.trim().ifBlank { null },
                            endDate = endDate.trim().ifBlank { null },
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