package hr.unipu.ksdtoolkit.outputs

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import hr.unipu.ksdtoolkit.entities.ModelEntity
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import hr.unipu.ksdtoolkit.simulations.Simulation
import javafx.application.Platform
import javafx.event.Event
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import org.slf4j.LoggerFactory

import javax.imageio.ImageIO
import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.text.ParseException
import java.util.ArrayList
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger

/**
 * JavaFX app for chart plotting and interactive simulation.
 */
class WinSimulatorApp : Application() {

    companion object {

        private lateinit var scene: Scene
        private lateinit var series: ArrayList<Series<Number, Number>>
        private lateinit var seriesConstants: ArrayList<Series<Number, Number>>
        private lateinit var checkBoxes: ArrayList<CheckBox>
        private lateinit var textFields: ArrayList<VBox>
        private lateinit var lineChart: LineChart<Number, Number>
        private var width = 800.0
        private var height = 600.0


        lateinit var simulation: Simulation
        lateinit var winSimulatorListener: ISimulationEventHandler

        /**
         * Add names of entities to names of series.
         *
         * @param modelEntityNames list of model entity names.
         */
        fun addSeriesNames(modelEntityNames: List<String>) {
            series = ArrayList()
            for (modelEntityName in modelEntityNames) {
                val s = Series<Number, Number>()
                s.name = modelEntityName
                series.add(s)
            }
        }

        /**
         * Add constants for text fields.
         *
         * @param modelEntityConstants list of model entity (constants) names.
         */
        fun addSeriesConstants(modelEntityConstants: Map<String, ModelEntity>) {
            seriesConstants = ArrayList()
            for (modelEntityConstant in modelEntityConstants) {
                val s = Series<Number, Number>()
                s.name = modelEntityConstant.key
                val value = modelEntityConstant.value.currentValue
                s.data.add(XYChart.Data(0.0, value))
                seriesConstants.add(s)
            }
        }

        /**
         * Add values to chart series.
         *
         * @param modelEntityValues list of model entity values.
         * @param currentTime       current model time.
         */
        fun addSeriesValues(modelEntityValues: List<String>, currentTime: Double) {
            for (i in modelEntityValues.indices) {
                val valueString = modelEntityValues[i]
                val format = NumberFormat.getInstance(Locale.US)
                val number: Number
                try {
                    number = format.parse(valueString)
                    val value = number.toDouble()
                    series[i].data?.add(XYChart.Data(currentTime, value))
                } catch (ex: ParseException) {
                    Logger.getLogger(WinSimulatorApp::class.java.getName()).log(Level.SEVERE, null, ex)
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
            val outputFile = File("chart.png")
            val bImage = SwingFXUtils.fromFXImage(image, null)
            try {
                ImageIO.write(bImage, "png", outputFile)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
    }

    override fun start(stage: Stage) {

        LoggerFactory.getLogger(javaClass).info("WinSimulatorApp started.")

        stage.title = "WinSimulator"
        val root = BorderPane()

        scene = Scene(root, width, height)

        lineChart = this.createLineChart("")
        lineChart.data.addAll(series)
        lineChart.cursor = Cursor.CROSSHAIR
        lineChart.animated = false
        addTooltips()
        addLineChartContextMenu()

        checkBoxes = ArrayList()
        for (s in series) {
            val cb = CheckBox(s.name)
            cb.onAction = EventHandler<ActionEvent> { this.updateLineChart(it) }
            cb.isSelected = true
            checkBoxes.add(cb)
        }

        textFields = ArrayList()
        for (s in seriesConstants) {

            val tf1 = Label(s.name)
            val tf2 = TextField(s.data[0].yValue.toString())
            val tf3 = VBox(tf1, tf2)

            tf2.onAction = EventHandler<ActionEvent> { this.reRunSimulation(it) }

            tf2.onKeyPressed = EventHandler<KeyEvent> {
                if (it.code == KeyCode.TAB) {
                    this.reRunSimulation(it)
                }
            }

            textFields.add(tf3)
        }


        val dataLabel1 = Label("Constants:")
        val vbox1 = VBox(8.0)
        vbox1.children.addAll(dataLabel1)
        vbox1.children.addAll(textFields)
        vbox1.padding = Insets(20.0, 10.0, 20.0, 10.0)

        val dataLabel2 = Label("Entities:")
        val vbox2 = VBox(8.0)
        vbox2.children.addAll(dataLabel2)
        vbox2.children.addAll(checkBoxes)
        vbox2.padding = Insets(20.0, 10.0, 20.0, 0.0)

        root.left = vbox1
        root.center = lineChart
        root.right = vbox2

        stage.scene = scene
        stage.show()

        val doOutputHandlersContainOtherJavaFxApp =
            simulation.outputHandlers.any { it.javaClass.name == "hr.unipu.ksdtoolkit.outputs.PngExporter" }
        if (doOutputHandlersContainOtherJavaFxApp) {
            simulation.outputHandlers.remove(winSimulatorListener)

            Platform.setImplicitExit(true)

            Platform.runLater { PngExporterApp().start(Stage()) }

        }

    }


    /**
     * Re-run simulation (with new value of model constant).
     *
     * @param event Event
     */
    private fun reRunSimulation(event: Event) {

        val tf = event.source as TextField
        val text = tf.text
        val label = (tf.parent.childrenUnmodifiable[0] as Label).text

        val isModuleEntity = label.contains('.')
        val moduleName = label.substringBefore(".")
        val entityName = label.substringAfter(".")

        val oldValue =
            if (isModuleEntity) {
                simulation.model.modules[moduleName]?.entities?.get(entityName)?.currentValue
            } else {
                simulation.model.entities[label]?.currentValue
            }

        val newValue = try {
                text.toDouble()
            } catch (exception: Exception) {
                LoggerFactory.getLogger(javaClass).error("WinSimulatorApp: incorrect input.", exception)
                tf.text = oldValue.toString()
                oldValue
            }

        if (isModuleEntity) {
            simulation.model.modules[moduleName]?.entities?.get(entityName)?.equation = { newValue }
        } else {
            simulation.model.entities[label]?.equation = { newValue }
        }

        simulation.run()

        refreshChart()

    }


    /**
     * Add/remove lines from chart.
     *
     * @param event Event
     */
    private fun updateLineChart(event: Event) {
        val checkBox = event.source as CheckBox
        val text = checkBox.text
        for (s in series) {
            if (text == s.name) {
                if (checkBox.isSelected) {
                    lineChart.data.add(s)
                } else {
                    lineChart.data.remove(s)
                }
            }
        }
    }


    /**
     * Refresh whole chart.
     */
    private fun refreshChart() {
        lineChart.data?.clear()
        for (s in series) {
            for (cb in checkBoxes) {
                val textCheckBox = cb.text
                if (textCheckBox == s.name) {
                    if (cb.isSelected) {
                        lineChart.data.add(s)
                    } else {
                        lineChart.data.remove(s)
                    }
                }
            }
        }
    }


    /**
     * Create a LineChart
     *
     * @param title line chart title
     * @return line chart
     */
    private fun createLineChart(title: String): LineChart<Number, Number> {

        val xAxis = NumberAxis()
        val yAxis = NumberAxis()
        xAxis.label = "Time"
        yAxis.label = "Value"

        val lineChart = LineChart(xAxis, yAxis)
        lineChart.title = title

        return lineChart
    }


    /**
     * Add a context menu to line chart.
     */
    private fun addLineChartContextMenu() {
        val saveAsFile = MenuItem("Save as file.")
        saveAsFile.setOnAction { event -> saveToFile(scene) }
        val menu = ContextMenu(saveAsFile)
        lineChart.setOnMouseClicked { event ->
            if (MouseButton.SECONDARY == event.button) {
                menu.show(lineChart, event.screenX, event.screenY)
            }
        }
    }


    /**
     * Add tooltips to chart.
     */
    private fun addTooltips() {

        for (i in 0 until lineChart.data.size) {
            for (j in 0 until lineChart.data[i].data.size) {
                val dot = lineChart.data[i].data[j]
                Tooltip.install(dot.node, Tooltip("Time  = ${dot.xValue}\n" +
                                                       "Value = ${dot.yValue}"))

                dot.node.setOnMouseEntered { event -> dot.node.styleClass.add("onHover") }
                dot.node.setOnMouseExited { event -> dot.node.styleClass.remove("onHover") }
            }
        }
    }


}
