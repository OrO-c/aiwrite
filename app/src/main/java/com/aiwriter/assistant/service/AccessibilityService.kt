package com.aiwriter.assistant.service

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf

class AccessibilityService : AccessibilityService() {

    // 1. Binder 实现，外部通过 bindService 拿到 binder 调用 insertText
    inner class LocalBinder : Binder() {
        fun insertText(text: String): Boolean = insertTextInternal(text)
        fun isServiceEnabled(): Boolean = instance != null
    }

    private val binder = LocalBinder()

    companion object {
        // 只做实例管理
        @Volatile
        private var instance: AccessibilityService? = null

        fun getInstance(): AccessibilityService? = instance

        fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Writing Assistant", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不处理事件，服务只用于文本插入
    }

    override fun onInterrupt() {
        // 服务中断处理
    }

    // 2. 业务逻辑只在 Service 内部
    private fun insertTextInternal(text: String): Boolean {
        return try {
            val focusedNode = findFocusedEditableNode(rootInActiveWindow)
            if (focusedNode != null) {
                insertTextToNode(focusedNode, text)
                return true
            }
            copyToClipboard(this, text)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 递归查找当前窗口中获得焦点且可编辑的节点
    private fun findFocusedEditableNode(root: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        root ?: return null
        if (root.isFocused && root.isEditable) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i)
            child?.let {
                val found = findFocusedEditableNode(it)
                if (found != null) {
                    it.recycle()
                    return found
                }
                it.recycle()
            }
        }
        return null
    }

    // 向指定节点插入文本
    private fun insertTextToNode(node: AccessibilityNodeInfo, text: String): Boolean {
        return try {
            val arguments = bundleOf(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE to text
            )
            val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            if (!success) {
                copyToClipboard(this, text)
                node.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            } else {
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
