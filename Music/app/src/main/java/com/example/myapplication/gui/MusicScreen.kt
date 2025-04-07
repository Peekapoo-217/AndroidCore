package com.example.myapplication.gui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.services.MusicService
import com.example.myapplication.viewmodel.MusicViewModel

@Composable
fun MusicScreen(context: Context, viewModel: MusicViewModel, modifier: Modifier = Modifier) {
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            startMusicService(context)
            viewModel.setPlaying(true)
        }) {
            Text("Phát nhạc")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            stopMusicService(context)
            viewModel.setPlaying(false)
        }) {
            Text("Dừng nhạc")
        }
    }
}

fun startMusicService(context: Context) {
    val intent = Intent(context, MusicService::class.java).apply { action = "START" }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

fun stopMusicService(context: Context) {
    val intent = Intent(context, MusicService::class.java).apply { action = "STOP" }
    context.startService(intent)
}