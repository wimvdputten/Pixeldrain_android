package xo.william.pixeldrain.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.httpGet

import com.github.kittinunf.result.Result;


class FuelService() {
    private val baseUri = "https://pixeldrain.com/api/"

    //'https://pixeldrain.com/api/file/6BBUUerM/info'

    fun setInfoData(liveDataData: MutableLiveData<String>) {
        Log.i("api", "Called")
        val httpAsync = "https://pixeldrain.com/api/file/6BBUUerM/info"
            .httpGet()
            .responseString { request, response, result ->
                Log.i("api", "Flight");
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
                        liveDataData.postValue(ex.toString())
                    }
                    is Result.Success -> {
                        val data = result.get()
                        liveDataData.postValue(data)
                    }
                }
            }
    }
}