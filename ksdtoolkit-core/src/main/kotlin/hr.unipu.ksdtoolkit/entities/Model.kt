package hr.unipu.ksdtoolkit.entities

import hr.unipu.ksdtoolkit.integration.EulerIntegration
import hr.unipu.ksdtoolkit.integration.Integration
import java.text.DecimalFormat
import kotlin.IllegalArgumentException
import kotlin.reflect.full.createInstance

/**
 * Model class represents simulation model.
 *
 * Model defines all [ModelEntity] instances and their cause-effect relationships.
 */

open class Model(
    /**
     * Primary constructor.
     */
    var initialTime: Number = 0.0,
    var finalTime: Number = 100.0,
    var timeStep: Number = 0.25,
    var integrationType: Integration = EulerIntegration(),
    var name: String = "",
    var timeUnit: String = "",

    val entities: HashMap<String, ModelEntity> = hashMapOf(),
    val converters: ArrayList<Converter> = arrayListOf(),
    val modules: HashMap<String, Model> = hashMapOf(),

    var currentTime: Double = initialTime.toDouble()

) {

    /**
     * Getter: list of stocks.
     */
    val stocks: List<Stock>
        get() {
            val stocks = ArrayList<Stock>()
            val modelEntities = ArrayList(this.entities.values)
            modelEntities.forEach { modelEntity ->
                if (modelEntity is Stock) {
                    stocks.add(modelEntity)
                }
            }
            return stocks
        }


    /**
     * Getter: list of model entity keys.
     */
    val modelEntitiesKeys: List<String>
        get() {
            return ArrayList(this.entities.keys)
        }


    /**
     * Getter: list of model entity units.
     */
    val modelEntitiesUnits: List<String>
        get() {
            val modelEntities = this.entities
            val modelEntityUnits = ArrayList<String>()
            modelEntities.forEach { modelEntity ->
                modelEntityUnits.add(modelEntity.value.unit)
            }
            return modelEntityUnits
        }


    /**
     * Getter: list of model entity values.
     */
    val modelEntitiesValues: List<String>
        get() {
            val modelEntityValues = ArrayList<String>()
            val modelEntities = ArrayList(this.entities.values)
            modelEntities.forEach { modelEntity ->
                modelEntityValues.add(DecimalFormat("#.######").format(modelEntity.currentValue))
            }
            return modelEntityValues
        }


    /**
     * Create a new model entity.
     */
    fun createModelEntity(entityType: ModelEntityType, name: String): ModelEntity? {
        val modelEntity: ModelEntity
        when (entityType) {
            ModelEntityType.STOCK -> modelEntity = Stock(name)
            ModelEntityType.FLOW -> modelEntity = Flow(name)
            ModelEntityType.CONSTANT -> modelEntity = Constant(name)
            ModelEntityType.CONVERTER -> modelEntity = Converter(name)
        }
        this.addModelEntity(modelEntity)
        return modelEntity
    }


    /**
     * Create a new entity - Stock.
     */
    fun stock(name: String): Stock {
        val modelEntity: ModelEntity = Stock(name)
        this.addModelEntity(modelEntity)
        return modelEntity as Stock
    }


    /**
     * Create a new entity - Flow.
     */
    fun flow(name: String): Flow {
        val modelEntity: ModelEntity = Flow(name)
        this.addModelEntity(modelEntity)
        return modelEntity as Flow
    }


    /**
     * Create a new entity - Constant.
     */
    fun constant(name: String): Constant {
        val modelEntity: ModelEntity = Constant(name)
        this.addModelEntity(modelEntity)
        return modelEntity as Constant
    }


    /**
     * Create a new entity - Converter.
     */
    fun converter(name: String): Converter {
        val modelEntity: ModelEntity = Converter(name)
        this.addModelEntity(modelEntity)
        return modelEntity as Converter
    }


    /**
     * Add model entity to the model.
     */
    fun addModelEntity(modelEntity: ModelEntity) {
        if (!existModelEntity(modelEntity)) {
            this.entities[modelEntity.name] = modelEntity
        } else {
            throw IllegalArgumentException("Duplicate model entity.")
        }

    }


    /**
     * Check whether model already contains a model entity or not.
     */
    private fun existModelEntity(modelEntity: ModelEntity): Boolean {
        return this.entities.containsKey(modelEntity.name)
    }


    /**
     * Create a new converter.
     */
    fun createConverter(entity: ModelEntity, vararg inputs: ModelEntity): Converter {
        val converter = Converter(entity, *inputs)
        this.addConverter(converter)
        entity.converter = converter
        return converter
    }


    /**
     * Add converter to model.
     */
    private fun addConverter(converter: Converter) {
        this.converters.add(converter)
    }


    /**
     * Create a new module.
     */
    fun createModule(moduleName: String, modelPath: String): Model {
        val moduleObject = Class.forName(modelPath).kotlin.createInstance()
        val module = moduleObject as Model
        module.name = moduleName
        this.addModule(module)
        return module
    }


    /**
     * Add module to model.
     */
    private fun addModule(module: Model) {
        if (!existModule(module)) {
            this.modules[module.name] = module
        } else {
            throw IllegalArgumentException("Duplicate model module.")
        }
    }


    /**
     * Check whether model already contains a module or not.
     */
    private fun existModule(module: Model): Boolean {
        return this.modules.containsKey(module.name)
    }


    /**
     * Print model entities.
     */
    override fun toString(): String {
        return "Model [entities = ${this.entities} ]"
    }

}