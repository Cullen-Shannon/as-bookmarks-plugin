package org.jetbrains.plugins.template.services

import com.google.gson.Gson
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.plugins.template.MyMenuItem
import java.io.IOException

class FileInputService {

    val project: Project = ProjectManager.getInstance().openProjects.first()

    fun readConfigFileContents(): MyMenuItem? {
        return try {
            val config = FilenameIndex.getVirtualFilesByName("config.json", GlobalSearchScope.allScope(project))
            if (config.size != 1) {
                throw Exception("Unexpected number of config files")
            }
            val nextItemText = config.firstOrNull()
            var menuItem: MyMenuItem? = null

            if (nextItemText != null) {
                val text = LoadTextUtil.loadText(nextItemText)
                menuItem = Gson().fromJson(text.toString(), MyMenuItem::class.java)
            }

            return menuItem
        } catch (e: IOException) {
            null // TODO: Error Handling
        }
    }

}