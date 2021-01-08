package hr.unipu.ksdtoolkit.outputs

import hr.unipu.ksdtoolkit.entities.Constant
import hr.unipu.ksdtoolkit.entities.Model
import javafx.application.Application.launch
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.slf4j.LoggerFactory


/**
 * Class that implements the [ISimulationEventHandler] interface and controls the chart printing.
 *
 */
class WinSimulator() : ISimulationEventHandler {

    companion object {
        lateinit var simulation: Simulation
    }

    init {
        WinSimulatorApp.simulation = simulation
    }

    override fun simulationInitialized(model: Model) {
        WinSimulatorApp.addSeriesNames(model.modelEntitiesKeys)
        WinSimulatorApp.addSeriesConstants(model.entities.filterValues { it is Constant })

    }

    override fun timeStepCalculated(model: Model) {
        WinSimulatorApp.addSeriesValues(model.modelEntitiesValues, model.currentTime)
    }

    override fun simulationFinished(model: Model) {
        try {
            // Application is not just a window, it's a Process.
            // Thus only one Application#launch() is allowed per VM.
            LoggerFactory.getLogger(javaClass).info("WinSimulatorApp launching attempt.")
            WinSimulatorApp.winSimulatorListener = this
            launch(WinSimulatorApp::class.java)

        } catch (exception: IllegalStateException) {
            LoggerFactory.getLogger(javaClass).error("WinSimulatorApp launching failed.", exception)
        }

    }
}
