package xo.william.pixeldrain.fileList

class DummyAdapter {

    fun getDummyFiles(): Array<FileModel> {
        val myDataset = arrayOf(
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel(),
            FileModel()
        )
        return myDataset;
    }

    fun getAntFile(): FileModel {
        val fileModel = FileModel();
        fileModel.id = "rv3DeDaJ";
        fileModel.name = "tumblr_m5l7dp1FK61qab0ywo1_500.png";
        fileModel.date_uploaded = "2020-10-06T09:11:07.625969Z"
        fileModel.mime_type = "image/jpeg";
        return fileModel;
    }
}