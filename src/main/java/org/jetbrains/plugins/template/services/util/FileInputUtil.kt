package org.jetbrains.plugins.template.services.util

import org.jetbrains.plugins.template.MyMenuItem
import javax.swing.tree.DefaultMutableTreeNode

/*
   Utils for converting data from the config JSON file to UI components
*/
object FileInputUtil {

    /*
        Reads in the tree's children for the data within `AppSettingsConfigurable`
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
}