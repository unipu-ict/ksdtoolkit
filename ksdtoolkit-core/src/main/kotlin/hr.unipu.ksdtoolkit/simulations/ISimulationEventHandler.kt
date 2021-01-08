package hr.unipu.ksdtoolkit.simulations

import hr.unipu.ksdtoolkit.entities.Model

interface ISimulationEventHandler {
    /**
     * Handler for a simulation initialization event.
     */
    fun simulationInitialized(model: Model)

    /**
     * Handler for a finished calculation for one time step.
     */
    fun timeStepCalculated(model: Model)

    /**
     * Handler for a finished simulation event.
     */
    fun simulationFinished(model: Model)

}