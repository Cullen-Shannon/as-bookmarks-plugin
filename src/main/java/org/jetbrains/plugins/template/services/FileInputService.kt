package org.jetbrains.plugins.template.services

import com.google.gson.GsonBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.template.DefaultMutableTreeNodeTypeAdapter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.io.path.exists


@Service(Service.Level.PROJECT)
class FileInputService {

    private val project = ProjectManager.getInstance().openProjects.first()

    val gson = GsonBuilder()
        .registerTypeAdapterFactory(DefaultMutableTreeNodeTypeAdapter.FACTORY)
        .setPrettyPrinting()
        .create()

    fun readConfigFileContents(): DefaultMutableTreeNode {
        val configFile = getCurrentConfigFile()

        // Get the Document associated with this VirtualFile (for unsaved changes)
        val document: Document? =  if (configFile != null) {
            FileDocumentManager.getInstance().getDocument(configFile)
        } else {
            null
        }

        // Load the repo_depot.json file
        // If one doesn't exist the first time the user opens the "Tools" menu,
        // we show default config from a hardcoded JSON String, while creating a new corresponding default file in the background
        // After this point, a file will always exists (unless the user chooses to delete it).
        val text = document?.text ?: defaultJSONString
        return gson.fromJson(text, DefaultMutableTreeNode::class.java)
    }

    fun writeConfigFileContents(updatedJSON: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val configFile = getCurrentConfigFile()
            if (configFile != null) {
                configFile.isWritable = true
                VfsUtil.saveText(configFile, updatedJSON)
            }
        }
    }

    private fun getCurrentConfigFile(): VirtualFile? {
        val fileName = "repo_depot.json"
        val fileRef = AtomicReference<VirtualFile>()

        var existingFile: VirtualFile? = null

        // If file doesn't exist, create it
        val filePath = "${project.basePath}/$fileName"
        val path = Paths.get(filePath)

        ApplicationManager.getApplication().runReadAction {
            existingFile =
                VfsUtil.findFile(path, true)
        }

        if (existingFile != null) {
            fileRef.set(existingFile)
            return fileRef.get()
        }

        // Ensure parent directories exist
        if (!path.parent.exists()) {
            path.parent.toFile().mkdirs()
        }

        // Create the file if it doesn't exist
        if (!path.exists()) {
            path.toFile().createNewFile()
        }

        // Run a background task to create a default repo_depot.json if one does not already exist
        ApplicationManager.getApplication().executeOnPooledThread {
            val newFile = VfsUtil.findFile(path, true)

            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    try {
                        newFile?.setBinaryContent(
                            defaultJSONString.toByteArray(StandardCharsets.UTF_8)
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return null
    }

    companion object {
        fun getInstance(project: Project): FileInputService {
            return project.getService(FileInputService::class.java)
        }

        const val defaultJSONString = "{\n" +
                "  \"allowsChildren\": true,\n" +
                "  \"userObject\": {\n" +
                "    \"label\": \"Your Organization\",\n" +
                "    \"url\": \"\",\n" +
                "    \"isDivider\": false\n" +
                "  },\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"allowsChildren\": true,\n" +
                "      \"userObject\": {\n" +
                "        \"label\": \"Sample Bookmark\",\n" +
                "        \"url\": \"https://google.com\",\n" +
                "        \"isDivider\": false\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}"
    }
}