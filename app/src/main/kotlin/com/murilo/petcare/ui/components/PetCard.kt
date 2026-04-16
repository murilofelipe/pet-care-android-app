package com.murilo.petcare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.murilo.petcare.model.Pet

@Composable
fun PetCard(
    pet: Pet,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onDeleteIndividual: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onToggleSelection() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para seleção múltipla
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Foto Principal do Animal ou Lote
            AsyncImage(
                model = pet.photoUri,
                contentDescription = "Foto de ${pet.name}",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                // Placeholder simples caso não tenha foto
                error = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Informações do Animal
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ícone indicando se é Lote ou Individual
                    Icon(
                        imageVector = if (pet.isGroup) Icons.Default.Groups else Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pet.name ?: "Sem Identificação",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${pet.species}${if (pet.isGroup) " (Lote: ${pet.quantity})" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Badge da Categoria (Finalidade)
                SuggestionChip(
                    onClick = { },
                    label = { Text(pet.category.label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(24.dp)
                )
            }

            // Ações: Editar e Excluir
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteIndividual) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}