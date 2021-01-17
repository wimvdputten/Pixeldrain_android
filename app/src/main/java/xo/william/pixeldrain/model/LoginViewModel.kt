package xo.william.pixeldrain.model

import android.app.Application
import androidx.lifecycle.*
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val loginResponse = MutableLiveData<LoginResponse>();
    private val loginRepository = LoginRepository();

    fun loginUser(username: String, password: String) {
        loginRepository.loginUser(username, password, loginResponse);
    }
}