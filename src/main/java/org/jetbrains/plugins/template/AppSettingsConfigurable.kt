package org.jetbrains.plugins.template

import com.android.ddmlib.Log
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.domain.MyMenuItem
import org.jetbrains.plugins.template.services.FileInputService
import org.jetbrains.plugins.template.services.PluginSettingsService
import org.jetbrains.plugins.template.services.util.FileInputUtil
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class AppSettingsConfigurable : Configurable {

    private val project = ProjectManager.getInstance().openProjects.first()
    private val pluginSettingsService = PluginSettingsService.getInstance(project)
    private val fileInputService = FileInputService.getInstance(project)

    // The tree representing the current `MyMenuItem` config, update this when adding/removing elements
    private var currentTree: Tree? = null

    // dummy item for dev purposes
    private val dummyMenuItem = MyMenuItem(
            text = "text",
            description = "description"
    )

    // Keep track of the "Path" text field so that we can read the value
    private var pathTextFieldCell: Cell<JBTextField>? = null

    // `isModified` controls enabling/disabling the "Apply" button
    private var isModified = false

    override fun createComponent(): JComponent {
        return panel {
            row("Path: ") {
                pathTextFieldCell = textField()

                // Prefill the existing file name by default
                pathTextFieldCell?.component?.text = pluginSettingsService.state.fileName

                // Add a document listener to enable the Apply button when the text changes
                pathTextFieldCell?.component?.document?.addDocumentListener(object : javax.swing.event.DocumentListener {
                    override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                        isModified = true
                    }

                    override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                        isModified = true
                    }

                    override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                        isModified = true
                    }
                })
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
            // Update the plugin's model with the menu that was just read in
            pluginSettingsService.state.currentMenuItemConfig = menuItem
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

    override fun isModified(): Boolean {
        return isModified
    }

    override fun apply() {
        // TODO: Error Handling

        // Try to read in the updated file name from the "Path" textInput, if not use the existing value from the plugin's state
        val updatedFileName = pathTextFieldCell?.component?.text

        val updatedMenuItem = FileInputUtil.convertTreeToMyMenuItem(
                currentTree,
                pluginSettingsService.state.currentMenuItemConfig ?: MyMenuItem(text = "", description = "")
        )

        if (updatedMenuItem != null) {
            // Update the file with the changes that the user made
            fileInputService.writeConfigFileContents(updatedMenuItem, newFileName = updatedFileName)
        }

        // Disable the "Apply" button after the updates are made
        isModified = false

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