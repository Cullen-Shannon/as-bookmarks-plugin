package org.jetbrains.plugins.template.services

import com.google.gson.Gson
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.plugins.template.domain.MyMenuItem
import java.io.IOException

@Service(Service.Level.PROJECT)
class FileInputService {

    private val project = ProjectManager.getInstance().openProjects.first()
    private val pluginSettingsService = PluginSettingsService.getInstance(project)

    fun readConfigFileContents(): MyMenuItem? {
        return try {
            val configFile = getCurrentConfigFile()
            var menuItem: MyMenuItem? = null

            if (configFile != null) {
                val text = LoadTextUtil.loadText(configFile)
                menuItem = Gson().fromJson(text.toString(), MyMenuItem::class.java)
                menuItem.isTopLevel = true
            }

            return menuItem
        } catch (e: IOException) {
            null // TODO: Error Handling
        }
    }

    fun writeConfigFileContents(newMenu: MyMenuItem, newFileName: String?) {
        WriteCommandAction.runWriteCommandAction(project) {
            try {
                val updatedJSON = Gson().toJson(newMenu, MyMenuItem::class.java)
                val configFile = getCurrentConfigFile()

                if (newFileName?.isNotEmpty() == true) {
                    // Update the plugin's model to remember this new file name
                    pluginSettingsService.loadState(state = pluginSettingsService.state.copy(fileName = newFileName))
                    // Update the file's name with the new value that the user entered, or the existing name
                    // TODO: Add input validation and verify that this works for changing the file's path (not just renaming)
                    configFile!!.rename(this, newFileName)
                }
                // Ensure that the file is writable
                configFile!!.isWritable = true

                // Write the new content to the file
                // TODO: Check if there is an easy way to have the updated JSON be formatted instead of all on one line
                VfsUtil.saveText(configFile, updatedJSON)
            } catch (e: IOException) {
                // TODO: Error Handling
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentConfigFile(): VirtualFile? {
        val fileName = pluginSettingsService.state.fileName
        return FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.allScope(project)).firstOrNull()
    }

    companion object {
        fun getInstance(project: Project): FileInputService {
            return project.getService(FileInputService::class.java)
        }
    }
}