package org.jetbrains.plugins.template

import com.android.ddmlib.Log
import com.intellij.openapi.options.Configurable
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class AppSettingsConfigurable: Configurable {

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
        // todo will load from data source when ready
        val root = DefaultMutableTreeNode("NFCU")
        val adoNode = DefaultMutableTreeNode("ADO")
        val riseNode = DefaultMutableTreeNode("Rise Modules")
        riseNode.add(DefaultMutableTreeNode("Environmental Setup sdkf dskf dskjf dskj dks jkds fksd"))
        riseNode.add(DefaultMutableTreeNode("Emulator Setup"))
        root.add(adoNode)
        root.add(riseNode)
        val tree = Tree(root)
        tree.isRootVisible = true
        val decoratedTree = ToolbarDecorator.createDecorator(tree)
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