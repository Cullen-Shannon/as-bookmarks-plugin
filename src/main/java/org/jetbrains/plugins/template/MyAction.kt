package org.jetbrains.plugins.template

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.browsers.WebBrowserManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.plugins.template.domain.MyMenuItem

/**
 * Represents a single actionable menu item. This will only be invoked once we've reached the bottom of the recursive
 * loop and found items with no children. Currently we only support kicking out to a browser, but could easily add
 * other basic operations, like shell scripts, gradle tasks, api calls, help modals, etc.
 */
class MyAction(private val menuItem: MyMenuItem): AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        e.presentation.text = menuItem.label
        e.presentation.description = menuItem.label + " description"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val browsers = WebBrowserManager.getInstance().browsers
        if (browsers.isEmpty() || menuItem.url.isEmpty()) return
        BrowserLauncher.instance.browse(menuItem.url, browsers.first())
    }

    // Resolves 'org.jetbrains.plugins.template.DynamicActionGroup' must override `getActionUpdateThread()` and chose EDT or BGT error
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

}