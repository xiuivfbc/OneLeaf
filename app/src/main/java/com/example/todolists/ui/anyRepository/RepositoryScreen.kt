package com.example.todolists.ui.anyRepository

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryScreen(
    repoId: String,
    onNavigateToItem: (itemId: Long) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel: RepositoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
    viewModel.loadItems(repoId)
    val items by viewModel.items.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Repository: $repoId") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        LazyColumn {
            items(items) { item ->
                Card(
                    onClick = { onNavigateToItem(item.id) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Item: ${item.title}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
