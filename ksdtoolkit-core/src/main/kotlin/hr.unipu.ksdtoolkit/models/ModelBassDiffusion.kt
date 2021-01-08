package hr.unipu.ksdtoolkit.models

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.integration.EulerIntegration

/**
 * SD model of Bass Diffusion.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
open class ModelBassDiffusion : Model() {

    // Static properties:
    companion object {
        const val INITIAL_TARGET_MARKET_SIZE_KEY = "INITIAL_TARGET_MARKET_SIZE"
        const val INITIAL_CUSTOMER_BASE_SIZE_KEY = "INITIAL_CUSTOMER_BASE_SIZE"
        const val ADVERTISING_BUDGET_KEY = "ADVERTISING_BUDGET"
        const val PERSONS_REACHED_PER_EURO_KEY = "PERSONS_REACHED_PER_EURO"
        const val ADVERTISING_SUCCESS_RATE_KEY = "ADVERTISING_SUCCESS_RATE"
        const val WORD_OF_MOUTH_CONTACT_RATE_KEY = "WORD_OF_MOUTH_CONTACT_RATE"
        const val WORD_OF_MOUTH_SUCCESS_RATE_KEY = "WORD_OF_MOUTH_SUCCESS_RATE"

        const val INITIAL_TARGET_MARKET_SIZE_VALUE= 6e6     // [customer]
        const val INITIAL_CUSTOMER_BASE_SIZE_VALUE = 0.0    // [customer]
        const val ADVERTISING_BUDGET_VALUE = 10000.0        // [EUR/month]
        const val PERSONS_REACHED_PER_EURO_VALUE = 100.0    // [customer/EUR]
        const val ADVERTISING_SUCCESS_RATE_VALUE = 1.0      // [%]
        const val WORD_OF_MOUTH_CONTACT_RATE_VALUE = 1      // [1/month]
        const val WORD_OF_MOUTH_SUCCESS_RATE_VALUE = 10.0   // [%]

        const val INITIAL_TIME_VALUE = 0.0
        const val FINAL_TIME_VALUE = 60.0
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
        model.integrationType = EulerIntegration()
        model.name = "Bass Diffusion Model"   // name is optional



        // 2. Create all system elements:

        // - 2a. Variables
        val INITIAL_TARGET_MARKET_SIZE= model.constant(INITIAL_TARGET_MARKET_SIZE_KEY)
        val INITIAL_CUSTOMER_BASE_SIZE = model.constant(INITIAL_CUSTOMER_BASE_SIZE_KEY)
        val ADVERTISING_BUDGET = model.constant(ADVERTISING_BUDGET_KEY)
        val PERSONS_REACHED_PER_EURO = model.constant(PERSONS_REACHED_PER_EURO_KEY)
        val ADVERTISING_SUCCESS_RATE = model.constant(ADVERTISING_SUCCESS_RATE_KEY)
        val WORD_OF_MOUTH_CONTACT_RATE = model.constant(WORD_OF_MOUTH_CONTACT_RATE_KEY)
        val WORD_OF_MOUTH_SUCCESS_RATE = model.constant(WORD_OF_MOUTH_SUCCESS_RATE_KEY)

        val marketSaturationPct = model.converter("marketSaturationPct")
        val potentialCustomersReachedThroughAdvertising = model.converter("potentialCustomersReachedThroughAdvertising")
        val acquisitionThroughAdvertising = model.converter("acquisitionThroughAdvertising")
        val potentialCustomersReachedThroughWordOfMouth = model.converter("potentialCustomersReachedThroughWordOfMouth")
        val acquisitionThroughWordOfMouth = model.converter("acquisitionThroughWordOfMouth")

        // - 2b. Stocks
        val Customer_Base = model.stock("Customer_Base")
        val Advertising_Customers = model.stock("Advertising_Customers")
        val WordOfMouth_Customers = model.stock("WordOfMouth_Customers")

        // - 2c. Flows
        val customerAcquisition = model.flow("customerAcquisition")
        val advCustomerIn = model.flow("advCustomerIn")
        val womCustomerIn = model.flow("womCustomerIn")



        // 3. Initial values:

        // - 3a. Stocks

        Customer_Base.initialValue = { INITIAL_CUSTOMER_BASE_SIZE }
        Advertising_Customers.initialValue = { 0.0 }
        WordOfMouth_Customers.initialValue = { 0.0 }



        // 4. Equations:

        // - 4a. Variables
        INITIAL_TARGET_MARKET_SIZE.equation = { INITIAL_TARGET_MARKET_SIZE_VALUE }
        INITIAL_CUSTOMER_BASE_SIZE.equation = { INITIAL_CUSTOMER_BASE_SIZE_VALUE }
        ADVERTISING_BUDGET.equation = { ADVERTISING_BUDGET_VALUE }
        PERSONS_REACHED_PER_EURO.equation = { PERSONS_REACHED_PER_EURO_VALUE }
        ADVERTISING_SUCCESS_RATE.equation = { ADVERTISING_SUCCESS_RATE_VALUE }
        WORD_OF_MOUTH_CONTACT_RATE.equation = { WORD_OF_MOUTH_CONTACT_RATE_VALUE }
        WORD_OF_MOUTH_SUCCESS_RATE.equation = { WORD_OF_MOUTH_SUCCESS_RATE_VALUE }

        // - 4b. Stocks

        Customer_Base.equation = { customerAcquisition }
        Advertising_Customers.equation = { advCustomerIn }
        WordOfMouth_Customers.equation = { womCustomerIn }

        // - 4c. Flows
        customerAcquisition.equation = { advCustomerIn + womCustomerIn }
        advCustomerIn.equation = { acquisitionThroughAdvertising }
        womCustomerIn.equation = { acquisitionThroughWordOfMouth }

        // - 4d. Converters:

        marketSaturationPct.equation = { Customer_Base / INITIAL_TARGET_MARKET_SIZE * 100.0 }

        potentialCustomersReachedThroughAdvertising.equation =
            { PERSONS_REACHED_PER_EURO * ADVERTISING_BUDGET * (1.0 - marketSaturationPct / 100.0) }

        acquisitionThroughAdvertising.equation =
            { potentialCustomersReachedThroughAdvertising * ADVERTISING_SUCCESS_RATE / 100.0 }

        potentialCustomersReachedThroughWordOfMouth.equation =
            { WORD_OF_MOUTH_CONTACT_RATE * Customer_Base * (1 - marketSaturationPct / 100.0) }

        acquisitionThroughWordOfMouth.equation =
            { potentialCustomersReachedThroughWordOfMouth * WORD_OF_MOUTH_SUCCESS_RATE / 100.0 }

    }

}
