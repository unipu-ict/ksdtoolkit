package hr.unipu.websimulatorapp.webapp


import com.vaadin.addon.charts.Chart
import com.vaadin.addon.charts.model.*
import com.vaadin.addon.spreadsheet.Spreadsheet
import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.annotations.Theme
import com.vaadin.annotations.VaadinServletConfiguration
import com.vaadin.external.org.slf4j.LoggerFactory
import com.vaadin.server.*
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.*
import com.vaadin.ui.Label
import com.vaadin.ui.themes.ValoTheme
import hr.unipu.ksdtoolkit.entities.ModelEntity
import hr.unipu.ksdtoolkit.models.*
import hr.unipu.ksdtoolkit.simulations.Simulation
import hr.unipu.websimulatorapp.WebSimulator
import org.apache.poi.ss.usermodel.Cell
import java.text.ParseException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet

/**
 * Vaadin web simulator app - for chart plotting and interactive simulation.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */


@Theme("mytheme") 
@PreserveOnRefresh 
class WebSimulatorApp : UI() {

    init {

        LoggerFactory.getLogger(javaClass).info("---WebSimulatorApp init invoked.---")

        /**---Creating: model, simulation and run.---**/

        // 1. Create a model.
        val model = ModelInnovationDiffusion()
        //val model = ModelGenericSD()
        //val model = ModelSimpleCompoundInterest()
        //val model = ModelInheritedCompoundInterest()
        //val model = ModelBassDiffusion()
        //val model = ModelTestSpeed()

        // 2. Create the simulation.
        simulation = Simulation(model)

        // 3. Add results handlers
        simulation.addSimulationEventListener(WebSimulator())

        // 4. Run simulation (so that all expressions are invoked for time=0).
        simulation.run()

    }


    /**
     * Class member variables.
     */
    private var timeUnit = simulation.model.timeUnit


    /**
     * Static variables and methods.
     */
    companion object {

        private val LAYOUT_WIDTH_COLUMN1 = "450px"
        private val LAYOUT_WIDTH_COLUMN2 = "450px"
        private val LAYOUT_WIDTH_COLUMN3 = "600px"
        private val CHART_WIDTH: String = "450px"
        private val CHART_HEIGHT: String = "350px"
        private val DATA_IN_ROWS = true

        private var seriesNames: ArrayList<String> = ArrayList()
        private var seriesUnits: ArrayList<String> = ArrayList()
        private var seriesValues: ArrayList<ListSeries> = ArrayList()
        private var time: ArrayList<Double> = ArrayList()
        private var constantsName: ArrayList<String> = ArrayList()
        private var constantsValue: ArrayList<DataSeriesItem> = ArrayList()

        public lateinit var simulation: Simulation
        public var simulationFinished: Boolean = false


        /**
         * Add series names to web simulator (for chart series).
         *
         * @param modelEntityNames list of model entity names.
         */
        fun addSeriesNames(modelEntityNames: List<String>) {

            LoggerFactory.getLogger(javaClass).info("---SeriesNames added.---")

            seriesNames = ArrayList()
            seriesValues = ArrayList()
            seriesUnits = ArrayList()
            time = ArrayList()
            val modelEntityUnits = simulation.model.modelEntitiesUnits
            for (modelEntityName in modelEntityNames) {
                val index = modelEntityNames.indexOf(modelEntityName)
                val modelEntityUnit = modelEntityUnits[index]
                seriesNames.add(modelEntityName)
                seriesUnits.add(modelEntityUnit)
                val series = ListSeries()
                series.name = modelEntityName
                seriesValues.add(series)
            }
        }


        /**
         * Add constants to web simulator.
         *
         * @param modelEntityConstants list of model constants.
         */
        fun addConstants(modelEntityConstants: Map<String, ModelEntity>) {

            constantsName = ArrayList()
            constantsValue = ArrayList()

            for (modelEntityConstant in modelEntityConstants) {
                constantsName.add(modelEntityConstant.key)
                val value = modelEntityConstant.value.currentValue
                val item = DataSeriesItem(0, value)
                constantsValue.add(item)
            }
        }


        /**
         * Add series values to web simulator (for chart series).
         *
         * @param modelEntityValues list of myModel entity values.
         * @param currentTime       current myModel time.
         */
        fun addSeriesValues(modelEntityValues: List<String>, currentTime: Double) {
            time.add(currentTime)
            for (i in 0..modelEntityValues.indices.last) {
                val valueString = modelEntityValues[i]
                try {
                    val value = valueString.toDouble()
                    seriesValues[i].addData(value)
                } catch (exception: ParseException) {
                    Logger.getLogger(WebSimulatorApp::class.java.name).log(
                        Level.SEVERE, "Parsing entity values from model unsuccessful.", exception)
                }

            }
        }
    }


