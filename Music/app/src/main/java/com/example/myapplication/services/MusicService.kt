package com.example.myapplication.services

import android.app.Service
import android.media.MediaPlayer
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.docthoai).apply {
            isLooping = true
            setOnPreparedListener { start() }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startMusic()
            "STOP" -> stopMusic()
        }
        return START_STICKY
    }

    private fun startMusic() {
        mediaPlayer?.start()
        startForeground(1, createNotification())
    }

    private fun stopMusic() {
        mediaPlayer?.pause()
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val channelId = "music_channel"
        val channelName = "Music Service"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Đang phát nhạc")
            .setContentText("Ứng dụng phát nhạc nền")
            .setSmallIcon(R.drawable.ic_launcher_background) // cần icon trong drawable
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}