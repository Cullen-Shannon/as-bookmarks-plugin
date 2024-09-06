package org.jetbrains.plugins.template

import com.google.gson.GsonBuilder
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.domain.MyMenuItem
import org.jetbrains.plugins.template.services.FileInputService
import java.awt.Dimension
import javax.swing.DropMode
import javax.swing.JPanel
import javax.swing.tree.*

class AppSettingsConfigurable : Configurable {

    private val project = ProjectManager.getInstance().openProjects.first()
    private val fileInputService = FileInputService.getInstance(project)

    // The tree representing the current `MyMenuItem` config, update this when adding/removing elements
    private lateinit var tree: Tree

    // `isModified` controls enabling/disabling the "Apply" button
    private var isModified = false

    override fun createComponent() : JPanel {
        val model = fileInputService.readConfigFileContents()
        tree = Tree(model.root)
        with (tree) {
            isRootVisible = true
            dragEnabled = true
            dropMode = DropMode.ON_OR_INSERT
            transferHandler = TreeTransferHandler()
            selectionModel.selectionMode = TreeSelectionModel.CONTIGUOUS_TREE_SELECTION
            addMouseListener(MyMouseListener(this) { myEditAction(this) })
        }

        return ToolbarDecorator.createDecorator(tree)
            .setAddAction { myAddAction() }
            .setRemoveAction { myRemoveAction() }
            .setEditAction { myEditAction(tree) }
            .setPreferredSize(Dimension(600, 400)) // TODO -- can't figure out a way around needing this
            .createPanel()
    }

    private fun myAddAction() {
        val changed = MyDialog(MyMenuItem("", "")) {
            val selection = tree.lastSelectedPathComponent as DefaultMutableTreeNode?
            val _model = tree.model as DefaultTreeModel
            if (selection == null) {
                // if nothing selected, add to top level
                val root = _model.root as DefaultMutableTreeNode
                root.add(DefaultMutableTreeNode(it))
                _model.reload()
            } else {
                // otherwise add within selection
                if (selection.childCount != 0) {
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

    private fun myRemoveAction() {
        val selection = tree.lastSelectedPathComponent as DefaultMutableTreeNode
        val _model = tree.model as DefaultTreeModel
        _model.removeNodeFromParent(selection)
        isModified = true
    }

    fun myEditAction(tree: Tree): Boolean {
        val selection = tree.lastSelectedPathComponent as DefaultMutableTreeNode
        val myMenuItem = if (selection.userObject is MyMenuItem) {
            selection.userObject as MyMenuItem
        } else if (selection.userObject is DefaultMutableTreeNode &&
            (selection.userObject as DefaultMutableTreeNode).userObject is MyMenuItem) {
            // a bug in drag-and-drop accidentally nests the user object down a level, so grabbing it from there
            // TODO investigate in drag-and-drop file
            (selection.userObject as DefaultMutableTreeNode).userObject as MyMenuItem
        } else {
            // fallback, but should probably throw an error or something here
            MyMenuItem()
        }
        val changed = MyDialog(myMenuItem) {
            selection.userObject = it
            (this.tree.model as DefaultTreeModel).reload()
        }.showAndGet()
        if (changed) isModified = true
        return changed
    }

    override fun isModified() = isModified

    override fun apply() {
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(DefaultMutableTreeNodeTypeAdapter.FACTORY)
            .setPrettyPrinting()
            .create()
        val newJSON = gson.toJson(tree.model.root)

        if (newJSON != null) {
            fileInputService.writeConfigFileContents(newJSON)
        }

        // Disable the "Apply" button after the updates are made
        isModified = false

    }

    override fun getDisplayName() = "Repo Depot"

}