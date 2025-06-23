package com.example.todolists.ui.item

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.R
import com.example.todolists.ui.AppViewModelProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
    val context = LocalContext.current

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

    // 日期时间选择状态
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState()
    val timeState = rememberTimePickerState(
        initialHour = currentItem.dateTime?.hour ?: 0,
        initialMinute = currentItem.dateTime?.minute ?: 0
    )
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isNew) stringResource(R.string.new_todo_title)
                        else stringResource(R.string.edit_todo_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
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
                    // 标题输入框
                    OutlinedTextField(
                        value = currentItem.title,
                        onValueChange = { setCurrentItem(currentItem.copy(title = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.todo_title_hint)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 描述输入框
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

                    // 日期时间选择按钮
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            currentItem.dateTime?.toString()
                                ?: stringResource(R.string.select_datetime_button)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 保存按钮
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

                    // 删除按钮（仅编辑模式显示）
                    if (!uiState.isNew) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteItem()
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6F61)
                            )
                        ) {
                            Text(stringResource(R.string.delete_button))
                        }
                    }
                }
            }
        }
    }

    // 日期选择对话框
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            val selectedDate_ = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val today = LocalDate.now()

                            if (selectedDate_!!.isBefore(today)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.date_error_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                selectedDate = selectedDate_
                                showDatePicker = false
                                showTimePicker = true
                            }
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

    if (showTimePicker) {
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
                                val newDateTime = LocalDateTime.of(
                                    selectedDate!!.year,
                                    selectedDate!!.month,
                                    selectedDate!!.dayOfMonth,
                                    timeState.hour,
                                    timeState.minute
                                )
                                val now = LocalDateTime.now()
                                
                                if (selectedDate!!.isEqual(LocalDate.now()) && 
                                    newDateTime.isBefore(now)) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.time_error_message),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    setCurrentItem(currentItem.copy(dateTime = newDateTime))
                                    viewModel.setSelectedDateTime(newDateTime)
                                    showTimePicker = false
                                }
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
