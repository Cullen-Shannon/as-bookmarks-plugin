package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.domain.MyMenuItem
import org.jetbrains.plugins.template.services.FileInputService
import org.jetbrains.plugins.template.services.PluginSettingsService
import javax.swing.tree.DefaultMutableTreeNode

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
class DynamicActionGroup(var menuItem: MyMenuItem? = null ) : ActionGroup() {

    override fun update(e: AnActionEvent) {

        val project = ProjectManager.getInstance().openProjects.first()
        val fileInputService = FileInputService.getInstance(project)
        val pluginSettingsService = PluginSettingsService.getInstance(project)

        // TODO: Error Handling
        if (menuItem == null) {
            // Read in top level menu item
            menuItem = fileInputService.readConfigFileContents()

            if (menuItem != null) {
                // Update the current model with the menu that was just read in
                pluginSettingsService.state.currentMenuItemConfig = menuItem
            }
        }

        val rootNode = DefaultMutableTreeNode(menuItem?.text)

        val tree = Tree(rootNode)

        tree.isRootVisible = true
        e.presentation.isPopupGroup = true
        e.presentation.text = menuItem?.text
        e.presentation.description = menuItem?.description
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (menuItem!!.children.isNullOrEmpty()) return arrayOf()

        val array = mutableListOf<AnAction>()
        menuItem!!.children!!.forEach {
            if (it.addSeparatorBefore) array.add(Separator.getInstance())
            if (it.children.isNullOrEmpty()) {
                array.add(MyAction(it))
            } else {
                array.add(DynamicActionGroup(it))
            }
            if (it.addSeparatorAfter) array.add(Separator.getInstance())
        }
        // Add a custom edit menu to launch our UI
        if (menuItem!!.isTopLevel) {
            array.add(Separator.getInstance())
            array.add(OpenSettingsAction())
        }
        return array.toTypedArray()
    }

}

