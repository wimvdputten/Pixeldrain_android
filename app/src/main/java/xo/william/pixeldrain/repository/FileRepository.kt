package xo.william.pixeldrain.repository

import androidx.lifecycle.*
import xo.william.pixeldrain.api.FuelService
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.database.FileDao
import xo.william.pixeldrain.fileList.FileModel
import xo.william.pixeldrain.fileList.InfoModel
import java.io.InputStream

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FileRepository(private val fileDao: FileDao) {


    // Room executes all queries on a separate thread.
    val allFiles: LiveData<List<FileModel>> = this.getAllFileModels();
    private val fuelService = FuelService();

    fun getAllFileModels(): LiveData<List<FileModel>> {
        val liveDataFiles = fileDao.getAll();
        val modelFiles = Transformations.map(liveDataFiles) { files ->
            return@map files.map { file: File ->
                return@map transformDBfile(file);
            }
        }
        return modelFiles;
    }

    fun transformDBfile(file: File): FileModel {
        val fileModel = FileModel();
        fileModel.id = file.id;
        fileModel.name = file.fileName;
        fileModel.mime_type = file.mimeType;
        fileModel.date_uploaded = file.dateUpload;
        return fileModel;
    }

    suspend fun insert(file: File) {
        fileDao.insert(file)
    }

    fun uploadPost(selectedFile: InputStream, fileName: String?) {
        return fuelService.uploadFile(selectedFile, fileName);
    }

    fun setInfoFiles(_infoFiles: MutableLiveData<MutableList<InfoModel>>, files: List<FileModel>):
            MutableLiveData<MutableList<InfoModel>> {
        for (file in files) {
            fuelService.setInfoData(_infoFiles, file.id);
        }
        return _infoFiles;
    }

}