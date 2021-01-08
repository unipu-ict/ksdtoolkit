package hr.unipu.ksdtoolkit

import hr.unipu.ksdtoolkit.entities.*
import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.integration.Integration
import hr.unipu.ksdtoolkit.modules.ModuleGenericCompoundDecrease
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Unit testing all entities: Model, Constant, Converter, Flow, Stock, Module.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class `1_EntitiesTest` {
    val model: Model
    val CONSTANT: Constant
    val converter: Converter
    val flow: Flow
    val Stock: Stock
    val Module: ModuleGenericCompoundDecrease


    // Prepare model and model entities for testing.
    init {
        model = Model()

        model.name = "Model_name"
        model.initialTime = 0.0
        model.finalTime = 100.0
        model.timeStep = 0.25
        model.integrationType = EulerIntegration()

        CONSTANT = model.constant("CONSTANT_NAME")
        CONSTANT.equation = { 1.0 }

        converter = model.converter("converter_name")
        converter.equation = { 2 * 1.0 }

        flow = model.flow("flow_name")
        flow.equation = { 1 + 1.0 }

        Stock = model.stock("Stock_name")
        Stock.initialValue = { 0.0 }
        Stock.equation = { -1.0 }

        Module = model.createModule(
            "Module",
            "hr.unipu.ksdtoolkit.modules.ModuleGenericCompoundDecrease"
        ) as ModuleGenericCompoundDecrease
    }


    @Test fun constantTest() {

        // Testing that constant is created.
        assertThat(CONSTANT.name, `is`("CONSTANT_NAME"))

        // Testing that constant value is set.
        assertThat(CONSTANT.equation?.invoke() as Double, `is`(1.0))
    }


    @Test fun converterTest() {

        // Testing that converter is created.
        assertThat(converter.name, `is`("converter_name"))

        // Testing that converter equation works.
        assertThat(converter.equation?.invoke() as Double, `is`(2.0))
    }


    @Test fun flowTest() {

        // Testing that flow is created.
        assertThat(flow.name, `is`("flow_name"))

        // Testing that flow equation works.
        assertThat(flow.equation?.invoke() as Double, `is`(2.0))
    }


    // Abstract method ModelEntity is tested using implementation of concrete classes.
    /*
    @Test fun modelEntityTest() { }
     */


    @Test fun stockTest() {

        // Testing that Stock is created.
        assertThat(Stock.name, `is`("Stock_name"))

        // Testing Stock initial value works.
        assertThat(Stock.initialValue?.invoke() as Double, `is`(0.0))

        // Testing Stock equation works.
        assertThat(Stock.equation?.invoke() as Double, `is`(-1.0))
    }


    @Test fun moduleTest() {

        // Testing that Module is created.
        assertThat(Module.name, `is`("Module"))

        // Test that all Module entities can be accessed.
        assertThat(Module.entities.keys, `is`(setOf("CONSTANT", "INITIAL_STOCK", "Stock",
            "inflow", "outflow", "converter" )))

        // Testing equation in Module works.
        assertThat(Module.INITIAL_STOCK.equation?.invoke() as Double, `is`(100.0))
    }


    @Test fun modelTest() {

        // Testing that model is created.
        assertThat(model.name, `is`("Model_name"))

        // Testing that setup works correctly.
        assertThat(model.initialTime.toDouble(), `is`(0.0))
        assertThat(model.finalTime.toDouble(), `is`(100.0))
        assertThat(model.timeStep.toDouble(), `is`(0.25))

        // Testing that setting integration type works correctly.
        assertThat(model.integrationType, instanceOf(EulerIntegration::class.java))

        // Testing that all entities are in the model.
        assertThat(model.entities, `is`(hashMapOf("CONSTANT_NAME" to CONSTANT,
            "converter_name" to converter,
            "flow_name" to flow,
            "Stock_name" to Stock)))
    }

}