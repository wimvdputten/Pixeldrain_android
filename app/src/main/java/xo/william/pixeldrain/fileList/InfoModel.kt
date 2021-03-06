package xo.william.pixeldrain.fileList


import kotlinx.serialization.*

@Serializable
data class InfoModel(var id: String) {
    var success: Boolean = false;
    var views: Int = 0
    var date_last_view = ""
    var info = "string"
    var thumbnail_href: String = "";
    val baseUrl = "https://pixeldrain.com"
    val name = "";
    val mime_type = "";
    val date_upload = "";
    val can_edit = false

    fun getThumbnailUrl(): String {
        return "${baseUrl}/api/${thumbnail_href}"
    }

    fun getFileUrl():String {
        return  "${baseUrl}/api/file/${id}"
    }

    fun getShareUrl(): String{
        return  "${baseUrl}/u/${id}"
    }
}