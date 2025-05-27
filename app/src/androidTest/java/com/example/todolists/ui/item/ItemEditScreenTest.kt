package com.example.todolists.ui.item

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolists.data.ToDoItem
import com.example.todolists.data.ToDoListRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ItemEditViewModel
    private val mockRepository: ToDoListRepository = mockk()
    private val testItem = ToDoItem(title = "Test", describe = "Test Description")

    @Before
    fun setup() {
        coEvery { mockRepository.insertItem(any(), any()) } returns Unit
        coEvery { mockRepository.updateItem(any(), any()) } returns Unit
        
        viewModel = ItemEditViewModel(mockRepository)
        viewModel._uiState.value = ItemEditState(
            repoId = "testRepo",
            item = testItem,
            isNew = false
        )
    }

    @Test
    fun editAndSaveItem_success() {
        // Launch the screen
        composeTestRule.setContent {
            ItemEditScreen(
                repoId = "testRepo",
                itemId = 1,
                onBack = {}
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("编辑待办").assertExists()
        composeTestRule.onNodeWithText("Test").assertExists()
        composeTestRule.onNodeWithText("Test Description").assertExists()

        // Edit title
        composeTestRule.onNodeWithText("标题")
            .performTextInput("Updated Title")

        // Edit description
        composeTestRule.onNodeWithText("描述")
            .performTextInput("Updated Description")

        // Click save button
        composeTestRule.onNodeWithText("保存")
            .performClick()

        // Verify saving indicator shows
        composeTestRule.onNodeWithText("保存").assertDoesNotExist()
    }

    @Test
    fun createNewItem_success() {
        viewModel._uiState.value = ItemEditState(
            repoId = "testRepo",
            item = ToDoItem(title = "", describe = ""),
            isNew = true
        )

        composeTestRule.setContent {
            ItemEditScreen(
                repoId = "testRepo",
                itemId = 0,
                onBack = {}
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("新建待办").assertExists()

        // Enter title
        composeTestRule.onNodeWithText("标题")
            .performTextInput("New Item")

        // Enter description
        composeTestRule.onNodeWithText("描述")
            .performTextInput("New Description")

        // Click save button
        composeTestRule.onNodeWithText("保存")
            .performClick()
    }
}
