package com.example.todolists.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolists.ui.AppViewModelProvider
import java.util.Calendar

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
    val timePickerState = rememberTimePickerState(
        initialHour = if (uiState.item.time > 0) {
            ((uiState.item.time / 3600) % 24).toInt()
        } else {
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        },
        initialMinute = if (uiState.item.time > 0) {
            ((uiState.item.time / 60) % 60).toInt()
        } else {
            Calendar.getInstance().get(Calendar.MINUTE)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNew) "新建待办" else "编辑待办") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
                        label = { Text("标题") },
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
                        label = { Text("描述") },
                        supportingText = { Text("${currentItem.describe.length}/200") },
                        singleLine = false,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TimePicker(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val timeInSeconds = 
                                timePickerState.hour * 3600L + 
                                timePickerState.minute * 60L
                            val updatedItem = currentItem.copy(
                                time = timeInSeconds
                            )
                            viewModel.saveItem(updatedItem)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("保存")
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
                            Text("删除")
                        }
                    }
                }
            }
        }
    }
}
