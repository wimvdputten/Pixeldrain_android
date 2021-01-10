package xo.william.pixeldrain.fileList


import kotlinx.serialization.*

@Serializable
data class InfoModel(var id: String) {
    var success: Boolean = false;
    var views: Int = 0
    var date_last_view = ""
    var info = "string"
    var thumbnail_href: String = "";
    val baseUrl = "https://pixeldrain.com/api"

    fun getThumbnailUrl(): String {
        return baseUrl + thumbnail_href;
    }

    fun getFileUrl():String {
        return  "${baseUrl}/file/${id}"
    }
}