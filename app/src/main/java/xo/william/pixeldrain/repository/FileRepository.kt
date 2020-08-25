package xo.william.pixeldrain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.database.FileDao
import xo.william.pixeldrain.fileList.FileModel

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FileRepository(private val fileDao: FileDao) {

    // Room executes all queries on a separate thread.
    val allFiles: LiveData<List<FileModel>> = this.getAllFileModels();



    fun getAllFileModels(): LiveData<List<FileModel>> {
        val files = fileDao.getAll();
        val modelFiles = Transformations.map(files) { files ->
            return@map files.map { file: File ->
                val fileModel = FileModel();
                fileModel.id = file.id;
                fileModel.name = file.fileName;
                fileModel.mime_type = file.mimeType;
                fileModel.date_uploaded = file.dateUpload;
                return@map fileModel;
            }
        }
        return modelFiles;
    }

    suspend fun insert(file: File) {
        fileDao.insert(file)
    }
}