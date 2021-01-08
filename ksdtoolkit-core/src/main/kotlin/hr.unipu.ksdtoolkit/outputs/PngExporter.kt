package hr.unipu.ksdtoolkit.outputs

import hr.unipu.ksdtoolkit.entities.Model
import javafx.application.Application.launch
import javafx.application.Platform
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import hr.unipu.ksdtoolkit.simulations.Simulation
import javafx.application.Application
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException


/**
 * Class that implements the [SimulationEventListener] interface and controls the chart plotting.
 */
class PngExporter(fileName: String) : ISimulationEventHandler {

    companion object {
        lateinit var simulation: Simulation
    }

    init {
        PngExporterApp.fileName = fileName
        PngExporterApp.model = simulation.model
        PngExporterApp.simulation = simulation
    }

    override fun simulationInitialized(model: Model) {
        PngExporterApp.addSeries(model.modelEntitiesKeys)
    }

    override fun timeStepCalculated(model: Model) {
        PngExporterApp.addValues(model.modelEntitiesValues, model.currentTime)
    }

    override fun simulationFinished(model: Model) {
        // Application is not just a window, it's a Process.
        // Thus only one Application.launch() is allowed per VM.
        try {
            LoggerFactory.getLogger(javaClass).info("PngExporterApp launching attempt.")
            PngExporterApp.pngExporterListener = this
            launch(PngExporterApp::class.java)

        } catch (exception: IllegalStateException) {
            LoggerFactory.getLogger(javaClass).error("PngExporterApp launching failed.", exception)
        }

    }
}
