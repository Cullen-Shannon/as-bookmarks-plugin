package org.jetbrains.plugins.template.domain

data class MyMenuItem(
    var label: String = "",
    var url: String = "",
    var isDivider: Boolean = false
) {
    override fun toString() = if (isDivider) "---" else label
}