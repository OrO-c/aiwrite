package com.aiwriter.assistant.service

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.os.bundleOf

class AccessibilityService : AccessibilityService() {
    companion object {
        private var instance: AccessibilityService? = null

        fun getInstance(): AccessibilityService? = instance

        fun isServiceEnabled(): Boolean = instance != null

        /**
         * 插入文本到当前焦点输入框
         * @return 是否插入成功
         */
        fun insertText(text: String): Boolean {
            val service = getInstance()
            return if (service != null) {
                // 直接在companion object中处理文本插入逻辑
                try {
                    val focusedNode = findFocusedEditableNode(service.rootInActiveWindow)
                    if (focusedNode != null) {
                        insertTextToNode(service, focusedNode, text)
                        return true
                    }
                    // 找不到输入框，使用剪贴板
                    copyToClipboard(service, text)
                    true
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }
        }

        /**
         * 复制文本到剪贴板
         */
        fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("AI Writing Assistant", text)
            clipboard.setPrimaryClip(clip)
        }

        /**
         * 递归查找当前窗口中获得焦点且可编辑的节点
         */
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

        /**
         * 向指定节点插入文本
         */
        private fun insertTextToNode(service: AccessibilityService, node: AccessibilityNodeInfo, text: String): Boolean {
            return try {
                val arguments = bundleOf(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE to text
                )
                val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                if (!success) {
                    // 兼容性处理：用剪贴板粘贴
                    copyToClipboard(service, text)
                    node.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                } else {
                    true
                }
            } catch (e: Exception) {
                false
            }
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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不处理事件，服务只用于文本插入
    }

    override fun onInterrupt() {
        // 服务中断处理
    }
}