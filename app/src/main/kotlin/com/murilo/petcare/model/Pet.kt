package com.murilo.petcare.model

import java.util.UUID

/**
 * Define a finalidade do animal na chácara.
 * Essencial para separar pets domésticos de animais de produção ou consumo.
 */
enum class CreationCategory(val label: String) {
    DOMESTICO("Pet Doméstico"),
    PRODUCAO("Produção/Gado"),
    SUBSISTENCIA("Consumo/Subsistência"),
    LAZER("Lazer/Estimação Rural")
}

/**
 * Modelo principal para gestão de animais e lotes.
 * Suporta genealogia para evitar consanguinidade e rastreabilidade.
 */
data class Pet(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,              // Nome individual ou identificação do lote
    val species: String,                   // Ex: Cão, Gato, Gado, Galinha, Tilápia
    val breed: String? = null,             // Raça (opcional)
    val category: CreationCategory = CreationCategory.DOMESTICO,

    // Gestão de Grupos/Lotes
    val isGroup: Boolean = false,          // Define se o registro é um lote ou um animal único
    val quantity: Int = 1,                 // Quantidade (relevante para aves e peixes)

    // Identificação e Saúde
    val identifier: String? = null,        // Brinco, anilha ou número de registro
    val birthDate: Long? = null,           // Timestamp para cálculos de idade e vacinas
    val photoUri: String? = null,          // URI da imagem capturada no celular

    // Genealogia (Rastreabilidade de Pedigree)
    val fatherId: String? = null,          // Referência ao ID do pai
    val motherId: String? = null           // Referência ao ID da mãe
)

/**
 * Função utilitária para calcular a idade de forma dinâmica.
 * Importante para o manejo correto na chácara.
 */
fun Pet.calculateAge(): String {
    if (birthDate == null) return "Idade não informada"

    val diff = System.currentTimeMillis() - birthDate
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days < 30 -> "$days dias"
        days < 365 -> "${days / 30} meses"
        else -> {
            val years = days / 365
            val remainingMonths = (days % 365) / 30
            if (remainingMonths > 0) "$years anos e $remainingMonths meses" else "$years anos"
        }
    }
}