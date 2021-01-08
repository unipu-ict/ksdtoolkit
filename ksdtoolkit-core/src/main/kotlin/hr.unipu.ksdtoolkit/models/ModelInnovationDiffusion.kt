package hr.unipu.ksdtoolkit.models

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.entities.div
import hr.unipu.ksdtoolkit.entities.times
import hr.unipu.ksdtoolkit.integration.RungeKuttaIntegration

/**
 * SD model of Bass Diffusion.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

open class ModelInnovationDiffusion : Model() {

    // Static properties:
    companion object {
        const val TOTAL_POPULATION_KEY = "TOTAL_POPULATION"
        const val ADVERTISING_EFFECTIVENESS_KEY = "ADVERTISING_EFFECTIVENESS"
        const val CONTACT_RATE_KEY = "CONTACT_RATE"
        const val ADOPTION_FRACTION_KEY = "ADOPTION_FRACTION"

        const val TOTAL_POPULATION_VALUE = 10000.0          // [customer]
        const val ADVERTISING_EFFECTIVENESS_VALUE = 0.011   // [1/year]
        const val CONTACT_RATE_VALUE = 100.0                // [1/year]
        const val ADOPTION_FRACTION_VALUE = 0.015           // [ ]

        const val INITIAL_TIME_VALUE = 0.0
        const val FINAL_TIME_VALUE = 10.0
        const val TIME_STEP_VALUE = 0.25
    }


    init {

        // 1. Create the model (with setup of: time boundaries & time step & integrationType type)
        val model = this    // inheritance: Model()
                            // alternative: Model(INITIAL_TIME_VALUE, FINAL_TIME_VALUE, TIME_STEP_VALUE, EulerIntegration())

        // override default model properties:
        model.initialTime = INITIAL_TIME_VALUE
        model.finalTime = FINAL_TIME_VALUE
        model.timeStep = TIME_STEP_VALUE
        model.timeUnit = "year"        // optional

        //model.integrationType = EulerIntegration()
        model.integrationType = RungeKuttaIntegration()
        model.name = "Innovation/Product Diffusion Model"



        // 2. Create all system elements:

        // - 2a. Variables (Constants)
        val TOTAL_POPULATION = model.constant("TOTAL_POPULATION")
        val ADVERTISING_EFFECTIVENESS = model.constant("ADVERTISING_EFFECTIVENESS")
        val CONTACT_RATE = model.constant("CONTACT_RATE")
        val ADOPTION_FRACTION = model.constant("ADOPTION_FRACTION")

        // - 2b. Variables (Converters)
        val adoptionFromAdvertising = model.converter("adoptionFromAdvertising")
        val adoptionFromWordOfMouth = model.converter("adoptionFromWordOfMouth")

        // - 2c. Stocks
        val Potential_Adopters = model.stock("Potential_Adopters")
        val Adopters = model.stock("Adopters")

        // - 2d. Flows
        val adoptionRate = model.flow("adoptionRate")

        // - 2g. (Optional): Entities' units
        TOTAL_POPULATION.unit = "customer"
        ADVERTISING_EFFECTIVENESS.unit = "1/year"
        CONTACT_RATE.unit = "1/year"
        ADOPTION_FRACTION.unit = ""
        Potential_Adopters.unit = "customer"
        Adopters.unit = "customer"
        adoptionRate.unit = "customer/year"



        // 3. Initial values:

        // - 3a. Stocks
        Potential_Adopters.initialValue = { TOTAL_POPULATION }
        Adopters.initialValue = { 0.0 }



        // 4. Equations:

        // - 4a. Constants:
        TOTAL_POPULATION.equation = { TOTAL_POPULATION_VALUE }
        ADVERTISING_EFFECTIVENESS.equation = { ADVERTISING_EFFECTIVENESS_VALUE }
        CONTACT_RATE.equation = { CONTACT_RATE_VALUE }
        ADOPTION_FRACTION.equation = { ADOPTION_FRACTION_VALUE }

        // - 4b. Converters:
        adoptionFromAdvertising.equation = { Potential_Adopters * ADVERTISING_EFFECTIVENESS }
        adoptionFromWordOfMouth.equation = { CONTACT_RATE * ADOPTION_FRACTION *
                Potential_Adopters * Adopters / TOTAL_POPULATION }

        // - 4c. Stocks
        Potential_Adopters.equation = { - adoptionRate }
        Adopters.equation = { adoptionRate }

        // - 4d. Flows
        adoptionRate.equation = { adoptionFromAdvertising + adoptionFromWordOfMouth }

    }

}
