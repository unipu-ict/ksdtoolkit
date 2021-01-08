package hr.unipu.ksdtoolkit.entities

/**
 * Abstract class that represents SD entities: [Stock], [Flow] or [Constant].
 *
 * @property name Entity name.
 * @property currentValue Entity current value (during integrationType). Default 0.0.
 * @property previousValue Entity previous value (in previous step of integrationType). Default 0.0.
 * @property equation Entity equation (for integrationType). Default null.
 * @property isCurrentValueCalculated Flag: 'true' if the current value has already been calculated in integrationType
 * process, and (default) 'false' otherwise.
 * @property converter Entity converter (for converting one entity to another): default is 'null'.
 */

abstract class ModelEntity(
    /**
     * Primary constructor.
     */
    var name: String,
    var description: String = "",
    var unit: String = "",
    var currentValue: Double = 0.0,
    var previousValue: Double = 0.0,
    var isCurrentValueCalculated: Boolean = false,
    var converter: Converter? = null

) {

    val _equation: (()->Double?)?
        get() {
            val invocationResult = equation?.invoke()
            return when(invocationResult) {
                is Double -> {
                    this.isCurrentValueCalculated = true

                    { invocationResult as Double? }
                }
                is ModelEntity -> {

                    if (invocationResult.isCurrentValueCalculated == false) {
                        this.isCurrentValueCalculated = false
                    } else {
                        this.isCurrentValueCalculated = true
                    }
                    { (invocationResult as ModelEntity).currentValue }
                }
                is Int -> {
                    this.isCurrentValueCalculated = true
                    { invocationResult.toDouble() }
                }
                else -> {
                    { null }
                }
            }
        }
    var equation: (()->Any?)? = null


    /**
     * Overridden methods.
     */
    override fun toString(): String {
        return "ModelEntity[ name=${this.name}, value=${this.currentValue}, previousValue=${this.previousValue} ]"
    }


    /**
     * Operator overloading: times.
     */
    operator fun times(other: Any): Double {
        return when (other) {
            is ModelEntity -> this.currentValue * other.currentValue
            is Double -> this.currentValue * other
            is Int -> this.currentValue * (other.toDouble())
            else -> 0.0
        }
    }


    /**
     * Operator overloading: plus.
     */
    operator fun plus(other: Any): Double {
        return when(other) {
            is ModelEntity -> this.currentValue + other.currentValue
            is Double -> this.currentValue + other
            is Int -> this.currentValue + (other.toDouble())
            else -> 0.0
        }
    }


    /**
     * Operator overloading: minus.
     */
    operator fun minus(other: Any): Double {
        return when(other) {
            is ModelEntity -> this.currentValue - other.currentValue
            is Double -> this.currentValue - other
            is Int -> this.currentValue - (other.toDouble())
            else -> 0.0
        }
    }


    /**
     * Operator overloading: div.
     */
    operator fun div(other: Any): Double {
        return when(other) {
            is ModelEntity -> if (other != 0) this.currentValue / other.currentValue else 0.0
            is Double -> if (other != 0) this.currentValue / other else 0.0
            is Int -> this.currentValue / (other.toDouble())
            else -> 0.0
        }
    }


    /**
     * Operator overloading: unaryMinus.
     */
    operator fun unaryMinus(): Double {
        return this.currentValue * (-1.0)
    }

}


/**
 * For commutativity - extension function on Double: div.
 */
operator fun Double.div(other: Any): Double {
    return when (other) {
        is ModelEntity -> if (other != 0) this / other.currentValue else 0.0
        is Double -> if (other != 0) this / other else 0.0
        is Int -> this / (other.toDouble())
        else -> 0.0
    }
}


/**
 * For commutativity - extension function on Double: times.
 */
operator fun Double.times(other: Any): Double {
    return when (other) {
        is ModelEntity -> this * other.currentValue
        is Double -> this * other
        is Int -> this * (other.toDouble())
        else -> 0.0
    }
}