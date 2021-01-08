package hr.unipu.ksdtoolkit.models

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration

/**
 * SD model of Simple Compound Interest.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

open class ModelSimpleCompoundInterest : Model() {

    // Static properties:
    companion object {
        const val INTEREST_RATE_KEY = "INTEREST_RATE"
        const val INITIAL_CAPITAL_KEY = "INITIAL_CAPITAL"
        const val INTEREST_KEY = "interest"
        const val CAPITAL_KEY = "Capital"

        const val INTEREST_RATE_VALUE = 0.1 / 12
        const val INITIAL_CAPITAL_VALUE = 100.0
        const val INITIAL_TIME_VALUE = 0.0
        const val FINAL_TIME_VALUE = 120.0
        const val TIME_STEP_VALUE = 0.25
    }


    // 1. Create the model (with setup of: time boundaries & time step & integrationType type)
    val model = this   // inheritance: Model()
    // alternative:
    //val model = Model(INITIAL_TIME_VALUE, FINAL_TIME_VALUE, TIME_STEP_VALUE, EulerIntegration())


    init {

        // override default model properties:
        model.initialTime = INITIAL_TIME_VALUE
        model.finalTime = FINAL_TIME_VALUE
        model.timeStep = TIME_STEP_VALUE
        model.integrationType = EulerIntegration()
        model.name = "Simple Compound Interest Model"   // name is optional
        model.timeUnit = "month"        // timeUnit is optional

    }


    // 2. Create all system elements:

    // - 2a. Variables
    val INTEREST_RATE = model.constant(INTEREST_RATE_KEY)       // Using: constant() function
    val INITIAL_CAPITAL = model.constant(INITIAL_CAPITAL_KEY)   // Using: constant() function

    // - 2b. Stocks
    val Capital = model.stock(CAPITAL_KEY)                      // Using: stock() function

    // - 2c. Flows
    val interest = model.flow(INTEREST_KEY)                     // Using: flow() function

    // - 2e. Modules


    init {

        // - 2f. (Optional): Entities' descriptions
        INTEREST_RATE.description = "Annual flow rate in [%/year]"
        INITIAL_CAPITAL.description = "Initial capital in [EUR] in the beginning of the simulation."
        Capital.description = "Accumulated capital in [EUR] at specific point in time."
        interest.description = "Interest flow in [EUR / chosen unit of time], " +
                               "e.g. [EUR/month] due to paying flow on the Capital."

        // - 2g. (Optional): Entities' units
        INTEREST_RATE.unit = "%/year"
        INITIAL_CAPITAL.unit = "€"
        Capital.unit = "€"
        interest.unit = "€/month"


        // 3. Initial values:

        // - 3a. Stocks
        Capital.initialValue = { INITIAL_CAPITAL }      // Accepts: Double or ModelEntity



        // 4. Equations:

        // - 4a. Variables
        INTEREST_RATE.equation = { INTEREST_RATE_VALUE }
        INITIAL_CAPITAL.equation = { INITIAL_CAPITAL_VALUE }

        // - 4b. Stocks
        Capital.equation = { interest }     // Function type can be either Double or ModelEntity.

        // - 4c. Converters:
        interest.equation = { Capital * INTEREST_RATE }

    }

}