    /**
     * Vaadin Servlet.
     */
    @WebServlet(urlPatterns = ["/*"], name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WebSimulatorApp::class, productionMode = false)
    class MyUIServlet : VaadinServlet()


    /**
     * Vaadin init() method - entry point in the web application.
     */
    override fun init(vaadinRequest: VaadinRequest) {

        LoggerFactory.getLogger(javaClass).info("---Vaadin 'init' method invoked.---")

        runSimulation()

        drawUI()

    }


    /**
     * Draw UI - for drawing and re-drawing.
     */
    private fun drawUI() {

        LoggerFactory.getLogger(javaClass).info("---'draw' method invoked.---")

        val contentVertical = VerticalLayout()
        val contentHorizontal = HorizontalLayout()
        val layoutGrid = GridLayout(3, 2)

        val header = Label("WebSimulator: ${simulation.model.name}")
        header.styleName = ValoTheme.LABEL_H2
        contentVertical.addComponent(header)
        contentVertical.setComponentAlignment(header, Alignment.TOP_CENTER)
        contentVertical.addComponent(layoutGrid)
        contentHorizontal.addComponent(contentVertical)
        content = contentHorizontal


        val layoutInput = createInputSection()
        layoutGrid.addComponent(layoutInput, 0, 0)

        val layoutModel = createBackendSection()
        layoutGrid.addComponent(layoutModel, 1, 0)

        val layoutOutput1 = createChartSection()
        layoutGrid.addComponent(layoutOutput1, 2, 0)

        val layoutControl = createControlSection()
        layoutGrid.addComponent(layoutControl, 0, 1)

        var layoutOutput2 = VerticalLayout()

        if (false) layoutOutput2 = createGridSection()
        if (true) layoutOutput2 = createSpreadsheetSection()

        layoutGrid.addComponent(layoutOutput2, 2, 1)
    }


    private fun runSimulation() {

        if (WebSimulatorApp.simulationFinished == false) {
            val doesNotContainWebSimulatorListener = simulation.outputHandlers.filterIsInstance(WebSimulator::class.java).isEmpty()
            if (doesNotContainWebSimulatorListener) {

                simulation.addSimulationEventListener(WebSimulator())
            }
            LoggerFactory.getLogger(javaClass).info("---Simulation (re)-run()---")
            simulation.run()
        }
    }


    /**
     * Re-run simulation & refresh UI.
     */
    private fun refreshUI() {
        WebSimulatorApp.simulationFinished = false
        runSimulation()
        drawUI()
    }


    /**
     * Create Input section.
     */
    private fun createInputSection(): VerticalLayout {
        val layoutInput = VerticalLayout()
        layoutInput.setWidth(LAYOUT_WIDTH_COLUMN1)

        val lblInput = Label("INPUT (parameters):")
        lblInput.setHeight("24px")
        lblInput.styleName = ValoTheme.LABEL_COLORED
        layoutInput.addComponent(lblInput)

        for (s in WebSimulatorApp.constantsName) {

            val labelTextField = CustomLabelTextField()
            labelTextField.caption = s
            val indexOfEntity = simulation.model.modelEntitiesKeys.indexOf(s)
            labelTextField.value = simulation.model.modelEntitiesValues[indexOfEntity]
            labelTextField.modelVariableName = simulation.model.modelEntitiesKeys[indexOfEntity]
            labelTextField.textField.valueChangeMode =
                ValueChangeMode.LAZY
            labelTextField.textField.valueChangeTimeout = 1600
            labelTextField.textField.addValueChangeListener { valueChangeEvent ->

                LoggerFactory.getLogger(javaClass).info("Value changed in: $valueChangeEvent")

                val newValue = valueChangeEvent.value.toDouble()
                Notification.show("Value changed:", newValue.toString(), Notification.Type.TRAY_NOTIFICATION);

                val modelVariableName = labelTextField.modelVariableName
                simulation.model.entities[modelVariableName]?.equation = { newValue }
                refreshUI()
            }
            layoutInput.addComponent(labelTextField)
        }

        return layoutInput
    }


