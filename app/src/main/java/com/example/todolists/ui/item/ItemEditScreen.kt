package com.example.todolists.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    repoId: String,
    itemId: Long,
    onBack: () -> Unit
) {
    val viewModel : ItemEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
    viewModel.init(repoId, itemId)
    val nowItem = viewModel.item.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Editing: $itemId") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        OutlinedTextField(
            value = nowItem.title,
            onValueChange = { nowItem.title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        OutlinedTextField(
            value = nowItem.describe,
            onValueChange = { nowItem.describe = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = { viewModel.updateItem(repoId, nowItem) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Save")
        }

        Button(
            onClick = { viewModel.deleteItem(repoId, nowItem)
                      onBack()},
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Delete")
        }
    }
}