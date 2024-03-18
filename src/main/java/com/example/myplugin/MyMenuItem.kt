package com.example.myplugin

data class MyMenuItem(
    val text: String,
    val description: String,
    val url: String?,
    val children: List<MyMenuItem>?,
    val addSeparatorBefore: Boolean?,
    val addSeparatorAfter: Boolean?
)