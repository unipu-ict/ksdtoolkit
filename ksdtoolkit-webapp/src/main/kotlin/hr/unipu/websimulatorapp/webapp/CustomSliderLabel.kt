package hr.unipu.websimulatorapp.webapp

import com.vaadin.server.Sizeable
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class CustomSliderLabel : CustomField<String>() {
    private val sliderCaption = Label()
    val slider = Slider()
    private val sliderValue = Label()
    var modelVariableName = ""

    override fun initContent(): Component {

        sliderCaption.styleName = ValoTheme.LABEL_SMALL
        sliderCaption.setWidth("200px")

        slider.styleName = ValoTheme.TEXTFIELD_SMALL
        slider.setWidth(100f, Sizeable.Unit.PERCENTAGE)

        sliderValue.styleName = ValoTheme.LABEL_SMALL
        sliderValue.setWidth("100px")

        val myLayout = HorizontalLayout()
        myLayout.addComponent(sliderCaption)
        myLayout.addComponent(slider)
        myLayout.addComponent(sliderValue)
        myLayout.setComponentAlignment(sliderCaption, Alignment.MIDDLE_LEFT)
        myLayout.setComponentAlignment(slider, Alignment.MIDDLE_LEFT)
        myLayout.setComponentAlignment(sliderValue, Alignment.MIDDLE_RIGHT)
        myLayout.styleName = ValoTheme.LAYOUT_COMPONENT_GROUP

        return myLayout
    }

    override fun getValue(): String {
        return sliderValue.value
    }

    override fun setValue(value: String) {
        this.sliderValue.value = value
    }

    fun setValue(value: Double?) {
        this.slider.value = value!!
    }

    override fun getCaption(): String {
        return sliderCaption.value
    }

    override fun setCaption(value: String) {
        this.sliderCaption.value = value
    }

    override fun doSetValue(s: String) {

    }

    fun setResolution(resolution: Int) {
        this.slider.resolution = resolution
    }

    fun setMin(resolution: Double) {
        this.slider.min = resolution
    }

    fun setMax(resolution: Double) {
        this.slider.max = resolution
    }

}
