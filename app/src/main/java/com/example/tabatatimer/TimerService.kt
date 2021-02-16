package com.example.tabatatimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TimerService : Service() {

    var NOTIFICATION_ID = 101
    var CHANNEL_ID = "TabataTimer"

    private var currentTimer: CountDownTimer? = null
    private var running = true
    private lateinit var notificationChannel : NotificationChannel
    private var sequenceHandler : SequenceHandler? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val com = intent?.getStringExtra("Command")
        if (sequenceHandler == null) {
            sequenceHandler = intent?.getSerializableExtra("Sequence") as SequenceHandler
        }

        when (com) {
            "Start" -> Start(sequenceHandler!!)
            "Pause" -> Pause()
            "BackPhase" -> BackPhase(sequenceHandler!!)
            "ForwardPhase" -> ForwardPhase(sequenceHandler!!)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun Start(sequenceHandler: SequenceHandler) {
        Log.i("APPTIMER", "START")
        running = true
        createNotification()
        sendNotification(sequenceHandler.getPhaseName())

        currentTimer = object : CountDownTimer(sequenceHandler.getCountdown(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                sequenceHandler.setCountDown(millisUntilFinished)
            }

            override fun onFinish() {
                if (running) {
                    closeNotification()
                    if (sequenceHandler.moveToNextPhase()) {
                        Start(sequenceHandler)
                    }
                    else {
                        stopSelf()
                    }
                }
            }
        }.start()
    }

    private fun sendNotification(message: String) {
        val nm :NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nc = nm.getNotificationChannel(CHANNEL_ID)
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.settings)
            .setContentTitle("Timer")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri)
        val notification = builder.build()
        nm.notify(NOTIFICATION_ID, notification)
        //startForeground(1, notification)
    }

    private fun closeNotification() {
        val nm :NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nc = nm.getNotificationChannel(CHANNEL_ID)

        //stopForeground(true)
        nm.cancelAll()
        nm.deleteNotificationChannel(CHANNEL_ID)
    }

    private fun createNotification() {
        val nm : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nc = NotificationChannel(CHANNEL_ID, "cat_feeder", NotificationManager.IMPORTANCE_DEFAULT)
        nc.enableLights(true)
        nc.enableVibration(true)
        nc.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        nc.setShowBadge(false)
        nm.createNotificationChannel(nc)
    }

    fun Pause() {
        Log.i("APPTIMER", "PAUSE")
        running = false
        currentTimer?.cancel()

        closeNotification()
    }

    fun BackPhase(sequenceHandler: SequenceHandler) {
        Pause()
        if (sequenceHandler.moveToPrevPhase()) {
            Start(sequenceHandler)
        }
    }

    fun ForwardPhase(sequenceHandler: SequenceHandler) {
        Pause()
        if (sequenceHandler.moveToNextPhase()) {
            Start(sequenceHandler)
        }
    }

    override fun onDestroy() {
        closeNotification()
        stopForeground(true)
        Pause()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}