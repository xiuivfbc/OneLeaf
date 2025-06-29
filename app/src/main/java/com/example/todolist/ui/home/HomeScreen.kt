package com.example.todolist.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.R
import com.example.todolist.ui.AppViewModelProvider
import com.example.todolist.utils.IconManager

@Composable
fun HomeScreen(
    onNavigateToRepository: (repoId: String) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val repositories by viewModel.repositories.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var repositoryName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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

    FloatBotton(
        description = stringResource(R.string.add_repository_fab),
        onClick = { showDialog = true },
        alignment = Alignment.BottomEnd,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(
                    R.string.add_repository_fab
                )
            )
        }
    )

    val context = LocalContext.current
    FloatBotton(
        description = stringResource(R.string.change_icon_button),
        onClick = {
            Toast.makeText(
                context,
                context.getString(R.string.change_icon_toast_message),
                Toast.LENGTH_SHORT
            ).show()
            IconManager(context = context).updateAppIcon()
        },
        alignment = Alignment.BottomStart,
        icon = {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.change_icon_button)
            )
        }
    )
}

@Composable
fun FloatBotton(
    description: String,
    onClick: () -> Unit,
    alignment: Alignment = Alignment.BottomEnd,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = description
        )
    }
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .align(alignment)
                .padding(16.dp)
        ) {
            icon()
        }
    }
}
