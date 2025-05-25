package com.example.todolists.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todolists.ui.AppViewModelProvider

@Composable
fun HomeScreen(
    onNavigateToRepository: (repoId: String) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val repositories by viewModel.repositories.collectAsState(initial = emptyList())

    LazyColumn {
        items(repositories) { repoId ->
            Card(
                onClick = { onNavigateToRepository(repoId) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Repository: $repoId",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
