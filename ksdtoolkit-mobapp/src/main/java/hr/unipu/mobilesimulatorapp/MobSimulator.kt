package hr.unipu.mobilesimulatorapp

import hr.unipu.ksdtoolkit.entities.Constant
import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler

/**
 * Class that implements the [SimulationEventListener] interface and controls the chart printing on Android.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class MobSimulator : ISimulationEventHandler {

    override fun simulationInitialized(model: Model) {
        MobSimulatorApp.addConstants(model.entities.filterValues { it is Constant })
        MobSimulatorApp.addSeriesNames(model.modelEntitiesKeys)
    }

    override fun timeStepCalculated(model: Model) {
        MobSimulatorApp.addSeriesValues(model.modelEntitiesValues, model.currentTime)
    }

    override fun simulationFinished(model: Model) {
        MobSimulatorApp.simulationFinished = true
    }

}