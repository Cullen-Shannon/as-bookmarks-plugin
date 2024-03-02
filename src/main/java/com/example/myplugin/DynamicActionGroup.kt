package com.example.myplugin

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class DynamicActionGroup: ActionGroup() {

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
        e.presentation.text = "Pop Dialog Action1"
        e.presentation.description="SDK action example1"
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            PopupDialogAction()
        )
    }

}

/*
topMenuName: NFCU
topMenuDesc:

 */

