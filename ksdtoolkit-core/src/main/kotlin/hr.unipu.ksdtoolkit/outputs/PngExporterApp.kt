package hr.unipu.ksdtoolkit.outputs

import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import hr.unipu.ksdtoolkit.simulations.Simulation
import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.slf4j.LoggerFactory

import javax.imageio.ImageIO
import java.io.File
import java.text.NumberFormat
import java.text.ParseException
import java.util.ArrayList
import java.util.Locale
import kotlin.time.measureTimedValue

/**
 * JavaFX app for chart plotting & exporting.
 */
class PngExporterApp : Application() {

    companion object {

        lateinit var model: Model
        lateinit var simulation: Simulation
        lateinit var pngExporterListener: ISimulationEventHandler

        var fileName: String = "outputs.png"   
        lateinit var series: ArrayList<Series<Number, Number>>
        private var width = 800.0
        private var height = 600.0

        /**
         * Add series names to the chart.
         *
         * @param modelEntityNames list of model entity names.
         */
        fun addSeries(modelEntityNames: List<String>) {
            series = ArrayList<Series<Number, Number>>()
            for (modelEntityName in modelEntityNames) {
                val s = Series<Number, Number>()     
                s.name = modelEntityName
                series.add(s)       
            }
        }

        /**
         * Add values to the chart series.
         *
         * @param modelEntityValues list of model entity values.
         * @param currentTime       current model time.
         */
        fun addValues(modelEntityValues: List<String>, currentTime: Double) {
            for (i in modelEntityValues.indices) {
                val valueString = modelEntityValues[i]
                val format = NumberFormat.getInstance(Locale.US)
                val number: Number
                try {
                    number = format.parse(valueString)
                    val value = number.toDouble()
                    series[i].data?.add(XYChart.Data(currentTime, value))
                } catch (exception: ParseException) {
                    LoggerFactory.getLogger(javaClass).error("Error with parsing model entity values.", exception)
                }

            }
        }

        /**
         * Set scene width and height.
         *
         * @param width  width.
         * @param height height.
         */
        fun setSize(width: Double, height: Double) {
            Companion.width = width
            Companion.height = height
        }

        /**
         * Save the chart as an image.
         *
         * @param scene Scene
         */
        private fun saveToFile(scene: Scene) {
            val image = scene.snapshot(null)
            val outputFile = File(fileName)
            val bImage = SwingFXUtils.fromFXImage(image, null)
            ImageIO.write(bImage, "png", outputFile)
            LoggerFactory.getLogger(javaClass).info("PNG file saved.")
        }
    }


    override fun start(stage: Stage) {

        LoggerFactory.getLogger(javaClass).info("PngExporterApp started.")

        val root = Group()

        val scene = Scene(root, width, height)

        val lineChart = createLineChart(model.name)
        lineChart.createSymbols = false
        lineChart.stylesheets.add("/Style2.css")
        lineChart.data.addAll(series)

        val borderPane = BorderPane()
        borderPane.prefHeightProperty().bind(scene.heightProperty())
        borderPane.prefWidthProperty().bind(scene.widthProperty())
        borderPane.center = lineChart

        root.children.add(borderPane)

        saveToFile(scene)

        stage.scene = scene
        stage.show()

        val doOutputHandlersContainOtherJavaFxApp = simulation.outputHandlers.any {
            it.javaClass.name == "hr.unipu.ksdtoolkit.outputs.WinSimulator"
        }
        if (doOutputHandlersContainOtherJavaFxApp) {
            simulation.outputHandlers.remove(pngExporterListener)
            Platform.setImplicitExit(true)
            Platform.runLater { WinSimulatorApp().start(Stage()) }
        }
        LoggerFactory.getLogger(javaClass).info("PngExporterApp exit.")

    }


    /**
     * Create LineChart.
     *
     * @param title line chart title
     * @return line chart
     */
    private fun createLineChart(title: String): LineChart<Number, Number> {

        val xAxis = NumberAxis()
        val yAxis = NumberAxis()
        xAxis.label = "Time [${model.timeUnit}]"
        yAxis.label = "Value []"

        val lineChart = LineChart(xAxis, yAxis)

        lineChart.animated = false

        setAxisBounds(lineChart,
            min = simulation.model.initialTime.toDouble(),
            max = simulation.model.finalTime.toDouble(),
            isXAxis = true
        )

        //lineChart.title = title
        return lineChart
    }


    /**
     * Setting axis bounds.
     */
    fun setAxisBounds(myChart: LineChart<Number, Number>, min: Double, max: Double, isXAxis: Boolean) {
        val axis: NumberAxis = if (isXAxis) {
            myChart.xAxis as NumberAxis
        } else {
            myChart.yAxis as NumberAxis
        }
        axis.isAutoRanging = false
        axis.lowerBound = min
        axis.upperBound = max
    }


}
