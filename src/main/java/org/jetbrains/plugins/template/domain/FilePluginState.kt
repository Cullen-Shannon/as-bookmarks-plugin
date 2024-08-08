package org.jetbrains.plugins.template.domain

/*
   Stores the state for the currently installed plugin

   `currentMenuItemConfig`: `MyMenuItem` corresponding to the existing menu item data that was previously read in from the user's config file
   GRAHAM TODO: this should probably represent DefaultMutableNode to support the new parser..I think..?
 */
data class FilePluginState(
        var currentMenuItemConfig: MyMenuItem? = null
)