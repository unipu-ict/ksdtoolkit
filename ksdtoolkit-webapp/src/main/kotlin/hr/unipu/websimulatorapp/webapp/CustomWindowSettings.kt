package hr.unipu.websimulatorapp.webapp

import com.vaadin.server.Sizeable
import com.vaadin.ui.FormLayout
import com.vaadin.ui.TextField
import com.vaadin.ui.Window


class CustomWindowSettings @JvmOverloads constructor(caption: String = "Simulation Settings") : Window(caption) {

    init {
        this.setWidth(400.0f, Sizeable.Unit.PIXELS)

        val form = FormLayout()

        val tf1 = TextField("Initial time:")
        tf1.value = WebSimulatorApp.simulation.model.initialTime.toString()
        tf1.addValueChangeListener { valueChangeEvent -> WebSimulatorApp.simulation.model.initialTime = java.lang.Double.parseDouble(valueChangeEvent.value) }
        form.addComponent(tf1)

        val tf2 = TextField("Final time:")
        tf2.value = WebSimulatorApp.simulation.model.finalTime.toString()
        tf2.addValueChangeListener { valueChangeEvent -> WebSimulatorApp.simulation.model.finalTime = java.lang.Double.parseDouble(valueChangeEvent.value) }
        form.addComponent(tf2)

        val tf3 = TextField("Time step:")
        tf3.value = WebSimulatorApp.simulation.model.timeStep.toString()
        tf3.addValueChangeListener { valueChangeEvent -> WebSimulatorApp.simulation.model.timeStep = java.lang.Double.parseDouble(valueChangeEvent.value) }
        form.addComponent(tf3)


        this.addCloseListener { closeEvent ->
            WebSimulatorApp.simulationFinished == false
        }

        form.setMargin(true)
        this.content = form
    }
}
