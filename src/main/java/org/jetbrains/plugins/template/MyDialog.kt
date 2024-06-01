package org.jetbrains.plugins.template

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not
import org.jetbrains.plugins.template.domain.MyMenuItem
import javax.swing.JComponent

class MyDialog(private val myMenuItem: MyMenuItem): DialogWrapper(true) {

    lateinit var checkBox: Cell<JBCheckBox>

    init {
        title = "Maintain Entry"
        init()
    }

    override fun createCenterPanel(): JComponent? {

        return panel {
            row("Title: ") {
                textField().bindText(myMenuItem::text)
            }
            row("Description: ") {
                textField().bindText(myMenuItem::description)
            }
            row {
                checkBox = checkBox("Group?")
            }
            row("Url: ") {
                textField()
            }.enabledIf(checkBox.selected.not())
        }
    }

    // todo handle okay listener

}