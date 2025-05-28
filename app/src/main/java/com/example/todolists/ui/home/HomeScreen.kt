package com.example.todolists.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.todolists.ui.AppViewModelProvider
import com.example.todolists.R

@Composable
fun HomeScreen(
    onNavigateToRepository: (repoId: String) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val repositories by viewModel.repositories.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var repositoryName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(repositories) { repoId ->
                Card(
                    onClick = { onNavigateToRepository(repoId) },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                        Text(
                        text = stringResource(R.string.repository_title, repoId),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.create_repository_dialog_title)) },
                text = {
                    TextField(
                        value = repositoryName,
                        onValueChange = { repositoryName = it },
                        label = { Text(stringResource(R.string.repository_name_hint)) }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.createNewRepository(repositoryName)
                            repositoryName = ""
                            showDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.create_button))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }

    FloatBotton(stringResource(R.string.add_repository_fab)) {
        showDialog = true
    }
}

@Composable
fun FloatBotton(description: String, onClick:()->Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = description)
        }
    }
}