    /**
     * Create Control section.
     */
    private fun createControlSection(): VerticalLayout {
        val layoutControl = VerticalLayout()

        val lblControl = Label("CONTROL (simulation):")
        lblControl.setHeight("24px")
        lblControl.styleName = ValoTheme.LABEL_COLORED

        val buttonControl = Button("Simulation Settings")
        val window = CustomWindowSettings("Simulation Settings")
        buttonControl.addClickListener { clickEvent ->
            this.ui.ui.addWindow(window)
        }
        window.addCloseListener {
            refreshUI()
        }

        val buttonRun = Button("Run Simulation")
        buttonRun.addClickListener { e ->
            refreshUI()
        }
        layoutControl.addComponent(lblControl)
        layoutControl.addComponent(buttonControl)
        layoutControl.addComponents(buttonRun)
        return layoutControl
    }


    /**
     * Create image of the SD model.
     */
    private fun createBackendSection(): VerticalLayout {
        val layoutModel = VerticalLayout()
        layoutModel.setWidth(LAYOUT_WIDTH_COLUMN2)

        val lblModel = Label("MODEL (backend):")
        lblModel.setHeight("24px")
        lblModel.styleName = ValoTheme.LABEL_COLORED

        val imageFileName0 = "/" + "NoModelImage.png"
        val imageFileName1 = "/" + simulation.model.javaClass.name.substringAfterLast(".") + ".png"
        val imageFileName2 = "/" + simulation.model.javaClass.name.substringAfterLast(".") + ".jpg"
        val sc = VaadinServlet.getCurrent().servletContext
        val path0 = sc.getRealPath(imageFileName0)
        val path1 = sc.getRealPath(imageFileName1)
        val path2 = sc.getRealPath(imageFileName2)

        val loader: ClassLoader = this::class.java.getClassLoader()
        val url0 = loader.getResource(imageFileName0)
        val url1 = loader.getResource(imageFileName1)
        val url2 = loader.getResource(imageFileName2)

        val path = if (url1 != null) {
            path1
        } else {
            if (url2 != null) {
                path2
            } else {
                path0
            }
        }

        val resource = ClassResource("/" + path.substringAfterLast("""\"""))
        val image = Image(null, resource)

        image.setSizeFull()

        layoutModel.addComponent(lblModel)
        layoutModel.addComponent(image)

        return layoutModel
    }


    /**
     * Create output Chart section.
     */
    private fun createChartSection(): VerticalLayout {
        val layoutOutput1 = VerticalLayout()
        layoutOutput1.setWidth(LAYOUT_WIDTH_COLUMN3)

        val lblOutput1 = Label("OUTPUT (graph):")
        lblOutput1.setHeight("24px")
        lblOutput1.styleName = ValoTheme.LABEL_COLORED

        val chart = Chart()

        val configuration = chart.configuration
        configuration.chart.type = ChartType.LINE
        configuration.title.text = ""
        configuration.subTitle.text = ""

        val yAxis = configuration.getyAxis()
        yAxis.setTitle("Value")

        val xAxis = configuration.getxAxis()
        xAxis.setTitle("Time [$timeUnit]")

        val legend = configuration.legend
        legend.layout = LayoutDirection.VERTICAL
        legend.align = HorizontalAlign.RIGHT
        legend.verticalAlign = VerticalAlign.TOP
        legend.borderWidth = 0

        val startTime = time[0]
        val endTime = time[time.size - 1]
        val intervalTime = time[1] - time[0]
        val plotOptions = PlotOptionsLine()
        plotOptions.pointStart = startTime
        plotOptions.pointInterval = intervalTime
        configuration.setPlotOptions(plotOptions)

        if (seriesNames.maxOf { it.length } > 25) {
            legend.enabled = false
        }

        for (i in 0 until Companion.seriesValues.size) {
            configuration.addSeries(Companion.seriesValues[i])
        }
        chart.drawChart(configuration)

        layoutOutput1.addComponent(lblOutput1)
        layoutOutput1.addComponent(chart)

        return layoutOutput1
    }


    /**
     * Create output Grid section.
     */
    private fun createGridSection(): VerticalLayout {

        val layoutOutput2 = VerticalLayout()

        val lblOutput2 = Label("OUTPUT (grid):")
        lblOutput2.setHeight("24px")
        lblOutput2.styleName = ValoTheme.LABEL_COLORED

        val dataRowsSize = Companion.seriesValues.size

        val rowNames = mutableListOf("Time" + " [${simulation.model.timeUnit}]")
        for (index in 0 until dataRowsSize) {
            rowNames.add(seriesValues[index].name + " [${seriesUnits[index]}]")
        }

        val rows = ArrayList<LinkedHashMap<String, String>>(rowNames.size * Companion.seriesValues[0].data.size)
        var series1: Array<Number>
        var series2: ArrayList<Double>
        for (i in 0..time.indices.last) {
            val fakeBean = LinkedHashMap<String, String>()
            for (j in 0..rowNames.indices.last) {
                if (j > 0) {
                    series1 = (Companion.seriesValues[j - 1].data)
                    fakeBean[rowNames[j]] = series1[i].toString()
                } else {
                    series2 = time
                    fakeBean[rowNames[j]] = series2[i].toString()
                }

            }
            rows.add(fakeBean)
        }

        val grid = Grid<LinkedHashMap<String, String>>()
        grid.setItems(rows)
        grid.setSizeFull()

        val s = rows[0]
        for (entry in s.entries) {
            grid.addColumn<String> { h -> h[entry.key] }.caption = entry.key
        }
        grid.styleName = "smallgrid"

        layoutOutput2.addComponent(lblOutput2)
        layoutOutput2.addComponent(grid)

        return layoutOutput2
    }


    /**
     * Create output Spreadsheet section.
     */
    private fun createSpreadsheetSection(): VerticalLayout {
        val layoutOutput2 = VerticalLayout()

        val lblOutput2 = Label("OUTPUT (spreadsheet):")
        lblOutput2.setHeight("24px")
        lblOutput2.styleName = ValoTheme.LABEL_COLORED

        val dataColumnsSize = Companion.seriesValues[2].data.size
        val dataRowsSize = Companion.seriesValues.size

        val sheet = Spreadsheet()
        if (DATA_IN_ROWS == true) {
            sheet.createNewSheet("Output", dataRowsSize + 1, dataColumnsSize + 1)
        } else {
            sheet.createNewSheet("Output", dataColumnsSize + 1, dataRowsSize + 1)
        }

        sheet.isReportStyle = true
        sheet.setWidth(LAYOUT_WIDTH_COLUMN3)

        val newCellStyle = sheet.workbook.createCellStyle()
        val format = sheet.workbook.createDataFormat()

        newCellStyle.dataFormat = format.getFormat("#,##0.00")

        val rowNames = mutableListOf("Time" + " [${simulation.model.timeUnit}]")
        for (index in 0 until dataRowsSize) {
            rowNames.add(seriesValues[index].name + " [${seriesUnits[index]}]")
        }

        for (rowName in rowNames) {
            if (DATA_IN_ROWS) {
                sheet.createCell(rowNames.indexOf(rowName), 0, rowName)
                sheet.autofitColumn(rowNames.indexOf(rowName))
            } else {
                sheet.createCell(0, rowNames.indexOf(rowName), rowName)
                sheet.autofitColumn(rowNames.indexOf(rowName))
            }

        }


        /*
        run {
            var i = 0
            while (i < dataLength) {
                val point = Companion.seriesValue[2].data[i]
                Companion.seriesValue[2].updatePoint(i, point as Double * 12.0 * 100.0)
                i = i + 1
            }
        }
        */

        val rows = ArrayList<LinkedHashMap<String, String>>(rowNames.size * Companion.seriesValues[0].data.size)
        var series1: Array<Number>
        var series2: ArrayList<Double>
        for (i in 0..time.indices.last) {
            val fakeBean = LinkedHashMap<String, String>()
            for (j in 0..rowNames.indices.last) {
                if (j > 0) {
                    series1 = (Companion.seriesValues[j - 1].data)
                    fakeBean[rowNames[j]] = series1[i].toString()
                } else {
                    series2 = time
                    fakeBean[rowNames[j]] = series2[i].toString()
                }

            }
            rows.add(fakeBean)
        }

        val cellsToRefresh = ArrayList<Cell>()
        var cell: Cell
        for (rowName in rowNames) {
            for (row in rows) {
                if (DATA_IN_ROWS) {
                    cell = sheet.createCell(rowNames.indexOf(rowName), rows.indexOf(row) + 1, row[rowName]?.toDouble())
                } else {
                    cell = sheet.createCell(rows.indexOf(row) + 1, rowNames.indexOf(rowName), row[rowName]?.toDouble())
                }

                cell.cellStyle = newCellStyle
                cellsToRefresh.add(cell)
            }
            sheet.autofitColumn(rowNames.indexOf(rowName))
        }
        sheet.refreshCells(cellsToRefresh)

        layoutOutput2.addComponent(lblOutput2)
        sheet.setSizeFull()
        layoutOutput2.addComponent(sheet)
        return layoutOutput2
    }

}
