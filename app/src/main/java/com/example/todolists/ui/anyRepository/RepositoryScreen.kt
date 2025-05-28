package com.example.todolists.ui.anyRepository

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.ui.AppViewModelProvider
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import com.example.todolists.R

@RequiresApi(Build.VERSION_CODES.O)
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
                title = { Text(stringResource(R.string.repository_title, repoId)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_desc))
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
                            text = stringResource(R.string.item_title_format, item.title),
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
                title = { Text(stringResource(R.string.add_item_dialog_title)) },
                text = {
                    Column {
                        TextField(
                            value = itemTitle,
                            onValueChange = { itemTitle = it },
                            label = { Text(stringResource(R.string.item_title_hint)) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = itemDescribe,
                            onValueChange = { itemDescribe = it },
                            label = { Text(stringResource(R.string.item_description_hint)) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                            var showDateTimePicker by remember { mutableStateOf(false) }
                            
                            Button(
                                onClick = { showDateTimePicker = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (itemTime == 0L) stringResource(R.string.select_datetime_button) else stringResource(R.string.selected_datetime_format, LocalDateTime.ofEpochSecond(itemTime, 0, ZoneOffset.UTC)))
                            }
                            
                            if (showDateTimePicker) {
                                val dateState = rememberDatePickerState()
                                val timeState = rememberTimePickerState()
                                
                                AlertDialog(
                                    onDismissRequest = { showDateTimePicker = false },
                                    title = { Text(stringResource(R.string.select_datetime_button)) },
                                    text = {
                                        Column {
                                            DatePicker(state = dateState)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            TimePicker(state = timeState)
                                        }
                                    },
                                    confirmButton = {
                                        Button(onClick = {
                                            dateState.selectedDateMillis?.let { dateMillis ->
                                                val instant = Instant.ofEpochMilli(dateMillis)
                                                val zonedDateTime = instant.atZone(ZoneId.systemDefault())
                                                val localDate = zonedDateTime.toLocalDate()
                                                
                                                itemTime = LocalDateTime.of(
                                                    localDate.year,
                                                    localDate.month,
                                                    localDate.dayOfMonth,
                                                    timeState.hour,
                                                    timeState.minute
                                                ).toEpochSecond(ZoneOffset.UTC)
                                                
                                                showDateTimePicker = false
                                            }
                                        }) {
                                            Text(stringResource(R.string.confirm_button))
                                        }
                                    },
                                    dismissButton = {
                                        Button(onClick = { showDateTimePicker = false }) {
                                            Text(stringResource(R.string.cancel_button))
                                        }
                                    }
                                )
                            }
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
                        Text(stringResource(R.string.add_button))
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

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_item_fab_desc))
        }
    }
}
