package org.jetbrains.plugins.template

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "org.jetbrains.plugins.template.AppSettingsState", storages = [Storage("SdkSettingsPlugin.xml")])
class AppSettingsState: PersistentStateComponent<AppSettingsState> {

    var userId = "John Q. Public"
    var ideaStatus = false

    override fun getState() = this

    override fun loadState(state: AppSettingsState) = XmlSerializerUtil.copyBean(state, this)

    companion object {
        fun getInstance(): AppSettingsState? {
            return ApplicationManager.getApplication().getService(AppSettingsState::class.java)
        }
    }

}