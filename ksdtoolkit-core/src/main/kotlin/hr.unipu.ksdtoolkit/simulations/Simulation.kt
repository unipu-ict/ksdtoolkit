package hr.unipu.ksdtoolkit.simulations

import hr.unipu.ksdtoolkit.entities.*
import hr.unipu.ksdtoolkit.outputs.CsvExporter
import hr.unipu.ksdtoolkit.outputs.PngExporter
import hr.unipu.ksdtoolkit.outputs.WinSimulator
import org.slf4j.LoggerFactory


/**
 * Simulation class represents & controls SD simulation.
 */
class Simulation(val model: Model) {

    val outputHandlers = ArrayList<ISimulationEventHandler>()

    var timeElapsed = 0.0
    val timeSteps = (model.finalTime.toDouble() - model.initialTime.toDouble()) / model.timeStep.toDouble()

    /**
     * Running simulation.
     */
    fun run() {

        val start = System.currentTimeMillis()

        runAllPreparations()
        while (this.finalTimeReached()) {
            runOneTimeStep()


            timeElapsed = (System.currentTimeMillis() - start) / 1000.0
        }

        LoggerFactory.getLogger(javaClass).info("   Simulation current time (finished): ${model.currentTime}")
        LoggerFactory.getLogger(javaClass).info("   TOTAL TIME ELAPSED: $timeElapsed \n")

        this.fireSimulationFinishedEvent(model)
    }



    /**
     * Prepare all before while loop integration.
     */
    fun runAllPreparations() {
        this.copyModuleEntitiesToParent()
        this.prepareInitialValues()
        this.prepareValuesForFirstTimestep()
        this.fireSimulationInitializedEvent(this.model)
        this.executeConverters()
        this.fireTimeStepCalculatedEvent(this.model)
    }


    /**
     * Run integration for one time step.
     */
    fun runOneTimeStep() {
        this.updateCurrentTime()
        this.prepareValuesForNextTimestep()
        this.model.integrationType.integrate()
        this.executeConverters()
        this.fireTimeStepCalculatedEvent(this.model)
    }


    /**
     * Copy all module entities into the parent model before simulation.
     */
    private fun copyModuleEntitiesToParent() {
        for (( moduleName , module) in model.modules) {
            module.entities.forEach { ( modelEntityName , modelEntity) ->
                modelEntity.name = "${moduleName}.${modelEntityName}"
                if (!model.entities.containsKey(modelEntity.name))
                    model.addModelEntity(modelEntity)
            }
        }
    }


    /**
     * Prepare all initial model values for running the simulation.
     */
    private fun prepareInitialValues() {
        this.model.currentTime = this.model.initialTime.toDouble()
        this.model.entities.forEach { (k, v) ->
            when (v) {
                is Stock -> {
                    v.currentValue = v._initialValue?.invoke() ?: 0.0
                }
                is Constant -> {
                    v.currentValue = v._equation?.invoke() ?: 0.0
                }
            }
            v.isCurrentValueCalculated = false
        }
        this.model.integrationType.stocks = model.stocks
        this.model.integrationType.converters = model.converters
        this.model.integrationType.dt = model.timeStep.toDouble()

    }


    /**
     * Prepare all Stocks whose current value is already calculated
     * for the first timestep.
     */
    private fun prepareValuesForFirstTimestep() {
        this.model.entities.forEach { (k, v) ->
            if (v is Stock && this.model.currentTime == this.model.initialTime) {
                v.isCurrentValueCalculated = true
            }
            if (v is Constant && this.model.currentTime == this.model.initialTime) {
                v.isCurrentValueCalculated = true
            }
        }
    }


    /**
     * Fires an event for the initialization of the simulation.
     *
     * @param model [Model] for the simulation.
     */
    private fun fireSimulationInitializedEvent(model: Model) {
        this.outputHandlers.forEach { listener -> listener.simulationInitialized(this.model) }
    }


