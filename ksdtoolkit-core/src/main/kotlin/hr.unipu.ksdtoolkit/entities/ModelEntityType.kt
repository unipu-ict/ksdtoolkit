package hr.unipu.ksdtoolkit.entities

/**
 * Three entities of system dynamics models: [Stock], [Flow] and [Constant].
 * [Flow] and [Converter] are basically the same entity, but with different roles.
 *
 */
enum class ModelEntityType {
    STOCK, FLOW, CONSTANT, CONVERTER
}