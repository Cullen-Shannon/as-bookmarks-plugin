package org.jetbrains.plugins.template

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not
import org.jetbrains.plugins.template.domain.MyMenuItem
import javax.swing.JComponent

/**
 * Maintenance dialog.
 */
class MyDialog(val myMenuItem: MyMenuItem, val onSubmit: (MyMenuItem) -> Unit): DialogWrapper(true) {

    private lateinit var _title: Cell<JBTextField>
    private lateinit var _url: Cell<JBTextField>
    private lateinit var _divider: Cell<JBCheckBox>

    private val panel = panel {
        val title = row("Title: ") {
            _title = textField().bindText(myMenuItem::label)
        }
        val url = row("Url: ") {
            _url = textField().bindText(myMenuItem::url)
        }
        row("Is Divider: ") {
            _divider = checkBox("Divider").bindSelected(myMenuItem::isDivider)
        }
        title.enabledIf(_divider.selected.not())
        url.enabledIf(_divider.selected.not())
    }

    override fun doValidate(): ValidationInfo? {
        if (_divider.component.isSelected) return null
        if (_title.component.text.isBlank()) return ValidationInfo("Title is required!")
        if (!_url.component.text.startsWith("http")) return ValidationInfo("URL must include prefix!")
        return null
    }

    init {
        title = "Maintain Entry"
        init()
    }

    override fun createCenterPanel() = panel

    override fun doOKAction() {
        panel.apply() // needed to set
        myMenuItem.label = if (!myMenuItem.isDivider) _title.component.text else ""
        myMenuItem.url = if (!myMenuItem.isDivider) _url.component.text else ""
        myMenuItem.isDivider = _divider.component.isSelected
        onSubmit(myMenuItem)
        super.doOKAction()
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return _title.component
    }
}