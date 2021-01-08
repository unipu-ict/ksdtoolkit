package hr.unipu.ksdtoolkit

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.entities.Stock
import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.integration.RungeKuttaIntegration
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit testing numeric integrations: Euler type and RungeKutta type.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class `2_IntegrationsTest` {

    private val model: Model
    private val Stock: Stock
    private val simulation: Simulation

    // Prepare model and model entities for testing.
    init {
        model = Model()
        model.initialTime = 0.0
        model.finalTime = 100.0
        model.timeStep = 0.25

        Stock = model.stock("Stock_name")
        Stock.initialValue = { 0.0 }
        Stock.equation = { -1.0 }

        simulation = Simulation(model)
    }


    @Test fun EulerItegrationTest() {

        model.integrationType = EulerIntegration()
        simulation.runAllPreparations()

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(-0.25))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(-0.50))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(-0.75))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(-1.00))

    }


    @Test fun RugneKuttaIntegrationTest() {

        model.integrationType = RungeKuttaIntegration()
        simulation.runAllPreparations()

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(closeTo(-0.25, 0.01)))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(closeTo(-0.50, 0.01)))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(closeTo(-0.75, 0.01)))

        simulation.runOneTimeStep()
        assertThat(Stock.currentValue as Double, `is`(closeTo(-1.00, 0.01)))
    }


    // Abstract method Integration is tested using implementation of concrete classes.
    /*
    @Test fun ``2_IntegrationsTest``() { }
     */

}

