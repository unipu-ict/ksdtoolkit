package hr.unipu.ksdtoolkit.entities

import java.util.*
import kotlin.IllegalArgumentException

/**
 * Converter entity.
 *
 * Converter calculates a current value of a target [ModelEntity] in a time step, representing the cause-effect
 * relationship between [ModelEntity] instances. Converters takes input data and converts it into some outputs
 * signal. Converter calculations depends on the [equation] that has been delivered to the converter.
 * Details:
 * - Converters are used to disaggregate the complex functions that define flows into their constituent parts.
 * - Converters may be influenced by stocks and can influence other converters.
 * - In general, it converts [inputEntities] into an [targetEntity].
 * - E.g. Unlike flows (which are special kinds of converters), they cannot directly influence a stock.
 *
 * @param targetEntity Target (outputs) model entity (calculated value).
 * @param inputEntities Input model entities (that influence calculation).
 *
 */
class Converter(name: String) : ModelEntity(name) {

    private var inputEntities = arrayListOf<ModelEntity>()
    lateinit var targetEntity: ModelEntity


    constructor(targetEntity: ModelEntity,
                vararg inputEntities: ModelEntity
    ) : this(targetEntity.name) {

        Collections.addAll(this.inputEntities, *inputEntities)
        this.targetEntity = targetEntity

    }


    /**
     * Convert the target model entity to a value.
     */
    fun convert() {
        for (input in this.inputEntities) {
            if (!input.isCurrentValueCalculated && input.converter != null) {
                input.converter!!.convert()
            }
        }

        targetEntity.currentValue = _equation?.invoke() ?: 0.0
        targetEntity.isCurrentValueCalculated = true
    }


    /**
     * Add multiple input model entities.
     */
    fun addInputs(vararg inputs: ModelEntity) {
        for (input in inputs) {
            this.addInput(input)
        }
    }


    /**
     * Add an input for the target entity.
     */
    private fun addInput(input: ModelEntity) {
        if (!inputAlreadyAdded(input)) {
            this.inputEntities.add(input)
        } else {
            throw IllegalArgumentException("Duplicate variable exception.")
        }
    }


    /**
     * Determine if a ModelEntity has been already added to the Converter.
     */
    private fun inputAlreadyAdded(input: ModelEntity): Boolean {
        return inputEntities.contains(input)
    }

}