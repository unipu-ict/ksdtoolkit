package hr.unipu.ksdtoolkit

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.models.*
import org.hamcrest.CoreMatchers.*
import hr.unipu.ksdtoolkit.entities.*
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit testing models:
 * - GenericSD,
 * - SimpleCompoundInterest, InheritedCompoundInterest,
 * - BassDiffusion, InnovationDiffusion,
 * - TestSpeed model.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class `3_ModelsTest` {

    private val model1: Model
    private val model2: Model
    private val model3: Model
    private val model4: Model
    private val model5: Model
    private val model6: Model

    init {
        model1 = ModelGenericSD()
        model2 = ModelSimpleCompoundInterest()
        model3 = ModelInheritedCompoundInterest()
        model4 = ModelBassDiffusion()
        model5 = ModelInnovationDiffusion()
        model6 = ModelTestSpeed()
    }


    @Test
    fun model1Test() {

        // Testing that model is created.
        assertThat(model1.name, `is`("Generic SD Model"))

        // Testing that setup works correctly.
        assertThat(model1.initialTime.toDouble(), `is`(0.0))
        assertThat(model1.finalTime.toDouble(), `is`(120.0))
        assertThat(model1.timeStep.toDouble(), `is`(0.25))

        // Testing that setting integration type works correctly.
        assertThat(model1.integrationType, instanceOf(EulerIntegration::class.java))

        // Testing Stock initial value works.
        assertThat((model1.entities["Stock"] as Stock)._initialValue?.invoke(), `is`(100.0))

        // Testing that all entities are in the model.
        assertThat(
            model1.entities.keys, `is`(
                hashMapOf(
                    "CONSTANT" to Constant("CONSTANT"),
                    "INITIAL_STOCK" to Constant("INITIAL_STOCK"),
                    "converter" to Converter("converter"),
                    "inflow" to Flow("inflow"),
                    "outflow" to Flow("outflow"),
                    "Stock" to Stock("Stock")
                ).keys
            )
        )

        assertThat(
            model1.entities.keys, `is`(
                setOf(
                    "CONSTANT",
                    "INITIAL_STOCK",
                    "converter",
                    "inflow",
                    "outflow",
                    "Stock"
                )
            )
        )

        // Testing equation in model works.
        assertThat( (((model1.entities["Stock"] as Stock)
            .initialValue?.invoke() as ModelEntity)
            .equation?.invoke() as Int).toDouble(), `is`(closeTo(100.0, 0.001)))

        // Testing that Module is created.
        assertThat(model1.modules["Module"]?.name, `is`("Module"))

        // Test that all Module entities can be accessed.
        assertThat(model1.modules["Module"]?.entities?.keys, `is`(setOf("CONSTANT", "INITIAL_STOCK", "Stock",
            "inflow", "outflow", "converter" )))

        // Testing equation in Module works.
        assertThat( (model1.modules["Module"]?.entities!!["Stock"] as Stock)._initialValue?.invoke(),
        `is`(100.0))


    }



    @Test
    fun model2Test() {

        // Testing that model is created.
        assertThat(model2.name, `is`("Simple Compound Interest Model"))

        // Other assertions are similar to the first test.

    }



    @Test
    fun model3Test() {

        // Testing that model is created.
        assertThat(model3.name, `is`("Inherited Compound Interest Model"))

        // Other assertions are similar to the first test.

    }


    @Test
    fun model4Test() {

        // Testing that model is created.
        assertThat(model4.name, `is`("Bass Diffusion Model"))

        // Other assertions are similar to the first test.

    }


    @Test
    fun model5Test() {

        // Testing that model is created.
        assertThat(model5.name, `is`("Innovation/Product Diffusion Model"))

        // Other assertions are similar to the first test.

    }


    @Test
    fun model6Test() {

        // Testing that model is created.
        assertThat(model6.name, `is`("SD Model for testing speed"))

        // Other assertions are similar to the first test.

    }


}

