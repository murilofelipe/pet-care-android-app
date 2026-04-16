package com.murilo.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.murilo.petcare.model.Pet
import com.murilo.petcare.ui.screens.PetFormScreen
import com.murilo.petcare.ui.screens.PetListScreen
import com.murilo.petcare.ui.theme.PetCareTheme
import com.murilo.petcare.viewmodel.PetViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetCareTheme {
                // Instância única do ViewModel compartilhada entre as telas
                val petViewModel: PetViewModel = viewModel()

                // Estados de navegação e controle de edição
                var currentScreen by remember { mutableStateOf("list") }
                var petToEdit by remember { mutableStateOf<Pet?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "list" -> {
                            PetListScreen(
                                viewModel = petViewModel,
                                onNavigateToForm = {
                                    petToEdit = null // Garante que o formulário abra vazio para novo pet
                                    currentScreen = "form"
                                },
                                onEditPet = { pet ->
                                    petToEdit = pet // Define o pet que será carregado no formulário
                                    currentScreen = "form"
                                }
                            )
                        }
                        "form" -> {
                            PetFormScreen(
                                viewModel = petViewModel,
                                petToEdit = petToEdit,
                                onNavigateBack = {
                                    currentScreen = "list"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}