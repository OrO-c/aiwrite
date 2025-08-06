package com.aiwriter.assistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.aiwriter.assistant.R
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.ui.floating.FloatingActivity
import com.aiwriter.assistant.utils.PermissionHelper

class FloatingWindowService : Service() {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "floating_service"
        
        fun start(context: Context) {
            val intent = Intent(context, FloatingWindowService::class.java).apply {
                action = "SHOW_FLOATING_BUTTON"
            }
            context.startForegroundService(intent)
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, FloatingWindowService::class.java).apply {
                action = "STOP_SERVICE"
            }
            context.startService(intent)
        }
    }
    
    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private val preferences by lazy { AIWriterApplication.instance.preferences }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "SHOW_FLOATING_BUTTON" -> showFloatingButton()
            "HIDE_FLOATING_BUTTON" -> hideFloatingButton()
            "STOP_SERVICE" -> stopSelf()
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        hideFloatingButton()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "悬浮窗服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "AI写作助手悬浮窗服务"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val stopIntent = Intent(this, FloatingWindowService::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI 写作助手")
            .setContentText("悬浮球已启用")
            .setSmallIcon(R.drawable.ic_edit)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_close, "关闭", stopPendingIntent)
            .build()
    }
    
    private fun showFloatingButton() {
        if (!PermissionHelper.hasOverlayPermission(this) || 
            preferences.workMode != WorkMode.FLOATING_INPUT) {
            return
        }
        
        if (floatingView != null) return
        
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // Create floating button view
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button, null)
        
        val params = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }
        
        // Set click listener
        floatingView?.setOnClickListener {
            launchWritingInterface()
            
            // Add vibration feedback if enabled
            if (preferences.isVibrationEnabled) {
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        android.os.VibrationEffect.createOneShot(50, android.os.VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(50)
                }
            }
        }
        
        // Add drag functionality
        setupDragListener(floatingView!!, params)
        
        try {
            windowManager?.addView(floatingView, params)
        } catch (e: Exception) {
            // Handle permission or other errors
        }
    }
    
    private fun hideFloatingButton() {
        floatingView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (e: Exception) {
                // View was already removed or never added
            }
        }
        floatingView = null
        windowManager = null
    }
    
    private fun setupDragListener(view: View, params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(view, params)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun launchWritingInterface() {
        val intent = Intent(this, FloatingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("mode", "floating")
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }
}