package org.jetbrains.plugins.template.services.util

import com.intellij.ui.treeStructure.Tree
import org.jetbrains.plugins.template.domain.MyMenuItem
import javax.swing.tree.DefaultMutableTreeNode

/*
   Utils for converting data from the config JSON file to UI components
*/
object FileInputUtil {

    /*
        Reads in the `MyMenuItem` children to form the tree for the data within `AppSettingsConfigurable`
     */
    fun readInTreeChildren(rootNode: DefaultMutableTreeNode, children: List<MyMenuItem>) {
        for (child in children) {
            val childNode = DefaultMutableTreeNode(child.text)
            rootNode.add(childNode)

            // Recursively read in the tree's children
            if (child.children != null) {
                readInTreeChildren(rootNode = childNode, children = child.children!!)
            }
        }
    }

    /*
       Converts the UI Tree from `AppSettingsConfigurable` to our domain class `MyMenuItem`
    */
    fun convertTreeToMyMenuItem(tree: Tree?, currentMenuItem: MyMenuItem): MyMenuItem? {
        val rootNode = tree?.model?.root as? DefaultMutableTreeNode

        return if (rootNode != null) {
            traverseMenuTree(parentTreeNode = rootNode, lookupMap = createFlatMenuItemLookupMap(currentMenuItem = currentMenuItem, lookupMap = mutableMapOf()))
        } else {
            null
        }
    }

    private fun traverseMenuTree(parentTreeNode: DefaultMutableTreeNode, lookupMap: MutableMap<String, MyMenuItem>): MyMenuItem {
        val updatedItemText = parentTreeNode.userObject as String

        // Try to find the existing entry so that we can preserve the user's existing data besides the new text
        // TODO: Handle updating description & other properties besides `text` from the UI
        val menuItem = lookupMap[updatedItemText] ?: MyMenuItem(text = updatedItemText, description = "")
        val children = mutableListOf<MyMenuItem>()

        // Traverse the `AppSettingsConfigurable` UI tree to add the child nodes
        for (i in 0 until parentTreeNode.childCount) {
            val childNode = parentTreeNode.getChildAt(i) as? DefaultMutableTreeNode
            if (childNode != null) {
                children.add(traverseMenuTree(childNode, lookupMap))
            }
        }

        menuItem.children = children

        return menuItem
    }

    // Convert the MyMenuItem to a map of Map[item.text] = MyMenuItem for fast lookup
    // This allows us to traverse the UI tree and the existing data in O(n + n) == O(n) time
    // TODO: Handle duplicate names
    private fun createFlatMenuItemLookupMap(currentMenuItem: MyMenuItem, lookupMap: MutableMap<String, MyMenuItem>): MutableMap<String, MyMenuItem> {
        var updatedMap = lookupMap

        updatedMap[currentMenuItem.text] = currentMenuItem

        if (currentMenuItem.children != null) {
            for (child in currentMenuItem.children!!) {
                updatedMap = createFlatMenuItemLookupMap(currentMenuItem = child, lookupMap = lookupMap)
            }
        }

        return updatedMap
    }
}