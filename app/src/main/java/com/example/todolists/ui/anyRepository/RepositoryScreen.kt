package com.example.todolists.ui.anyRepository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.R
import com.example.todolists.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
    var itemDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.repository_title, repoId)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_desc))
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_repository_desc))
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
                        
                        var showDatePicker by remember { mutableStateOf(false) }
                        var showTimePicker by remember { mutableStateOf(false) }
                        val dateState = rememberDatePickerState()
                        val timeState = rememberTimePickerState()
                        var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(itemDateTime?.toString() ?: stringResource(R.string.select_datetime_button))
                        }

                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            dateState.selectedDateMillis?.let { millis ->
                                                selectedDate = Instant.ofEpochMilli(millis)
                                                    .atZone(ZoneId.systemDefault())
                                                    .toLocalDate()
                                                showDatePicker = false
                                                showTimePicker = true
                                            }
                                        },
                                        enabled = dateState.selectedDateMillis != null
                                    ) {
                                        Text(stringResource(R.string.next_button))
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text(stringResource(R.string.cancel_button))
                                    }
                                }
                            ) {
                                DatePicker(state = dateState)
                            }
                        }

                        if (showTimePicker && selectedDate != null) {
                            Dialog(
                                onDismissRequest = { showTimePicker = false },
                                properties = DialogProperties(usePlatformDefaultWidth = false)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TimePicker(
                                            state = timeState,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { showTimePicker = false }) {
                                                Text(stringResource(R.string.cancel_button))
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            TextButton(
                                                onClick = {
                                                    itemDateTime = LocalDateTime.of(
                                                        selectedDate!!.year,
                                                        selectedDate!!.month,
                                                        selectedDate!!.dayOfMonth,
                                                        timeState.hour,
                                                        timeState.minute
                                                    )
                                                    itemTime = itemDateTime!!.toEpochSecond(ZoneOffset.UTC)
                                                    println("Saved datetime: $itemDateTime, timestamp: $itemTime")
                                                    showTimePicker = false
                                                }
                                            ) {
                                                Text(stringResource(R.string.confirm_button))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addItem(repoId, itemTitle, itemDescribe, itemTime, itemDateTime)
                            itemTitle = ""
                            itemDescribe = ""
                            itemTime = 0L
                            itemDateTime = null
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

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_repository_title)) },
                text = { Text(stringResource(R.string.delete_repository_message, repoId)) },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.deleteRepository(repoId)
                                showDeleteDialog = false
                                onBack()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.delete_button))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}
