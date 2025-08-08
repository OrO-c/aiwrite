package com.aiwriter.assistant.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

object PermissionHelper {
    
    const val REQUEST_OVERLAY_PERMISSION = 1001
    const val REQUEST_ACCESSIBILITY_PERMISSION = 1002
    const val REQUEST_NOTIFICATION_PERMISSION = 1003
    
    /**
     * 检查悬浮窗权限
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    /**
     * 请求悬浮窗权限
     */
    fun requestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }
    
    /**
     * 检查无障碍服务是否启用
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
        
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val serviceName = "${context.packageName}/.service.AppAccessibilityService"
            return services?.contains(serviceName) == true
        }
        
        return false
    }
    
    /**
     * 请求无障碍权限
     */
    fun requestAccessibilityPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_ACCESSIBILITY_PERMISSION)
    }
    
    /**
     * 检查通知权限
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_GRANTED
        } else {
            true
        }
    }
    
    /**
     * 请求通知权限
     */
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }
    
    /**
     * 检查磁贴模式所需权限
     */
    fun hasTilePermissions(context: Context): Boolean {
        return hasNotificationPermission(context)
    }
    
    /**
     * 检查悬浮球模式所需权限
     */
    fun hasFloatingPermissions(context: Context): Boolean {
        return hasOverlayPermission(context) && isAccessibilityServiceEnabled(context)
    }
    
    /**
     * 获取缺失的权限列表
     */
    fun getMissingPermissions(context: Context, requireFloating: Boolean): List<String> {
        val missing = mutableListOf<String>()
        
        if (!hasNotificationPermission(context)) {
            missing.add("通知权限")
        }
        
        if (requireFloating) {
            if (!hasOverlayPermission(context)) {
                missing.add("悬浮窗权限")
            }
            if (!isAccessibilityServiceEnabled(context)) {
                missing.add("无障碍服务")
            }
        }
        
        return missing
    }
}