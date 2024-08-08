package org.jetbrains.plugins.template

import com.intellij.ui.treeStructure.Tree
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

// Double click to enter edit menu
class MyMouseListener(val tree: Tree, val editAction: (tree: Tree) -> Boolean) : MouseListener {

    override fun mouseClicked(e: MouseEvent?) {
        val selRow: Int = tree.getRowForLocation(e!!.x, e.y)
        if (selRow != -1 && e.clickCount == 2) {
            editAction(tree)
        }
    }

    override fun mousePressed(p0: MouseEvent?) {}

    override fun mouseReleased(p0: MouseEvent?) {}

    override fun mouseEntered(p0: MouseEvent?) {}

    override fun mouseExited(p0: MouseEvent?) {}

}
