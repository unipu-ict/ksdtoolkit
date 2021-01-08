package hr.unipu.ksdtoolkit.models

import hr.unipu.ksdtoolkit.integration.RungeKuttaIntegration

/**
 * Inheritance of Simple Compound Interest Model.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */

class ModelInheritedCompoundInterest : ModelSimpleCompoundInterest() {

    // Static properties:
    companion object {
        const val INTEREST_RATE_KEY = "INTEREST_RATE"
        const val INITIAL_CAPITAL_KEY = "INITIAL_CAPITAL"
        const val INTEREST_KEY = "interest"
        const val CAPITAL_KEY = "Capital"

        const val INTEREST_RATE_VALUE = -0.1 / 12
        const val INITIAL_CAPITAL_VALUE = 200.0
        const val INITIAL_TIME_VALUE = 0.0
        const val FINAL_TIME_VALUE = 240.0
        const val TIME_STEP_VALUE = 0.25
    }


    // 1. Create the model (with the parameters)
    val modelInherited = this    // inheritance: ModelSimpleCompoundInterest()
    //val modelInherited = object : ModelSimpleCompoundInterest() {}


    init {

        // overriding default model properties:
        modelInherited.initialTime = INITIAL_TIME_VALUE
        modelInherited.finalTime = FINAL_TIME_VALUE
        modelInherited.timeStep = TIME_STEP_VALUE
        modelInherited.integrationType = RungeKuttaIntegration()
        modelInherited.name = "Inherited Compound Interest Model"

        // changing inherited model constants
        modelInherited.entities[INITIAL_CAPITAL_KEY]?.equation = { INITIAL_CAPITAL_VALUE }
        modelInherited.entities[INTEREST_RATE_KEY]?.equation = { INTEREST_RATE_VALUE }

        modelInherited.INITIAL_CAPITAL.equation = { 200.0 }
        modelInherited.INTEREST_RATE.equation = { -0.1 / 12 }
    }

    val ModuleSimpleCompound1 = modelInherited.createModule(
        "ModuleSimpleCompound1",
        "hr.unipu.ksdtoolkit.models.ModelSimpleCompoundInterest"
    ) as ModelSimpleCompoundInterest

    val ModuleSimpleCompound2 = object : ModelSimpleCompoundInterest() {}

    init {
        modelInherited.modules["ModuleSimpleCompound1"]?.entities!!["INITIAl_CAPITAL"]?.equation = { 1000.0 }

        modelInherited.ModuleSimpleCompound1.INITIAL_CAPITAL.equation = { 1000.0 }

        modelInherited.ModuleSimpleCompound2.INITIAL_CAPITAL.equation = { 1000.0 }
    }

    val some_converter_in_parent = modelInherited.converter("some_converter_in_parent")

    init {
        some_converter_in_parent.equation = { modelInherited.ModuleSimpleCompound1.INITIAL_CAPITAL }
    }

}