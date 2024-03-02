package com.example.myplugin

import com.intellij.ide.browsers.BrowserFamily
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ide.browsers.WebBrowserManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import java.io.File

/*
    dynamic actions
    Run gradle task
    implement linting rule
    icon support

    https://plugins.jetbrains.com/docs/intellij/creating-plugin-project.html
    https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#kotlin-gradle-plugin
    https://plugins.jetbrains.com/docs/intellij/android-studio.html#configuring-the-plugin-gradle-build-script
    https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html#2022
    https://plugins.jetbrains.com/docs/intellij/working-with-custom-actions.html#registering-a-custom-action
    https://plugins.jetbrains.com/docs/intellij/dynamic-plugins.html
    https://github.com/JetBrains/intellij-sdk-code-samples
    https://github.com/JetBrains/intellij-plugins

    JAR from build/libs folder is can be installed locally in AS for testing

 */

class PopupDialogAction : AnAction() {
    override fun update(event: AnActionEvent) {
        // Set the availability based on whether a project is open
        val project = event.project
        event.presentation.isEnabledAndVisible = project != null
        event.presentation.text = "Pop Dialog Action1"
        event.presentation.description="SDK action example1"
    }

    override fun actionPerformed(event: AnActionEvent) {
        // Using the event, create and show a dialog
//        val currentProject = event.project
//        var dlgMsg = event.presentation.text + " Selected!"
//        val title = event.presentation.description
//        // If an element is selected in the editor, add info about it.
//        val nav = event.getData(CommonDataKeys.NAVIGATABLE)
//        if (nav != null) {
//            dlgMsg += "\nSelected Element: $nav"
//        }
//        Messages.showMessageDialog(currentProject, dlgMsg, title, Messages.getInformationIcon())
//
//        launchUrlInBrowser("https://www.google.com");

//        println(File("cullen.json"))

        val test: List<String> = listOf()
        TestDialog.main(test.toTypedArray())
    }

    private fun launchUrlInBrowser(url: String) {
        val chromeBrowsers = WebBrowserManager.getInstance().browsers.filter { it.family == BrowserFamily.CHROME }
        val browser = if (chromeBrowsers.isEmpty()) null else chromeBrowsers.first()
        BrowserLauncher.instance.browse(url, browser)
    }
}