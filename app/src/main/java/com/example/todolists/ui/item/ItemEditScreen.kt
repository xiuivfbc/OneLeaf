package com.example.todolists.ui.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.ui.AppViewModelProvider
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.foundation.layout.Spacer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import com.example.todolists.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    repoId: String,
    itemId: Long,
    onBack: () -> Unit
) {
    val viewModel: ItemEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState = viewModel.uiState.collectAsState().value
    
    LaunchedEffect(repoId, itemId) {
        viewModel.init(repoId, itemId)
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val (currentItem, setCurrentItem) = remember { 
        mutableStateOf(uiState.item.copy()) 
    }
    val showDatePickerDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNew) stringResource(R.string.new_todo_title) else stringResource(R.string.edit_todo_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_desc))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = currentItem.title,
                        onValueChange = { setCurrentItem(currentItem.copy(title = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.todo_title_hint)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentItem.describe,
                        onValueChange = { 
                            if (it.length <= 200) {
                                setCurrentItem(currentItem.copy(describe = it))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.todo_description_hint)) },
                        supportingText = { Text("${currentItem.describe.length}/200") },
                        singleLine = false,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDatePickerDialog.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currentItem.dateTime?.toString() ?: stringResource(R.string.select_datetime_button))
                    }

                    if (showDatePickerDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showDatePickerDialog.value = false },
                            title = { Text(stringResource(R.string.select_datetime_button)) },
                            text = {
                                // 简单实现日期时间选择
                                Column {
                                    val dateState = rememberDatePickerState()
                                    val timeState = rememberTimePickerState(
                                        initialHour = currentItem.dateTime?.hour ?: 0,
                                        initialMinute = currentItem.dateTime?.minute ?: 0
                                    )
                                    
                                    DatePicker(
                                        state = dateState,
                                        title = { Text(stringResource(R.string.select_date_title)) }
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    TimePicker(
                                        state = timeState
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Button(onClick = {
                                        dateState.selectedDateMillis?.let { dateMillis ->
                                            val date = Instant.ofEpochMilli(dateMillis)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                            viewModel.setSelectedDateTime(
                                                LocalDateTime.of(
                                                    date.year,
                                                    date.month,
                                                    date.dayOfMonth,
                                                    timeState.hour,
                                                    timeState.minute
                                                )
                                            )
                                            showDatePickerDialog.value = false
                                        }
                                    }) {
                                        Text(stringResource(R.string.confirm_button))
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { showDatePickerDialog.value = false }) {
                                    Text(stringResource(R.string.ok_button))
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val updatedItem = currentItem.copy(
                                time = currentItem.dateTime?.toEpochSecond(ZoneOffset.UTC) ?: 0L
                            )
                            viewModel.saveItem(updatedItem)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(stringResource(R.string.save_button))
                    }

                    if (!uiState.isNew) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteItem()
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                        Text(stringResource(R.string.delete_button))
                        }
                    }
                }
            }
        }
    }
}
