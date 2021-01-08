package hr.unipu.ksdtoolkit.integration

import hr.unipu.ksdtoolkit.entities.Stock

/**
 * Abstract class that describes an integrationType process.
 */
abstract class Integration {

    var dt: Double = 0.0
    var stocks: List<Stock> = arrayListOf()
    var converters: List<hr.unipu.ksdtoolkit.entities.Converter> = arrayListOf()

    /**
     * Abstract method for numeric integrationType that has to be implemented (by methods: Euler, Runge-Kutta, etc.)
     */
    abstract fun integrate()
}