package xo.william.pixeldrain.fileList


import kotlinx.serialization.*

@Serializable
data class InfoModelList(
    @Serializable
    val files: List<InfoModel>
)

