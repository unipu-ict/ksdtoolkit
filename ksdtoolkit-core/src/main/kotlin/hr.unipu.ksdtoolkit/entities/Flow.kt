package hr.unipu.ksdtoolkit.entities

/**
 * Flow entity.
 *
 * Flow can be an "input_flow" or "output_flow", which difference defines "change_rate" of a [Stock].
 * Flows are special kind of converters. Flows can also be inputEntities to other converters.
 *
 * @property name Flow name (only passed to the abstract class ModelEntity).
 */

class Flow(name: String) : ModelEntity(name)

