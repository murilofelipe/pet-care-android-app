package com.murilo.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murilo.petcare.ui.login.LoginScreen
import com.murilo.petcare.ui.navigation.PetCareNavHost
import com.murilo.petcare.ui.session.SessionState
import com.murilo.petcare.ui.session.SessionViewModel
import com.murilo.petcare.ui.theme.PetCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PetCareApp()
                }
            }
        }
    }
}

@Composable
private fun PetCareApp(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val state by sessionViewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        SessionState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        SessionState.LoggedOut -> LoginScreen()

        is SessionState.LoggedIn -> PetCareNavHost(
            session = s.session,
            onLogout = sessionViewModel::logout,
        )
    }
}