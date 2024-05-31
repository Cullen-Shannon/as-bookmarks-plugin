package org.jetbrains.plugins.template

import com.android.ddmlib.Log
import com.intellij.openapi.options.Configurable
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.services.FileInputService
import org.jetbrains.plugins.template.services.util.FileInputUtil
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class AppSettingsConfigurable : Configurable {

    val fileInputService = FileInputService()
    var currentTree: Tree? = null

    // dummy item for dev purposes
    val dummyMenuItem = MyMenuItem(
            text = "text",
            description = "description"
    )

    // should consider moving this to its own class
    override fun createComponent(): JComponent? {
        return panel {
            row("Path: ") {
                textField()
            }
            row {
                cell(myTreeDecorated())
            }
        }
    }

    private fun myTreeDecorated(): JPanel {
        val menuItem = fileInputService.readConfigFileContents()
        var rootNode: DefaultMutableTreeNode? = null

        // TODO: Error Handling
        if (menuItem != null) {
            rootNode = DefaultMutableTreeNode(menuItem.text)
            if (menuItem.children != null) {
                FileInputUtil.readInTreeChildren(rootNode = rootNode, children = menuItem.children!!)
            }
        }

        currentTree = Tree(rootNode)
        currentTree!!.isRootVisible = true
        val decoratedTree = ToolbarDecorator.createDecorator(currentTree!!)
                .setAddAction {
                    if (MyDialog(dummyMenuItem).showAndGet()) {
                        Log.e("TAGX", "Got it!")
                    }
                }
                .setRemoveAction { Log.e("TAGX", "TODO remove") }
                .setEditAction { Log.e("TAGX", "TODO edit") }
                .setPreferredSize(Dimension(400, 300)) // TODO -- can't figure out a way around needing this
                .createPanel()
        return decoratedTree
    }

    // TODO everything below
    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
        // update the settings
    }

    override fun getDisplayName(): String {
        return "NFCU Test Component"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return super.getPreferredFocusedComponent()
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
    }
}