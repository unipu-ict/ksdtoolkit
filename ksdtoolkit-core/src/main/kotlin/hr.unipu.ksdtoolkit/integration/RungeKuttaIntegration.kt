package hr.unipu.ksdtoolkit.integration

/**
 * Runge-Kutta method for numeric integrationType implementation.
 *
 * The class is extends [Integration] abstract class.
 */
class RungeKuttaIntegration : Integration() {

    override fun integrate() {
        val k = Array(this.stocks.size) { DoubleArray(4) }
        for (i in 0..3) {
            var j = 0
            for (stock in this.stocks) {
                k[j][i] = (stock._equation?.invoke() ?: 0.0) * this.dt
                if (i < 2)
                    stock.currentValue = stock.previousValue + k[j][i] / 2
                else if (i == 2)
                    stock.currentValue = stock.previousValue + k[j][i]
                else {
                    val calculatedValue = stock.previousValue + k[j][0] / 6 + k[j][1] / 3 + k[j][2] / 3 + k[j][3] / 6
                    stock.currentValue = calculatedValue
                }
                stock.isCurrentValueCalculated = true
                j++
            }
        }
    }

}