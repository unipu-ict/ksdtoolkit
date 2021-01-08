package hr.unipu.ksdtoolkit

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.models.*
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Test
import org.slf4j.LoggerFactory
import hr.unipu.ksdtoolkit.models.ModelTestSpeed as ModelTestSpeed


/**
 * Unit testing simulation run for all models:
 * - GenericSD,
 * - SimpleCompoundInterest, InheritedCompoundInterest,
 * - BassDiffusion, InnovationDiffusion,
 * - TestSpeed model.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class `4_SimulationRunTest` {

    private var myModel: Model
    private val myModel1: Model
    private val myModel2: Model
    private val myModel3: Model
    private val myModel4: Model
    private val myModel5: Model
    private val myModel6: Model

    private var mySimulation: Simulation
    private val mySimulation1: Simulation
    private val mySimulation2: Simulation
    private val mySimulation3: Simulation
    private val mySimulation4: Simulation
    private val mySimulation5: Simulation
    private val mySimulation6: Simulation


    init {
        // 1-3. Create the model
        myModel1 = ModelGenericSD()
        myModel2 = ModelSimpleCompoundInterest()
        myModel3 = ModelInheritedCompoundInterest()
        myModel4 = ModelBassDiffusion()
        myModel5 = ModelInnovationDiffusion()
        myModel6 = ModelTestSpeed()


        // 4. Create the simulation
        mySimulation1 = Simulation(myModel1)
        mySimulation2 = Simulation(myModel2)
        mySimulation3 = Simulation(myModel3)
        mySimulation4 = Simulation(myModel4)
        mySimulation5 = Simulation(myModel5)
        mySimulation6 = Simulation(myModel6)


        // select the model & simulation for the output
        myModel = myModel1
        mySimulation = mySimulation1

    }


    @Test
    fun simulation1RunTest() {

        // Testing that model is passed to simulation.
        assertThat(mySimulation1.model.name, `is`("Generic SD Model"))


        // Testing that setup works correctly.
        assertThat(mySimulation1.model.initialTime.toDouble(), `is`(0.0))
        assertThat(mySimulation1.model.finalTime.toDouble(), `is`(120.0))
        assertThat(mySimulation1.model.timeStep.toDouble(), `is`(0.25))


        // Testing model & simulation at time=0 (beginning of simulation).
        LoggerFactory.getLogger(javaClass).info("Running Simulation 0: at final time=0")
        myModel1.finalTime = 0
        mySimulation1.run()
        assertThat(myModel1.entities["INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["Module.converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Module.CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["Module.INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))


        // Testing model & simulation at time=60 (middle of simulation).
        LoggerFactory.getLogger(javaClass).info("Running Simulation 1: at final time=60")
        myModel1.finalTime = 60
        mySimulation1.run()
        assertThat(myModel1.entities["INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["Module.converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Module.CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["Module.INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))



        // Testing model & simulation at time=120 (end of simulation).
        LoggerFactory.getLogger(javaClass).info("Running Simulation 2: at final time=120")
        myModel1.finalTime = 120
        mySimulation1.run()
        assertThat(myModel1.entities["INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.inflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.outflow"]?.currentValue, `is`(closeTo(0.8333, 0.001)))
        assertThat(myModel1.entities["Module.Stock"]?.currentValue, `is`(closeTo(100.0, 0.001)))
        assertThat(myModel1.entities["Module.converter"]?.currentValue, `is`(closeTo(0.0083, 0.001)))
        assertThat(myModel1.entities["Module.CONSTANT"]?.currentValue, `is`(closeTo(10.0, 0.001)))
        assertThat(myModel1.entities["Module.INITIAL_STOCK"]?.currentValue, `is`(closeTo(100.0, 0.001)))


    }


    @Test
    fun simulation2RunTest() {

        // Testing that model is passed to simulation
        assertThat(mySimulation2.model.name, `is`("Simple Compound Interest Model"))
    }


    @Test
    fun simulation3RunTest() {

        // Testing that model is passed to simulation
        assertThat(mySimulation3.model.name, `is`("Inherited Compound Interest Model"))
    }


    @Test
    fun simulation4RunTest() {

        // Testing that model is passed to simulation
        assertThat(mySimulation4.model.name, `is`("Bass Diffusion Model"))
    }


    @Test
    fun simulation5RunTest() {

        // Testing that model is passed to simulation
        assertThat(mySimulation5.model.name, `is`("Innovation/Product Diffusion Model"))
    }


    @Test
    fun simulation6RunTest() {

        // Testing that model is passed to simulation
        assertThat(mySimulation6.model.name, `is`("SD Model for testing speed"))
    }


}