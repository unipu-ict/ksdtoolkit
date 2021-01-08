package hr.unipu.websimulatorapp

import hr.unipu.ksdtoolkit.entities.Constant
import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import hr.unipu.websimulatorapp.webapp.WebSimulatorApp

/**
 * Class that implements the [SimulationEventListener] interface and controls the chart printing on web.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class WebSimulator : ISimulationEventHandler {

    override fun simulationInitialized(model: Model) {
        WebSimulatorApp.addConstants(model.entities.filterValues { it is Constant })
        WebSimulatorApp.addSeriesNames(model.modelEntitiesKeys)

    }

    override fun timeStepCalculated(model: Model) {
        WebSimulatorApp.addSeriesValues(model.modelEntitiesValues, model.currentTime)
    }

    override fun simulationFinished(model: Model) {
        WebSimulatorApp.simulationFinished = true
    }
}
