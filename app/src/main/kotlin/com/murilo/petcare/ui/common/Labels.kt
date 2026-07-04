package com.murilo.petcare.ui.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Rótulos pt-BR para os enums da API e utilitários de data. */
object Labels {

    val genders = listOf(
        "MACHO" to "Macho",
        "FEMEA" to "Fêmea",
        "NAO_APLICAVEL" to "Não se aplica",
    )

    val supplyCategories = listOf(
        "RACAO" to "Ração",
        "UTENSILIO" to "Utensílio",
        "HIGIENE" to "Higiene",
        "BRINQUEDO" to "Brinquedo",
        "OUTRO" to "Outro",
    )

    val expenseCategories = listOf(
        "RACAO" to "Ração",
        "VETERINARIO" to "Veterinário",
        "HIGIENE" to "Higiene",
        "MEDICACAO" to "Medicação",
        "BRINQUEDO" to "Brinquedo",
        "OUTRO" to "Outro",
    )

    fun of(options: List<Pair<String, String>>, value: String?): String =
        options.firstOrNull { it.first == value }?.second ?: value.orEmpty()

    fun healthType(value: String): String = when (value) {
        "VACINA" -> "Vacina"
        "MEDICACAO" -> "Medicação"
        "PESAGEM" -> "Pesagem"
        "EXAME" -> "Exame"
        "PARTO" -> "Parto"
        else -> "Outro"
    }
}

private val BR_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** true se vazio ou data ISO válida (aaaa-mm-dd). */
fun String.isValidIsoDateOrBlank(): Boolean =
    isBlank() || runCatching { LocalDate.parse(trim()) }.isSuccess

/** "2026-07-04" → "04/07/2026" (ou o valor original se não parsear). */
fun String?.toBrDate(): String {
    if (this.isNullOrBlank()) return ""
    return runCatching { LocalDate.parse(trim()).format(BR_DATE) }.getOrDefault(this)
}

/** Idade aproximada a partir de uma data ISO. */
fun String?.toAgeLabel(): String? {
    if (this.isNullOrBlank()) return null
    val birth = runCatching { LocalDate.parse(trim()) }.getOrNull() ?: return null
    val months = ChronoUnit.MONTHS.between(birth, LocalDate.now())
    return when {
        months < 1 -> "${ChronoUnit.DAYS.between(birth, LocalDate.now())} dias"
        months < 12 -> "$months meses"
        else -> {
            val years = months / 12
            val rest = months % 12
            if (rest > 0) "$years anos e $rest meses" else "$years anos"
        }
    }
}

/** Valor monetário para exibição. */
fun Double.toMoney(): String = "R$ %.2f".format(this)
