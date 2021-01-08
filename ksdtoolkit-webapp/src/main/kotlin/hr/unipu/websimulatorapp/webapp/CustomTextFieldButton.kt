package hr.unipu.websimulatorapp.webapp

import com.vaadin.server.FontAwesome
import com.vaadin.server.Sizeable
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class CustomTextFieldButton : CustomField<String>() {
    private val textField = TextField()

    override fun initContent(): Component {

        textField.setWidth(100f, Sizeable.Unit.PERCENTAGE)
        textField.styleName = ValoTheme.TEXTFIELD_SMALL
        textField.styleName = ValoTheme.TEXTFIELD_BORDERLESS
        textField.isReadOnly = true

        val button = Button()
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY)
        button.icon = FontAwesome.USER

        val myLayout = CssLayout()
        myLayout.addComponents(textField, button)
        myLayout.styleName = ValoTheme.LAYOUT_COMPONENT_GROUP
        return myLayout
    }

    override fun getValue(): String {
        return textField.value
    }

    override fun setValue(value: String) {
        this.textField.value = value
    }

    override fun doSetValue(s: String) {

    }


}
