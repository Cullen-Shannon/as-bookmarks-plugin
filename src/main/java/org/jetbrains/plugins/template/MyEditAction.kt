package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Custom item to launch our UI
 */
class MyEditAction: AnAction() {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        e.presentation.text = "Edit Items..."
        e.presentation.description = "Edit the items in your list."
    }

    override fun actionPerformed(e: AnActionEvent) {
        MyDialog.main(arrayOf())
    }

}