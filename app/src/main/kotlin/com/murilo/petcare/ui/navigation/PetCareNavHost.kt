package com.murilo.petcare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.murilo.petcare.data.auth.Session
import com.murilo.petcare.ui.home.HomeScreen
import com.murilo.petcare.ui.pets.PetDetailScreen
import com.murilo.petcare.ui.pets.PetFormScreen

/** Grafo de navegação do app autenticado. */
@Composable
fun PetCareNavHost(
    session: Session,
    onLogout: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                session = session,
                onLogout = onLogout,
                onAddPet = { navController.navigate("pet_form") },
                onOpenPet = { petId -> navController.navigate("pet_detail/$petId") },
            )
        }

        composable(
            route = "pet_form?petId={petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            PetFormScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(navArgument("petId") { type = NavType.StringType }),
        ) { entry ->
            val petId = entry.arguments?.getString("petId").orEmpty()
            PetDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("pet_form?petId=$petId") },
            )
        }
    }
}