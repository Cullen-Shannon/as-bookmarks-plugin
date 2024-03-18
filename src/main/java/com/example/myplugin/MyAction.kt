package com.example.myplugin

import com.intellij.ide.browsers.BrowserFamily
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.browsers.WebBrowserManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Represents a single actionable menu item. This will only be invoked once we've reached the bottom of the recusrive
 * loop and found items with no children. Currently we only support kicking out to a browser, but could easily add
 * other basic operations, like shell scripts, gradle tasks, api calls, etc.
 */
class MyAction(private val menuItem: MyMenuItem): AnAction() {
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        e.presentation.text = menuItem.text
        e.presentation.description = menuItem.description
    }

    override fun actionPerformed(e: AnActionEvent) {
        // TODO use default browser instead of chrome by rule
        val chromeBrowsers = WebBrowserManager.getInstance().browsers.filter { it.family == BrowserFamily.CHROME }
        val browser = if (chromeBrowsers.isEmpty()) null else chromeBrowsers.first()
        BrowserLauncher.instance.browse(menuItem.url!!, browser)
//        TestDialog.main(arrayOf())
    }

}