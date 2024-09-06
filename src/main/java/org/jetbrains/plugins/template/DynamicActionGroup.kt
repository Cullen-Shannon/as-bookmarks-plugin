package org.jetbrains.plugins.template

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.domain.MyMenuItem
import org.jetbrains.plugins.template.services.FileInputService
import javax.swing.tree.DefaultMutableTreeNode

/* TODOs
    replace pluginIcon.svg with something more meaningful
    update plugin.xml with our publishing info
*/

/*
    For V2:
        clean up any non-critical TODOs
        support other functions besides links to browser (shell, gradle tasks, information bubbles, etc.)
        rewrite any java classes in Kotlin
 */


/**
 * Represents a group of menu items. menuItem will be null for the top level menu defined in plugin.xml.
 * Otherwise we can pass in the child object recursively to allow for infinite nesting based on user configuration.
 */
class DynamicActionGroup(var node: DefaultMutableTreeNode? = null) : ActionGroup() {

    override fun update(e: AnActionEvent) {

        val project = ProjectManager.getInstance().openProjects.first()
        val fileInputService = FileInputService.getInstance(project)

        val latestMenuContents = fileInputService.readConfigFileContents()

        // Ensure that we check if the menu has been altered since it was first read in (but only for top-level node)
        if (node?.parent == null && node != latestMenuContents) {
            // Read in top level menu item
            node = latestMenuContents

            // add custom settings action
            node!!.add(DefaultMutableTreeNode(MyMenuItem(label = SETTINGS_KEY)))
        }

        val tree = Tree(node)
        tree.isRootVisible = true
        e.presentation.isPopupGroup = true
        val casted = node!!.userObject as MyMenuItem

        e.presentation.isPopupGroup = true
        e.presentation.text = casted.label
        e.presentation.description = casted.label + " description"
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        if (node?.childCount == 0) return arrayOf()

        val array = mutableListOf<AnAction>()
        node!!.children().asIterator().forEach {
            val casted = (it as DefaultMutableTreeNode).userObject as MyMenuItem
            if (casted.isDivider) array.add(Separator.getInstance())
            if (it.childCount == 0) {
                if (casted.label == SETTINGS_KEY) {
                    array.add(Separator.getInstance())
                    array.add(OpenSettingsAction())
                } else {
                    array.add(MyAction(casted))
                }
            } else {
                array.add(DynamicActionGroup(it))
            }
        }
        return array.toTypedArray()
    }

    private companion object {
        const val SETTINGS_KEY = "<settings_SDKFNDSK#%$(*#$%FNDS>"
    }

}

