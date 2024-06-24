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
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class AppSettingsConfigurable : Configurable {

    private val project = ProjectManager.getInstance().openProjects.first()
    private val pluginSettingsService = PluginSettingsService.getInstance(project)
    private val fileInputService = FileInputService.getInstance(project)

    // The tree representing the current `MyMenuItem` config, update this when adding/removing elements
    private var currentTree: Tree? = null

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

        // TODO: Error Handling
        // Update the plugin's model with the menu that was just read in
        val model = fileInputService.readConfigFileContents()!!
        pluginSettingsService.state.currentMenuItemConfig = model
        val rootNode = DefaultMutableTreeNode(model)
        FileInputUtil.readInTreeChildren(rootNode = rootNode, children = model.children!!)
        currentTree = Tree(rootNode)
        currentTree!!.isRootVisible = true
        currentTree!!.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        // this might work if we want to add a double click event; leaving for reference
//        currentTree!!.addMouseListener(object : MouseListener {
//            override fun mouseClicked(e: MouseEvent?) {
//                if (e?.clickCount == 2 && !e.isConsumed) {
//                    e.consume();
//                    println(e)
//                }
//            }
//            override fun mousePressed(p0: MouseEvent?) {}
//            override fun mouseReleased(p0: MouseEvent?) {}
//            override fun mouseEntered(p0: MouseEvent?) {}
//            override fun mouseExited(p0: MouseEvent?) {}
//        })

        val decoratedTree = ToolbarDecorator.createDecorator(currentTree!!)
            .setAddAction {
                val changed = MyDialog(MyMenuItem("", "")) {
                    val selection = currentTree!!.lastSelectedPathComponent as DefaultMutableTreeNode?
                    val _model = currentTree!!.model as DefaultTreeModel
                    if (selection == null) {
                        val root = _model.root as DefaultMutableTreeNode
                        root.add(DefaultMutableTreeNode(it))
                        _model.reload()
                    } else {
                        val selCasted = selection.userObject as MyMenuItem
                        val addToParent = selCasted.children == null
                        if (addToParent) {
                            (selection.parent as DefaultMutableTreeNode).add(DefaultMutableTreeNode(it))
                            _model.reload(selection.parent as DefaultMutableTreeNode)
                        } else {
                            selection.add(DefaultMutableTreeNode(it))
                            _model.reload(selection)
                        }
                    }
                }.showAndGet()
                if (changed) isModified = true
            }
            .setRemoveAction {
                val selection = currentTree!!.lastSelectedPathComponent as DefaultMutableTreeNode
                val _model = currentTree!!.model as DefaultTreeModel
                _model.removeNodeFromParent(selection)
                isModified = true
            }
            .setEditAction {
                val selection = currentTree!!.lastSelectedPathComponent as DefaultMutableTreeNode
                val changed = MyDialog(selection.userObject as MyMenuItem) {
                    selection.userObject = it
                    (currentTree!!.model as DefaultTreeModel).reload()
                }.showAndGet()
                if (changed) isModified = true
            }
            .setPreferredSize(Dimension(600, 400)) // TODO -- can't figure out a way around needing this
            .createPanel()
        return decoratedTree
    }

    override fun isModified() = isModified

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
        return pathTextFieldCell!!.component
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
    }
}