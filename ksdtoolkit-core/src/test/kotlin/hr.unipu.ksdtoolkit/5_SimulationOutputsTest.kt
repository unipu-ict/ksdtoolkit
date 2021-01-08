package hr.unipu.ksdtoolkit

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.models.*
import hr.unipu.ksdtoolkit.outputs.*
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.junit.Test
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Unit testing simulation outputs:
 * - CsvExporter
 * - PngExporter
 * - WinSimulator
 * - WebSimulator
 * - MobSimulator
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class `5_SimulationOutputsTest` {

    private var model: Model
    private var simulation: Simulation
    private lateinit var csvExporter: CsvExporter       // ISimulationEventHandler needed for tests.
    private lateinit var pngExporter: PngExporter       // ISimulationEventHandler needed for tests.
    private lateinit var winSimulator: WinSimulator       // ISimulationEventHandler needed for tests.

    init {

        // 1-3. Create the model
        model = ModelGenericSD()
        //model = ModelSimpleCompoundInterest()
        //model = ModelInheritedCompoundInterest()
        //model = ModelBassDiffusion()
        //model = ModelInnovationDiffusion()
        //model = ModelTestSpeed()


        // 4. Create the simulation
        simulation = Simulation(model)


        // 5. Add results handlers
        simulation.outputs {
            csvExporter = CsvExporter("output_data.csv", ";")
            pngExporter = PngExporter("output_chart.png")
            winSimulator = WinSimulator()

            // !!! Mobile simulator test has to be run from Android Test, because Java modules
            // cannot depend on Android modules (reverse is ok).
            //MobSimulator()

            //WebSimulator()
        }

        // 6. Run the simulation
        simulation.run()

    }



    @Test fun csvExporterTest() {

        LoggerFactory.getLogger(javaClass).info("\n---CsvExporterTest---")

        // Testing that sb is created.
        assertThat(csvExporter.sb, instanceOf(StringBuilder::class.java))


        // Testing that sb contains all simulation data keys.
        val entities = simulation.model.modelEntitiesKeys
        for (entity in entities) {
            assertThat(csvExporter.sb.toString(), containsString(entity))
        }

        // Testing that sb contains all simulation data values.
        val values = simulation.model.modelEntitiesValues
        for (value in values) {
            assertThat(csvExporter.sb.toString(), containsString(value))
        }

        // Testing that CSV file is saved.
        assertTrue(File("output_data.csv").exists());

    }



    @Test fun pngExporterTest() {

        LoggerFactory.getLogger(javaClass).info("\n---PngExporterTest---")

        // Testing that PngExporterApp is initialized.
        assertThat(PngExporterApp.fileName, `is`("output_chart.png"))

        // Testing that PngExporterApp contains all data series.
        val entities = simulation.model.modelEntitiesKeys
        for (entity in entities) {
            assertThat(PngExporterApp.series.toString(), containsString(entity))
        }

        // Testing that PngExporterApp contains all simulation data values.
        val values = simulation.model.modelEntitiesValues
        for ((index,value) in values.withIndex()) {
            // Quick & dirty to replace decimal point format from "," to "."
            assertThat(PngExporterApp.series[index].data[0].yValue.toString().replace(",",".").toDouble(),
                `is`(closeTo(value.replace(",",".").toDouble(), 0.001)))
        }

        // Testing that PNG file is saved.
        assertTrue(File("output_chart.png").exists());


    }

    @Test fun winSimulatorTest() {
        LoggerFactory.getLogger(javaClass).info("\n---WinSimulatorTest---")
    }


/*

    // !!! Mobile simulator test has to be run from Android Test.
    @Test fun MobSimulatorTest() {
        LoggerFactory.getLogger(javaClass).info("---MobSimulatorTest---")
    }


    @Test fun WebSimulatorTest() {
        LoggerFactory.getLogger(javaClass).info("---WebSimulatorTest---")
    }

 */


}