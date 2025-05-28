package com.example.todolists.ui.anyRepository

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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

    var showDialog by remember { mutableStateOf(false) }
    var itemTitle by remember { mutableStateOf("") }
    var itemDescribe by remember { mutableStateOf("") }
    var itemTime by remember { mutableStateOf(0L) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Item: ${item.title}",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("添加新Item") },
                text = {
                    Column {
                        TextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            label = { Text("Item标题") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = itemDescribe,
                            onValueChange = { itemDescribe = it },
                            label = { Text("Item描述") },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = if (itemTime == 0L) "" else {
                                val seconds = itemTime % 60
                                val minutes = (itemTime / 60) % 60
                                val hours = (itemTime / 3600)
                                "$hours:$minutes:$seconds"
                            },
                            onValueChange = { 
                                val parts = it.split(":")
                                if (parts.size == 3) {
                                    val hours = parts[0].toLongOrNull() ?: 0L
                                    val minutes = parts[1].toLongOrNull() ?: 0L
                                    val seconds = parts[2].toLongOrNull() ?: 0L
                                    itemTime = hours * 3600 + minutes * 60 + seconds
                                } else {
                                    itemTime = 0L
                                }
                            },
                            label = { Text("时间 (时:分:秒)") },
                            placeholder = { Text("例如: 12:30:45") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addItem(repoId, itemTitle, itemDescribe, itemTime)
                            itemTitle = ""
                            itemDescribe = ""
                            itemTime = 0L
                            showDialog = false
                        }
                    ) {
                        Text("添加")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false }
                    ) {
                        Text("取消")
                    }
                }
            )
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "添加Item")
        }
    }
}
