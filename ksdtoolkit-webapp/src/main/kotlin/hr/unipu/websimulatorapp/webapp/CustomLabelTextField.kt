package hr.unipu.websimulatorapp.webapp

import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class CustomLabelTextField : CustomField<String>() {
    val textField = TextField()
    private val labelText = Label()
    var modelVariableName = ""

    override fun initContent(): Component {

        labelText.styleName = ValoTheme.LABEL_SMALL
        labelText.setWidth("200px")

        textField.styleName = ValoTheme.TEXTFIELD_SMALL
        textField.setWidth("100px")


        val myLayout = HorizontalLayout()
        myLayout.addComponents(labelText)
        myLayout.addComponent(textField)
        myLayout.setComponentAlignment(labelText, Alignment.MIDDLE_LEFT)
        myLayout.setComponentAlignment(textField, Alignment.MIDDLE_RIGHT)
        myLayout.styleName = ValoTheme.LAYOUT_COMPONENT_GROUP

        return myLayout
    }

    override fun getValue(): String {
        return textField.value
    }

    override fun setValue(value: String) {
        this.textField.value = value
    }

    override fun getCaption(): String {
        return labelText.caption
    }

    override fun setCaption(caption: String) {
        this.labelText.caption = caption
    }

    override fun doSetValue(s: String) {

    }

}
