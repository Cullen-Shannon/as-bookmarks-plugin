package org.jetbrains.plugins.template.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.plugins.template.DefaultMutableTreeNodeTypeAdapter
import org.jetbrains.plugins.template.domain.MyMenuItem
import javax.swing.tree.DefaultMutableTreeNode


@Service(Service.Level.PROJECT)
class FileInputService {

    private val project = ProjectManager.getInstance().openProjects.first()
    private val pluginSettingsService = PluginSettingsService.getInstance(project)

    val gson = GsonBuilder()
        .registerTypeAdapterFactory(DefaultMutableTreeNodeTypeAdapter.FACTORY)
        .setPrettyPrinting()
        .create()

    fun readConfigFileContents(): DefaultMutableTreeNode {
        val configFile = getCurrentConfigFile()
        val text = LoadTextUtil.loadText(configFile)
        return gson.fromJson(text.toString(), DefaultMutableTreeNode::class.java)
    }

    fun writeConfigFileContents(newMenu: MyMenuItem, newFileName: String?) {
        WriteCommandAction.runWriteCommandAction(project) {
            val updatedJSON = Gson().toJson(newMenu, DefaultMutableTreeNode::class.java)
            val configFile = getCurrentConfigFile()
            configFile.isWritable = true
            VfsUtil.saveText(configFile, updatedJSON)
        }
    }

    private fun getCurrentConfigFile(): VirtualFile {
        val fileName = "repo_depot.json"
        val file = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.allScope(project)).firstOrNull()
        return file!!

        // GRAHAM TODO create new file if missing with default values
//        val root = DefaultMutableTreeNode(MyMenuItem(label = "Your Organization"))
//        root.add(DefaultMutableTreeNode(MyMenuItem("Example", "www.yourorg.com")))
//        gson.toJson(root)
    }

    companion object {
        fun getInstance(project: Project): FileInputService {
            return project.getService(FileInputService::class.java)
        }
    }
}