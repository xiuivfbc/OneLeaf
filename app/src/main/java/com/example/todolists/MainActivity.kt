package com.example.todolists

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.todolists.ui.navigation.ToDoListsNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ToDoListsNavGraph(navController, modifier = Modifier)
        }
    }
}