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
        fileModel.id = "6BBUUerM";
        fileModel.name = "9qPDXw8F_400x400.jpg";
        fileModel.date_uploaded = "2020-08-19T09:11:07.625969Z"
        fileModel.mime_type = "image/jpeg";
        return fileModel;
    }
}