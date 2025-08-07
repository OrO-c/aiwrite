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
        
        fun insertText(text: String): Boolean {
            val service = getInstance()
            return if (service != null) {
                service.insertTextInternal(text)
            } else {
                false
            }
        }
        
        private fun AccessibilityService.insertTextInternal(text: String): Boolean {
            return try {
                // Method 1: Try to find focused text field and insert text
                val focusedNode = this.findFocusedEditableNode(rootInActiveWindow)
                if (focusedNode != null) {
                    this.insertTextToNode(focusedNode, text)
                    return true
                }
                
                // Method 2: Use clipboard and paste
                copyToClipboard(this, text)
                // Note: GLOBAL_ACTION_PASTE is not available in all Android versions
                // Using clipboard method instead
                true
            } catch (e: Exception) {
                false
            }
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
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle events for this use case
        // This service is primarily used for text insertion
    }
    
    override fun onInterrupt() {
        // Handle service interruption
    }
    

    
    private fun findFocusedEditableNode(root: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        root ?: return null
        
        // Check if current node is focused and editable
        if (root.isFocused && root.isEditable) {
            return root
        }
        
        // Search in children
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
    
    private fun insertTextToNode(node: AccessibilityNodeInfo, text: String): Boolean {
        return try {
            // Method 1: Set text directly
            val arguments = bundleOf(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE to text
            )
            val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            
            if (!success) {
                // Method 2: Use clipboard and paste action
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