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
 * currentTree needed to determine what, if anything is selected
 */
class MyDialog(val model: MyMenuItem, val onSubmit: (MyMenuItem) -> Unit): DialogWrapper(true) {

    private lateinit var _title: Cell<JBTextField>
    private lateinit var _description: Cell<JBTextField>
    private lateinit var _url: Cell<JBTextField>
    private lateinit var _isCollection: Cell<JBCheckBox>

    private val panel = panel {
        row("Title: ") {
            _title = textField().bindText(model::text)
        }
        row("Description: ") {
            _description = textField().bindText(model::description)
        }
        row {
            _isCollection = checkBox("Is Collection").selected(model.children != null)
        }
        row("Url: ") {
            _url = textField().text(if (model.url == null) "" else model.url!!)
        }.enabledIf(_isCollection.selected.not())
    }

    override fun doValidate(): ValidationInfo? {
        if (_title.component.text.isBlank()) return ValidationInfo("Title is required!")
        if (_description.component.text.isBlank()) return ValidationInfo("Description is required!")
        if (!_isCollection.component.isSelected && _url.component.text.isBlank()) return ValidationInfo("Url is required!")
        return null
    }

    init {
        title = "Maintain Entry"
        init()
    }

    override fun createCenterPanel() = panel

    override fun doOKAction() {
        panel.apply() // needed to set
        val isCollection = _isCollection.component.isSelected
        model.url = if (isCollection) null else _url.component.text
        model.children = if (isCollection) mutableListOf() else null
        onSubmit(model)
        super.doOKAction()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return _title.component
    }
}