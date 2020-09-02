package xo.william.pixeldrain.model

import android.app.Application
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xo.william.pixeldrain.database.AppDatabase
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.database.FileDao
import xo.william.pixeldrain.fileList.FileModel
import xo.william.pixeldrain.repository.FileRepository

class FileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FileRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val filesAmount = 20;
    val allFiles: LiveData<List<FileModel>>
    var fileList: LiveData<List<String>> = MutableLiveData<List<String>>()
    val testInfo: MutableLiveData<String> = MutableLiveData()
    private val fileDao: FileDao = AppDatabase.getDatabase(application, viewModelScope).fileDao()

    init {
        repository = FileRepository(fileDao)
        allFiles = repository.allFiles
        repository.setInfoData(testInfo)
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(file: File) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(file)
    }
}