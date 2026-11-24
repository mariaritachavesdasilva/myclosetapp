package com.example.myclosetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myclosetapp.ui.navigation.NavGraph
import com.example.myclosetapp.ui.theme.MyClosetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MyClosetApplication

        setContent {
            MyClosetTheme {
                NavGraph(container = app.container)
            }
        }
    }
}
