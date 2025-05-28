package com.example.todolists

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todolists.ui.navigation.ToDoListsNavGraph
import com.example.todolists.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberNavController()
                ToDoListsNavGraph(navController, modifier = Modifier)
            }
        }
    }
}