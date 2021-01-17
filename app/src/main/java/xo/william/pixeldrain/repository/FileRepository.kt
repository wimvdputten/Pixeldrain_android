package xo.william.pixeldrain.repository

import android.util.Log
import androidx.lifecycle.*
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.requests.UploadRequest
import xo.william.pixeldrain.api.FuelService
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.database.FileDao
import xo.william.pixeldrain.fileList.InfoModel
import com.github.kittinunf.result.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.fileList.InfoModelList
import java.io.InputStream

class FileRepository(private val fileDao: FileDao) {

    // Room executes all queries on a separate thread.

    private val fuelService = FuelService()
    private val format = Json { ignoreUnknownKeys = true }

    fun getDatabaseFiles(): LiveData<List<File>> {
        return fileDao.getAll()
    }

    fun insert(file: File) {
        fileDao.insert(file)
    }

    fun deleteFromDb(id: String) {
        return fileDao.deleteById(id)
    }

    fun uploadAnonPost(selectedFile: InputStream, fileName: String?): UploadRequest {
        return fuelService.uploadAnonFile(selectedFile, fileName)
    }

    fun uploadPost(selectedFile: InputStream, fileName: String?, authKey: String): UploadRequest {
        return fuelService.uploadFile(selectedFile, fileName, authKey)

    }

    fun loadFileInfo(file: File, loadedFiles: MutableLiveData<MutableList<InfoModel>>) {
        fuelService.getFileInfoById(file.id).responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    val infoFile = format.decodeFromString<InfoModel>(result.get())
                    loadedFiles.value?.add(infoFile)
                    loadedFiles.postValue(loadedFiles.value)
                }
                is Result.Failure -> {
                    Log.d("response", "error: " + result.error.exception.message)
                }
            }
        }
    }

    fun loadApiFiles(loadedFiles: MutableLiveData<MutableList<InfoModel>>, authKey: String) {
        fuelService.getFiles(authKey).responseString { _, _, result ->
            when (result) {
                is Result.Success -> {
                    val infoModelList = format.decodeFromString<InfoModelList>(result.get())
                    loadedFiles.value?.addAll(infoModelList.files)
                    loadedFiles.postValue(loadedFiles.value)
                }
                is Result.Failure -> {
                    Log.d("response", "error: " + result.error.exception.message)
                }
            }
        }
    }

    fun deleteFromApi(id: String, authKey: String): Request {
        return fuelService.deleteFile(id, authKey)
    }

    fun deleteFromLoadedFiles(id: String, loadedFiles: MutableLiveData<MutableList<InfoModel>>) {
        val loadedFilesValue = loadedFiles.value
        if (!loadedFilesValue.isNullOrEmpty()) {
            loadedFilesValue.removeAll { it.id == id }
            loadedFiles.postValue(loadedFilesValue)
        }
    }

}