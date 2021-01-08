package hr.unipu.ksdtoolkit.models

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration

/**
 * SD model of Simple Compound Interest.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

open class ModelTestSpeed : Model() {

    // Static properties:
    companion object {
        const val INITIAL_STOCK_KEY = "INITIAL_STOCK"
        const val FLOW_KEY = "flow"
        const val STOCK_KEY = "Stock"

        const val INITIAL_STOCK_VALUE = 1      // []
        const val FLOW_VALUE = 1               // []
        const val INITIAL_TIME_VALUE = 0       // []
        const val FINAL_TIME_VALUE = 1e7       // []    1e7 for comparison (Vensim = cca30sec., BPTK-Py = cca153sec.)
        const val TIME_STEP_VALUE = 1          // []
    }


    // Time_steps=1e3 -> Time elapsed=0.093  -> 0.036 sec
    // Time_steps=1e4 -> Time elapsed=0.192  -> 0.099 sec
    // Time_steps=1e5 -> Time elapsed=0.704  -> 0.389 sec
    // Time_steps=1e6 -> Time elapsed=4.367  -> 1.034 sec
    // Time_steps=1e7 -> Time elapsed=27.99  -> 7.082 sec

    init {

        // 1. Create the model (with setup of: time boundaries & time step & integrationType type)
        val model = this

        // override default model properties:
        model.initialTime = INITIAL_TIME_VALUE
        model.finalTime = FINAL_TIME_VALUE
        model.timeStep = TIME_STEP_VALUE
        model.integrationType = EulerIntegration()
        model.name = "SD Model for testing speed"   // name is optional



        // 2. Create all system elements:

        // - 2a. Variables (Constants)
        val INITIAL_STOCK = model.constant(INITIAL_STOCK_KEY)

        // - 2b. Variables (Converters)

        // - 2c. Stocks
        val Stock = model.stock(STOCK_KEY)

        // - 2d. Flows
        val flow = model.flow(FLOW_KEY)

        // - 2e. Modules

        // - 2f. (Optional): Entities' descriptions



        // 3. Initial values:

        // - 3a. Stocks
        Stock.initialValue = { INITIAL_STOCK }      // Accepts: Double, Int or ModelEntity



        // 4. Equations:

        // - 4a. Constants
        INITIAL_STOCK.equation = { INITIAL_STOCK_VALUE }

        // - 4b. Converters

        // - 4c. Stocks
        Stock.equation = { flow }  // Function type can be either Double or ModelEntity.

        // - 4d. Flows:
        flow.equation = { 1.0 }    // Simplified converters so that only equations are used.

        // - 4e. Modules:


    }

}
