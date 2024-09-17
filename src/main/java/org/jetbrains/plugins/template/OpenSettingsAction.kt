package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

/**
 * Custom item to open our menu in settings
 */
class OpenSettingsAction: AnAction() {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        e.presentation.text = "Edit Items..."
        e.presentation.description = "Edit the items in your list."
    }

    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, AppSettingsConfigurable::class.java)
    }

    // Resolves 'org.jetbrains.plugins.template.DynamicActionGroup' must override `getActionUpdateThread()` and chose EDT or BGT error
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}