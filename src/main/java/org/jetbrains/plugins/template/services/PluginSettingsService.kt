package org.jetbrains.plugins.template.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.template.domain.FilePluginState

/*

 */
@State(name = "FilePluginState", storages = [Storage("FilePluginSettings.xml")])
@Service(Service.Level.PROJECT)
class PluginSettingsService : PersistentStateComponent<FilePluginState> {
    private var state = FilePluginState()

    override fun getState(): FilePluginState {
        return state
    }

    override fun loadState(state: FilePluginState) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project): PluginSettingsService {
            return project.getService(PluginSettingsService::class.java)
        }
    }
}