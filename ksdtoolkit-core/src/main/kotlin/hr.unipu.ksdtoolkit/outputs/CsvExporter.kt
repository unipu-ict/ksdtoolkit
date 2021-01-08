package hr.unipu.ksdtoolkit.outputs


import hr.unipu.ksdtoolkit.entities.Model
import hr.unipu.ksdtoolkit.simulations.ISimulationEventHandler
import org.slf4j.LoggerFactory
import java.io.FileWriter
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Class for exporting simulation data in CSV format
 * (also implements the [SimulationEventListener] interface).
 */
class CsvExporter(private val csvFile: String = "output.csv",       // Default name.
                  private val separator: String = ";"               // Default separator.
) : ISimulationEventHandler {

    val sb = StringBuilder()

    private val string: String
        get() {
            val last = sb.lastIndexOf("\n")
            if (last >= 0 && sb.length - last == 1) {
                sb.delete(last, sb.length)
            }
            return sb.toString()
        }


    /**
     * Implementation of method simulationInitialized() from ISimulationEventHandler interface.
     * Method 1) clears StringBuilder content and 2) writes simulation data, after initialization.
     */
    override fun simulationInitialized(model: Model) {
        clearContent()
        writeTimeStepValues(model.modelEntitiesKeys)
    }

    /**
     * Implementation of method simulationInitialized() from ISimulationEventHandler interface.
     * Method 1) clears StringBuilder content and 2) writes simulation data, after every time step.
     */
    override fun timeStepCalculated(model: Model) {
        writeTimeStepValues(model.modelEntitiesValues)
    }

    /**
     * Implementation of method simulationInitialized() from ISimulationEventHandler interface.
     * Method saves simulation data into CSV file, after simulation is finished.
     */
    override fun simulationFinished(model: Model) {
        saveFile()
    }


    /**
     * Clear CSV content.
     */
    private fun clearContent() {
        sb.delete(0, sb.length)
    }


    /**
     * Write all simulation data for one timestep to the CSV.
     *
     * @param modelEntityValues values of the model entities.
     */
    private fun writeTimeStepValues(modelEntityValues: List<String>) {
        var first = true
        for (value in modelEntityValues) {
            if (!first) {
                sb.append(separator)
            }
            sb.append(value)
            first = false
        }
        sb.append("\n")
    }

    /**
     * Save data to CSV file.
     */
    private fun saveFile() {
        try {
            FileWriter(csvFile).use { writer ->
                writer.append(string)
                writer.flush()
                LoggerFactory.getLogger(javaClass).info("CSV file saved.")
            }
        } catch (exception: IOException) {
            LoggerFactory.getLogger(javaClass).error("Unable to save file.", exception)
        }
    }

}
