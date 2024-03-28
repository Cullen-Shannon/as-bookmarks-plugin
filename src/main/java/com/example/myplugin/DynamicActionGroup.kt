package com.example.myplugin

import com.google.gson.Gson
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

/* TODOs
    replace pluginIcon.svg with something more meaningful
    update plugin.xml with our publishing info
    use a different name than config.json, depending on whatever we name this
    add edit menu, default values, help modal
 */

/**
 * Represents a group of menu items. menuItem will be null for the top level menu defined in plugin.xml.
 * Otherwise we can pass in the child object recursively to allow for infinite nesting based on user configuration.
 */
class DynamicActionGroup(var menuItem: MyMenuItem? = null): ActionGroup() {

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
        if (project == null) return

        if (menuItem == null) {
            // todo error handling -- missing file, malformed json, etc.
            val config = FilenameIndex.getVirtualFilesByName("config.json", GlobalSearchScope.allScope(project))
            if (config.size != 1) throw Exception("Unexpected number of config files")
            val text = LoadTextUtil.loadText(config.first())
            menuItem = Gson().fromJson(text.toString(), MyMenuItem::class.java)
            menuItem!!.isTopLevel = true
        }
        e.presentation.isPopupGroup = true
        e.presentation.text = menuItem!!.text
        e.presentation.description = menuItem!!.description

    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (menuItem!!.children.isNullOrEmpty()) return arrayOf()

        val array = mutableListOf<AnAction>()
        menuItem!!.children!!.forEach {
            if (it.addSeparatorBefore == true) array.add(Separator.getInstance())
            if (it.children.isNullOrEmpty()) {
                array.add(MyAction(it))
            } else {
                array.add(DynamicActionGroup(it))
            }
            if (it.addSeparatorAfter == true) array.add(Separator.getInstance())
        }
        // Add a custom edit menu to launch our UI
        if (menuItem!!.isTopLevel) {
            array.add(Separator.getInstance())
            array.add(MyEditAction())
        }
        return array.toTypedArray()
    }

}

