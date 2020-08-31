package xo.william.pixeldrain.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xo.william.pixeldrain.database.AppDatabase
import xo.william.pixeldrain.database.File
import xo.william.pixeldrain.fileList.FileModel
import xo.william.pixeldrain.repository.FileRepository

class FileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FileRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allFiles: LiveData<List<FileModel>>

    init {
        val fileDao = AppDatabase.getDatabase(application, viewModelScope).fileDao()
        repository = FileRepository(fileDao)
        allFiles = repository.allFiles
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(file: File) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(file)
    }
}