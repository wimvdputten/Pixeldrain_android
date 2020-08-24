package xo.william.pixeldrain.fileList

import kotlin.random.Random

class FileModel() {
    var date_uploaded: String
    var mime_type: String
    var id: String = "";
    var name: String = "";

    init {
        val random: Int = Random.nextInt(0, 1000);
        this.id = "Id" + random
        this.name = "" + random + ".jpg"
        this.mime_type = "image/jpeg";
        this.date_uploaded = "2020-08-19";
    }

}