    /**
     * Execute the converters.
     */
    private fun executeConverters() {
        for (converter in this.model.converters) {
            if (!converter.targetEntity.isCurrentValueCalculated) {
                converter.convert()
            }
        }

        var hasSucceeded = false
        var numberOfTries = 0
        while (!hasSucceeded && numberOfTries<10) {
            numberOfTries++
            this.model.entities.forEach { (_, v) ->
                val invocationResult = v._equation?.invoke()
                if (v.isCurrentValueCalculated && invocationResult != null) {
                    when (v) {
                        is Converter -> {
                            v.currentValue = invocationResult
                            v.isCurrentValueCalculated = true

                        }
                        is Flow -> {
                            v.currentValue = invocationResult
                            v.isCurrentValueCalculated = true

                        }
                    }
                }
            }
            if (this.model.entities.all { it.value.isCurrentValueCalculated } ) {
                hasSucceeded = true
            } else {

                this.model.entities.filter { it.value.isCurrentValueCalculated == false }.forEach {

                }

            }
        }


        this.model.entities.filter { !it.value.isCurrentValueCalculated }.forEach { (_, v) ->
            val invocationResult = v._equation?.invoke()
            if (!v.isCurrentValueCalculated) {
                LoggerFactory.getLogger(javaClass).info("Not yet calculated: ${v.toString()}")
            }
        }


    }


    /**
     * Fire an event for a finished calculation of a time step.
     *
     * @param model [Model] for the simulation.
     */
    private fun fireTimeStepCalculatedEvent(model: Model) {
        this.outputHandlers.forEach { listener -> listener.timeStepCalculated(model) }
    }


    /**
     * Update the current time by adding one time step.
     */
    private fun updateCurrentTime() {
        model.currentTime = model.currentTime + model.timeStep.toDouble()

        if ( (model.currentTime / model.timeStep.toDouble()).rem(timeSteps / 10.0) == 0.0 ) {
            LoggerFactory.getLogger(javaClass).
                info("   Simulation percentage: ${model.currentTime / model.timeStep.toDouble() / timeSteps * 100}%, " +
                        "Simulation current time: ${model.currentTime}")
            LoggerFactory.getLogger(javaClass).
                info("   Time elapsed: $timeElapsed")
        }
    }


    /**
     * Prepare all values for the next timestep.
     */
    private fun prepareValuesForNextTimestep() {
        this.model.entities.forEach { (_, v) ->
            v.previousValue = v.currentValue
            when (v) {
                is Constant -> v.isCurrentValueCalculated = true
                else -> v.isCurrentValueCalculated = false
            }
        }
    }


    /**
     * Method that controls if the final time has been reached.
     *
     * @return <tt>true</tt> only if the final time has been reached.
     */
    private fun finalTimeReached(): Boolean {
        return this.model.currentTime < this.model.finalTime.toDouble()
    }


    /**
     * Fires an event for a finished simulation.
     *
     * @param model [Model] for the simulation.
     */
    private fun fireSimulationFinishedEvent(model: Model) {
        this.outputHandlers.forEach { listener -> listener.simulationFinished(model) }
    }


    /**
     * Adds an listener that handles simulation events (in classical API form).
     *
     * @param listener [SimulationEventListener]
     */
    fun addSimulationEventListener(listener: ISimulationEventHandler) {
        val isListenerAlreadyAvailable = this.outputHandlers.contains(listener)
        if (!isListenerAlreadyAvailable) {
            this.outputHandlers.add(listener)
        }
    }


    /**
     * Removes a [SimulationEventListener].
     *
     * @param listener [SimulationEventListener]
     */
    fun removeSimulationEventListener(listener: ISimulationEventHandler) {
        this.outputHandlers.remove(listener)
    }


    /**
     * Adds an listener that handles simulation events (in form of lambda expression).
     */
    fun addSimulationEventListener(
        listenerAction: Simulation.() -> ISimulationEventHandler
    ) : Unit {
        outputHandlers.add(listenerAction(this))
    }


    /**
     * Adds multiple listeners that handles simulation events (using lambda with receiver).
     */
    val outputs = SimulationEventListenersHandler()
    inner class SimulationEventListenersHandler {
        fun add(listener: ISimulationEventHandler) {
            this@Simulation.outputHandlers.add(listener)
        }

        fun CsvExporter(csvFile: String = "output.csv", separator: String = ";"): CsvExporter {
            val listener = hr.unipu.ksdtoolkit.outputs.CsvExporter(csvFile, separator)
            add(listener)
            return listener
        }

        fun PngExporter(pngFile: String = "output.png"): PngExporter {
            PngExporter.simulation = this@Simulation
            val listener = hr.unipu.ksdtoolkit.outputs.PngExporter(pngFile)
            add(listener)
            return listener
        }

        fun WinSimulator(): WinSimulator {
            WinSimulator.simulation = this@Simulation
            val listener = hr.unipu.ksdtoolkit.outputs.WinSimulator()
            add(listener)
            return listener
        }

        operator fun invoke(
            body: SimulationEventListenersHandler.() -> Unit) {
            body()
        }
    }



}