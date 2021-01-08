package hr.unipu.mobilesimulatorapp


import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.models.*
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.hamcrest.CoreMatchers
import org.junit.Test

import org.junit.Assert.*
import org.slf4j.LoggerFactory

/**
 * Local unit test, which will execute on local JVM.
 * - Use these tests to minimize execution time when your tests have no Android framework dependencies or
 *   when you can mock the Android framework dependencies.
 * - More info: https://developer.android.com/training/testing/unit-testing/local-unit-tests
 */
class MobSimulatorLocalUnitTest {

    private val model: Model
    private var simulation: Simulation

    init {
        // Create generic model.
        model = ModelGenericSD()

        // Create the simulation.
        simulation = Simulation(model)

        // Add results handlers.
        MobSimulatorApp.simulation = simulation       // Add simulation (and model) objects to app companion.
        simulation.addSimulationEventListener(MobSimulator())

        // Run the simulation
        //simulation.run()
    }



    // Testing that model is accessible from mobile app.
    @Test fun modelTest() {

        LoggerFactory.getLogger(javaClass).info("\n---modelTest---")

        // Testing that model is created.
        assertThat(MobSimulatorApp.simulation.model.name, CoreMatchers.`is`("Generic SD Model"))
    }


    // Testing that simulation is accessible from mobile app.
    @Test fun simulationTest() {

        LoggerFactory.getLogger(javaClass).info("\n---simulationTest---")

        // Testing that model is passed to simulation.
        assertThat(MobSimulatorApp.simulation.model.name, CoreMatchers.`is`("Generic SD Model"))
    }

}