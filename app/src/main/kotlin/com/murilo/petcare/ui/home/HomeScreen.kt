package com.murilo.petcare.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.murilo.petcare.data.auth.Session
import com.murilo.petcare.ui.expenses.ExpensesTab
import com.murilo.petcare.ui.pets.PetListTab
import com.murilo.petcare.ui.supplies.SuppliesTab
import com.murilo.petcare.ui.vet.VetTab

private enum class HomeTab(val label: String) {
    PETS("Pets"),
    SUPPLIES("Itens"),
    EXPENSES("Gastos"),
    VET("Como vet"),
}

/** Tela principal: abas de Pets, Utensílios/Ração, Financeiro e acesso de veterinário. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    session: Session,
    onLogout: () -> Unit,
    onAddPet: () -> Unit,
    onOpenPet: (String) -> Unit,
) {
    var currentTab by rememberSaveable { mutableStateOf(HomeTab.PETS) }
    var confirmLogout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Olá, ${session.userName.substringBefore(" ")}") },
                actions = {
                    IconButton(onClick = { confirmLogout = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                HomeTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        label = { Text(tab.label) },
                        icon = {
                            val icon = when (tab) {
                                HomeTab.PETS -> Icons.Default.Pets
                                HomeTab.SUPPLIES -> Icons.Default.Inventory2
                                HomeTab.EXPENSES -> Icons.Default.Payments
                                HomeTab.VET -> Icons.Default.MedicalServices
                            }
                            Icon(icon, contentDescription = tab.label)
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (currentTab) {
            HomeTab.PETS -> PetListTab(
                modifier = contentModifier,
                onAddPet = onAddPet,
                onOpenPet = onOpenPet,
            )

            HomeTab.SUPPLIES -> SuppliesTab(modifier = contentModifier)

            HomeTab.EXPENSES -> ExpensesTab(modifier = contentModifier)

            HomeTab.VET -> VetTab(modifier = contentModifier, onOpenPet = onOpenPet)
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text("Sair da conta") },
            text = { Text("Deseja encerrar a sessão? Os dados em cache serão limpos.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    onLogout()
                }) { Text("Sair") }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { Text("Cancelar") }
            },
        )
    }
}