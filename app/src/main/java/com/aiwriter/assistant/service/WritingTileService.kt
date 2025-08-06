package com.aiwriter.assistant.service

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.ui.floating.FloatingActivity

class WritingTileService : TileService() {
    
    private val preferences by lazy { AIWriterApplication.instance.preferences }
    
    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }
    
    override fun onClick() {
        super.onClick()
        
        // Only work if setup is completed and in tile mode
        if (!preferences.isSetupCompleted || preferences.workMode != WorkMode.TILE_CLIPBOARD) {
            return
        }
        
        // Launch writing interface
        launchWritingInterface()
    }
    
    private fun updateTileState() {
        val tile = qsTile ?: return
        
        if (preferences.isSetupCompleted && preferences.workMode == WorkMode.TILE_CLIPBOARD) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = "AI 写作"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                tile.subtitle = "点击生成文本"
            }
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "AI 写作"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                tile.subtitle = "未配置"
            }
        }
        
        tile.updateTile()
    }
    
    private fun launchWritingInterface() {
        // Create intent to launch floating activity for input
        val intent = Intent(this, FloatingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("mode", "tile")
        }
        
        try {
            // Start activity normally - this is simpler and avoids lint issues
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error - maybe show a toast or notification
        }
    }
}