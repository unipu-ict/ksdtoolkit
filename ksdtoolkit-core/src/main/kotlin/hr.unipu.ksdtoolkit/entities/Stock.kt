package hr.unipu.ksdtoolkit.entities

/**
 * Stock entity.
 *
 * Stock has one or more "input" and "outputs" [Flow] instances, integrates (accumulates) their difference or "change
 * rate", and represents a "memory"  of a current state of the dynamics system.
 * Stocks can be only influenced by flows.
 *
 * @property name Stock name (only passed to the abstract class ModelEntity).
 */
class Stock(name: String) : ModelEntity(name) {

    val _initialValue: (()->Double?)?
        get() {
            val invocationResult = initialValue?.invoke()
            return when(invocationResult) {
                is Double -> {
                    { invocationResult as Double? }
                }
                is ModelEntity -> {
                    { invocationResult.equation?.invoke().toString().toDouble() }
                }
                is Int -> {
                    { invocationResult.toDouble() }
                }
                else -> null
            }
        }
    var initialValue: (()->Any?)? = null


    init {
        this.isCurrentValueCalculated = true
    }

}