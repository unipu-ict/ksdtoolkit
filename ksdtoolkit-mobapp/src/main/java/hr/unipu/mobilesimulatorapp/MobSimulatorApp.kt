package hr.unipu.mobilesimulatorapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import hr.unipu.ksdtoolkit.entities.ModelEntity
import hr.unipu.ksdtoolkit.models.ModelInnovationDiffusion
import hr.unipu.ksdtoolkit.simulations.Simulation
import org.slf4j.LoggerFactory

/**
 * Android mobile simulator app - for chart plotting and interactive simulation.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class MobSimulatorApp : AppCompatActivity() {

    init {

        /**---Creating: model, simulation and run.---**/

        // 1. Create a demo model.
        val model = ModelInnovationDiffusion()      // Or other models: ModelGenericSD(), ModelTestSpeed(), etc.

        // 2. Create the simulation.
        simulation = Simulation(model)

        // 3. Add results handlers
        simulation.addSimulationEventListener(MobSimulator())

        // 4. Run simulation (so that all expressions are invoked for time=0).
        simulation.run()

    }

    companion object {
        public lateinit var simulation: Simulation

        public var simulationFinished: Boolean = false
        public var seriesName: ArrayList<String> = ArrayList()
        public var seriesValue: ArrayList<ArrayList<Entry>> = ArrayList()
        public var constantsName: ArrayList<String> = ArrayList()
        public var constantsValue: ArrayList<Entry> = ArrayList()
        public var time: ArrayList<Double> = ArrayList()
        public val lineDataSets: ArrayList<LineDataSet> = ArrayList()

        fun addSeriesNames(modelEntityNames: List<String>) {
            seriesName = ArrayList()
            seriesValue = ArrayList()
            for (modelEntityName in modelEntityNames) {
                seriesName.add(modelEntityName)
                seriesValue.add(ArrayList<Entry>())
                lineDataSets.add(LineDataSet(null, ""))
            }
        }

        fun addSeriesValues(modelEntityValues: List<String>, currentTime: Double) {
            time.add(currentTime)
            for (i in 0..modelEntityValues.indices.last) {
                val valueString = modelEntityValues[i]
                val value = valueString.toFloat()
                val entry = Entry(currentTime.toFloat(), value)
                seriesValue[i].add(entry)
            }
        }

        fun addConstants(modelEntityConstants: Map<String, ModelEntity>) {
            constantsName = ArrayList()
            constantsValue = ArrayList()
            for (modelEntityConstant in modelEntityConstants) {
                constantsName.add(modelEntityConstant.key)
                val value = modelEntityConstant.value.currentValue.toFloat()
                val entry = Entry(0f, value)
                constantsValue.add(entry)
            }
        }

    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoggerFactory.getLogger(javaClass).info("---onCreate() invoked.---")
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

        drawUI()
    }


    /**
     * Creating layout programmatically (dynamically).
     */
    private fun drawUI() {
        LoggerFactory.getLogger(javaClass).info("---drawUI() invoked.---")

        val rootLayout =  ScrollView(this)
        rootLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val linearLayoutHorizontal = LinearLayout(this)
        val linearLayoutHorizontalParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
        rootLayout.addView(linearLayoutHorizontal, linearLayoutHorizontalParams)

        val linearLayoutVertical = LinearLayout(this)

        linearLayoutVertical.id = R.id.insertPoint
        linearLayoutVertical.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        linearLayoutHorizontal.addView(linearLayoutVertical)

        for (s in MobSimulatorApp.constantsName) {
            val textView = TextView(this)
            textView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.text = s
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            linearLayoutVertical.addView(textView)

            val editText = EditText(this)
            editText.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            editText.setEms(5)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            val indexOfEntity = simulation.model.modelEntitiesKeys.indexOf(s)
            editText.setText(simulation.model.modelEntitiesValues[indexOfEntity])
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
            editText.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (!hasFocus) {
                        val newValue = editText.text.toString().toDouble()
                        val modelVariableName = textView.text
                        simulation.model.entities[modelVariableName]?.equation = { newValue }
                        refreshUI()
                    }
                }
            })
            linearLayoutVertical.addView(editText)
        }

        val lineChart = LineChart(this)

        val myColorsPalette = intArrayOf(
            Color.rgb(217, 80, 138),
            Color.rgb(254, 149, 7),
            Color.rgb(254, 247, 120),
            Color.rgb(106, 167, 134),
            Color.rgb(53, 194, 209),
            Color.rgb(64, 89, 128),
            Color.rgb(149, 165, 124),
            Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134),
            Color.rgb(179, 48, 80)
        )
        val numberOfColorInPalette = myColorsPalette.size

        for (i in seriesValue.indices) {
            lineDataSets[i] = LineDataSet(seriesValue[i], seriesName[i])
            lineDataSets[i].color = myColorsPalette[i.rem(numberOfColorInPalette)]
            lineDataSets[i].circleHoleColor = myColorsPalette[i.rem(numberOfColorInPalette)]
            lineDataSets[i].setCircleColor(myColorsPalette[i.rem(numberOfColorInPalette)])
            lineDataSets[i].setDrawValues(false)
            lineDataSets[i].setAxisDependency(YAxis.AxisDependency.LEFT)
        }

        lineChart.data = LineData(lineDataSets as List<ILineDataSet>?)

        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                fun Float.format(digits: Int) = "%.${digits}f".format(this)
                val tooltipText = "x=${e?.x?.format(2)}, y=${e?.y?.format(2)}"
                LoggerFactory.getLogger(javaClass).info("---Value selected: $tooltipText---")
                Toast.makeText(this@MobSimulatorApp, tooltipText, Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected() { }
        })

        lineChart.layoutParams = ViewGroup.LayoutParams(

            ViewGroup.LayoutParams.MATCH_PARENT,

            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250F, this.resources.displayMetrics).toInt()
        )
        lineChart.id = R.id.lineChart
        linearLayoutHorizontal.addView(lineChart)

        setContentView(rootLayout);
    }


    private fun runSimulation() {
        LoggerFactory.getLogger(javaClass).info("---runSimulation() invoked.---")

        if (MobSimulatorApp.simulationFinished == false) {
            val doesNotContainMobSimulatorListener = simulation.outputHandlers.filterIsInstance(MobSimulator()::class.java).isEmpty()
            if (doesNotContainMobSimulatorListener) {

                simulation.addSimulationEventListener(MobSimulator())
            }

            simulation.run()
        }
    }


    private fun refreshUI() {
        LoggerFactory.getLogger(javaClass).info("---refreshUI() invoked.---")

        MobSimulatorApp.simulationFinished = false
        runSimulation()

        drawUI()
    }


}


