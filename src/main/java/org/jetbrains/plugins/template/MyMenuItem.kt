package org.jetbrains.plugins.template

data class MyMenuItem(
    var text: String,
    var description: String,
    var url: String? = null,
    var children: List<MyMenuItem>? = null,
    var addSeparatorBefore: Boolean = false,
    var addSeparatorAfter: Boolean = false,
    var isTopLevel: Boolean = false
)