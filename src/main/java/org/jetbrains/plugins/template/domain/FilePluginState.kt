package org.jetbrains.plugins.template.domain

/*
   Stores the state for the currently installed plugin

   `fileName`: the name of the file that can be configured by the user within `AppSettingsConfigurable`
   `currentMenuItemConfig`: `MyMenuItem` corresponding to the existing menu item data that was previously read in from the user's config file
 */
data class FilePluginState(
        var fileName: String = "config.json",
        var currentMenuItemConfig: MyMenuItem? = null
)