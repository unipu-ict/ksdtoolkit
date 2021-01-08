package hr.unipu.ksdtoolkit.integration

/**
 * Euler method for numeric integrationType implementation.
 *
 * The class is extends [Integration] abstract class.
 */
class EulerIntegration : Integration() {

    override fun integrate() {
        for(stock in stocks) {
            val calculatedValue = stock.currentValue + ( stock._equation?.invoke() ?: 0.0 ) * dt
            stock.currentValue = calculatedValue
            stock.isCurrentValueCalculated = true
        }
    }

}