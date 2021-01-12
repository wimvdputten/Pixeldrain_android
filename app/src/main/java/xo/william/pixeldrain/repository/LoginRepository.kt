package xo.william.pixeldrain.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.result.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xo.william.pixeldrain.api.FuelService
import xo.william.pixeldrain.model.LoginResponse


class LoginRepository() {
    private val fuelService = FuelService();
    private val format = Json { ignoreUnknownKeys = true }

    fun loginUser(
        username: String,
        password: String,
        loginResponse: MutableLiveData<LoginResponse>,
    ) {
        fuelService.loginUser(username, password).responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    val loginResponseData = LoginResponse("")
                    loginResponseData.message = "Error: ${ex.message}"
                    loginResponse.postValue(loginResponseData);
                }

                is Result.Success -> {
                    val data = result.get()
                    val loginResponseData = format.decodeFromString<LoginResponse>(data);
                    loginResponseData.succes = true;
                    Log.d("response", "data: ${data}")
                    loginResponse.postValue(loginResponseData);
                }
            }
        }

    }
}