package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.gui.MusicScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    /*    override fun onCreate(savedInstanceState: Bundle?) {*/
    /*        super.onCreate(savedInstanceState)
            setContent {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MusicScreen(
                            context = LocalContext.current,
                            viewModel = remember {MusicViewModel()},
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicScreen(
                        context = LocalContext.current,
                        viewModel = MusicViewModel(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}