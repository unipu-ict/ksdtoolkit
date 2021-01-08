package hr.unipu.ksdtoolkit.modules

import hr.unipu.ksdtoolkit.entities.Model

/**
 * SD model of Simple Compound Interest.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

open class ModuleGenericCompoundDecrease : Model() {

    // Static properties:
    companion object {
        const val CONSTANT_KEY = "CONSTANT"
        const val CONVERTER_KEY = "converter"
        const val INITIAL_STOCK_KEY = "INITIAL_STOCK"
        const val OUTFLOW_KEY = "outflow"
        const val INFLOW_KEY = "inflow"
        const val STOCK_KEY = "Stock"

        const val CONSTANT_VALUE = 10.0           // [%]
        const val INITIAL_STOCK_VALUE = 100.0     // [€]
    }


    // 1. Create the model (with setup of: time boundaries & time step & integrationType type)
    val model = this   // inheritance: Model()
    // alternative: Model(INITIAL_TIME_VALUE, FINAL_TIME_VALUE, TIME_STEP_VALUE, EulerIntegration())


    init {
        // override default model properties:
        model.name = "Generic Compound Decrease Module"   // name is optional
    }


    // 2. Create all system elements:

    // - 2a. Variables: Constants
    val CONSTANT = model.constant(CONSTANT_KEY)
    val INITIAL_STOCK = model.constant(INITIAL_STOCK_KEY)

    // - 2b. Variable: Converters
    val converter = model.converter(CONVERTER_KEY)

    // - 2c. Stocks
    val Stock = model.stock(STOCK_KEY)

    // - 2d. Flows
    val inflow = model.flow(INFLOW_KEY)
    val outflow = model.flow(OUTFLOW_KEY)

    // - 2e. Modules
    // n/a

    init {
        // - 2f. (Optional): Entities' descriptions
        CONSTANT.description = "Annual flow rate in [%/year]"
        INITIAL_STOCK.description = "Initial capital in [EUR] in the beginning of the simulation."
        converter.description = "Converts percentage to decimal."
        Stock.description = "Accumulated capital in [EUR] at specific point in time."
        inflow.description = "Interest inflow in [EUR / chosen unit of time], e.g. [EUR/month]"
        outflow.description = "Interest outflow in [EUR / chosen unit of time], e.g. [EUR/month]"



        // 3. Initial values:

        // - 3a. Stocks
        Stock.initialValue = { INITIAL_STOCK }      // Accepts: Double, Int or ModelEntity



        // 4. Equations:

        // - 4a. Variables (Constants)
        CONSTANT.equation = { CONSTANT_VALUE }
        INITIAL_STOCK.equation = { INITIAL_STOCK_VALUE }

        // - 4b. Variables (Converters)
        converter.equation = { CONSTANT / 100.0 / 12.0 }

        // - 4c. Stocks
        Stock.equation = { inflow - outflow }     // Function type can be either Double or ModelEntity.

        // - 4d. Flows:
        inflow.equation = { }                       // Not defined. Can be also defined as: { null }. Module input.
        outflow.equation = { Stock * converter }    // Simplified converters so that only equations are used.

        // - 4e. Modules:
        // n/a


    }

}
