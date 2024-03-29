package cn.xor7.abrezk.resourcepack.convertor

import kotlinx.serialization.Serializable

@Serializable
data class PackMeta(
    var pack: PackData,
)

@Suppress("PropertyName")
@Serializable
data class PackData(
    var pack_format: Int,
    val description: String,
)
