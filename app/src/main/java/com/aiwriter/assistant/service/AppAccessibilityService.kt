package com.aiwriter.assistant.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf

class AppAccessibilityService : android.accessibilityservice.AccessibilityService() {
    companion object {
        private var instance: AppAccessibilityService? = null

        fun getInstance(): AppAccessibilityService? = instance

        fun isServiceEnabled(): Boolean = instance != null

        fun insertText(text: String): Boolean {
            val service = getInstance()
            return service?.insertTextInternal(text) ?: false
        }

        fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Writing Assistant", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // 不处理事件，服务只用于文本插入
    }

    override fun onInterrupt() {
        // 服务中断处理
    }

    fun insertTextInternal(text: String): Boolean {
        return try {
            val root = rootInActiveWindow ?: return false
            try {
                val focusedNode = findFocusedEditableNode(root)
                if (focusedNode != null) {
                    val success = insertTextToNode(focusedNode, text)
                    focusedNode.recycle()
                    return success
                }
            } finally {
                root.recycle()
            }
            copyToClipboard(this, text)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun findFocusedEditableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (root.isFocused && root.isEditable) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            try {
                val found = findFocusedEditableNode(child)
                if (found != null) return found
            } finally {
                child.recycle()
            }
        }
        return null
    }

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