package cn.xor7.abrezk

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class PackMeta(
    var pack_format: Int,
    val description: String,
)
