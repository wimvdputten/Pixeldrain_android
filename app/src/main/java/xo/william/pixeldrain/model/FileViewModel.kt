package xo.william.pixeldrain.model

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xo.william.pixeldrain.database.AppDatabase
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.database.FileDao
import xo.william.pixeldrain.fileList.FileModel
import xo.william.pixeldrain.fileList.InfoModel
import xo.william.pixeldrain.repository.FileRepository
import java.io.InputStream

class FileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FileRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allFiles: LiveData<List<FileModel>>
    private val fileDao: FileDao = AppDatabase.getDatabase(application, viewModelScope).fileDao()

    private var _infoFiles: MutableLiveData<MutableList<InfoModel>> = MutableLiveData();
    val infoFiles: LiveData<MutableList<InfoModel>>
    init {
        repository = FileRepository(fileDao)
        allFiles = repository.allFiles
        infoFiles = this.setInfoFiles();
    }

    private fun setInfoFiles(): LiveData<MutableList<InfoModel>> {
        return Transformations.switchMap(allFiles) { files ->
            repository.setInfoFiles(_infoFiles, files);
        }
    }

     fun uploadPost(stream: InputStream?, fileName: String?) {
         if (stream !== null){
             repository.uploadPost(stream, fileName);
         }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(file: File) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(file)
    }
}