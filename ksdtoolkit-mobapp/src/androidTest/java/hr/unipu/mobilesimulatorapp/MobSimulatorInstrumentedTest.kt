package hr.unipu.mobilesimulatorapp

import android.app.PendingIntent.getActivity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.models.ModelGenericSD
import hr.unipu.ksdtoolkit.models.ModelInnovationDiffusion
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory


/**
 * Instrumented test, which will execute on an Android device or emulator.
 * - These tests have access to Instrumentation APIs, give you access to information such as the Context of the app
 *   you are testing, and let you control the app under test from your test code.
 * - The Gradle build interprets these test source sets in the same manner as it does for your project's app source
 *   sets, which allows you to create tests based on build variants.
 * - More info: https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests
 */

@RunWith(AndroidJUnit4::class)      // AndroidJUnit4 as default test runner.
class MobSimulatorInstrumentedTest {

    @After fun launchApp() {
        launchActivity<MobSimulatorApp>()
        Thread.sleep(30000)     // Automatic Testing closes app after tests are finished.
                                     // Manual testing needs this pause for postponing the app closing.
    }

    private val model: Model
    private var simulation: Simulation

    init {
        // Create generic model.
        //model = ModelGenericSD()
        model = ModelInnovationDiffusion()

        // Create the simulation.
        simulation = Simulation(model)

        // Add results handlers.
        MobSimulatorApp.simulation = simulation       // Add simulation (and model) objects to app companion.
        simulation.addSimulationEventListener(MobSimulator())

        // Run the simulation
        simulation.run()
    }


    /**
     * Testing that simulation results are accessible from the mobile app.
     */
    @Test fun mobSimulatorTest() {

        // Initial test that testing framework is working.
        // - Testing that context of the app under test is correct.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertThat(appContext.packageName, containsString("hr.unipu.mobilesimulatorapp"))


        // Testing that model is accessible from the mobile app.
        Assert.assertNotNull(model.name)
        LoggerFactory.getLogger(javaClass).info("\n---modelTest---")


        // Testing that simulation is accessible from the mobile app.
        // - Testing that model is passed to simulation.
        Assert.assertNotNull(MobSimulatorApp.simulation.model.name)
        LoggerFactory.getLogger(javaClass).info("\n---simulationTest---")


        // Testing that MobSimulatorApp contains all data series.
        val entities = MobSimulatorApp.simulation.model.modelEntitiesKeys
        for (entity in entities) {
            Assert.assertThat(MobSimulatorApp.seriesName.toString(), containsString(entity))
        }
        LoggerFactory.getLogger(javaClass).info("---mobSimulatorTest---")


        // Testing that MobSimulatorApp contains all simulation data values.
        val values = simulation.model.modelEntitiesValues
        for ((index, value) in values.withIndex()) {
            Assert.assertThat(
                MobSimulatorApp.seriesValue[index][0].y.toString().replace(",", ".").toDouble(),
                `is`(Matchers.closeTo(value.replace(",", ".").toDouble(), 0.001))
            )
        }

    }





}