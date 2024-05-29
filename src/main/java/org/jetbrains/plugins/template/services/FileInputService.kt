package org.jetbrains.plugins.template.services

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.plugins.template.MyMenuItem
import java.io.IOException

class FileInputService {

    val project: Project = ProjectManager.getInstance().openProjects.first()

    fun readConfigFileContents(): List<MyMenuItem>? {
        return try {
            val config = FilenameIndex.getVirtualFilesByName("config.json", GlobalSearchScope.allScope(project))
            if (config.size != 1) {
                throw Exception("Unexpected number of config files")
            }
            val menuItemList = mutableListOf<MyMenuItem>()

            var nextItemText = config.firstOrNull()

            while (nextItemText != null) {
                val menuItem = Gson().fromJson(nextItemText.toString(), MyMenuItem::class.java)
                menuItemList.add(menuItem)
                nextItemText = config.firstOrNull()
            }

            return menuItemList.toList()
        } catch (e: IOException) {
            null // TODO: Error Handling
        }
    }
}