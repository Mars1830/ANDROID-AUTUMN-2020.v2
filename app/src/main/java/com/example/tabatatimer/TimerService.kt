package com.example.tabatatimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class TimerService : Service() {

    private var currentTimer: CountDownTimer? = null
    private var running = true
    private val channelId = "TabataTimer"
    private lateinit var notificationChannel : NotificationChannel

    override fun onCreate() {
        super.onCreate()

        notificationChannel = NotificationChannel(
            channelId,
            "Tabata timer",
            NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val com = intent?.getStringExtra("Command")
        val sequenceHandler = intent?.getSerializableExtra("Sequence") as SequenceHandler

        when (com) {
            "Start" -> Start(sequenceHandler)
            "Pause" -> Pause(sequenceHandler)
            "BackPhase" -> BackPhase(sequenceHandler)
            "ForwardPhase" -> ForwardPhase(sequenceHandler)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun Start(sequenceHandler: SequenceHandler) {
        running = true
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(sequenceHandler.getPhaseName())
            .setSmallIcon(R.drawable.settings)
            .build()
        startForeground(1, notification)

        NotificationManagerCompat.from(this).notify(1, notification)

        currentTimer = object : CountDownTimer(sequenceHandler.getCountdown(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                sequenceHandler.setCountDown(millisUntilFinished)
            }

            override fun onFinish() {
                if (running) {
                    if (sequenceHandler.moveToNextPhase()) {
                        Start(sequenceHandler)
                    }
                }
            }
        }.start()
    }

    fun Pause(sequenceHandler: SequenceHandler) {
        running = false
        currentTimer?.cancel()

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //NotificationManagerCompat.from(this).notify(1, )
        NotificationManagerCompat.from(this).cancelAll()
        notificationManager.cancelAll()
        notificationManager.deleteNotificationChannel(channelId)
        stopForeground(true)
    }

    fun BackPhase(sequenceHandler: SequenceHandler) {
        Pause(sequenceHandler)
        sequenceHandler.moveToPrevPhase()
    }

    fun ForwardPhase(sequenceHandler: SequenceHandler) {
        Pause(sequenceHandler)
        sequenceHandler.moveToNextPhase()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}