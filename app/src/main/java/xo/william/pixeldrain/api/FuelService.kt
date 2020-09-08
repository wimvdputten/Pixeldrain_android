package xo.william.pixeldrain.api

import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.httpGet

import com.github.kittinunf.result.Result;
import xo.william.pixeldrain.fileList.InfoModel

import kotlinx.serialization.*
import kotlinx.serialization.json.*

class FuelService() {
    private val baseUri = "https://pixeldrain.com/api/"

    private val format = Json { ignoreUnknownKeys = true }

    fun setInfoData(_infoFiles: MutableLiveData<MutableList<InfoModel>>, id: String) {
        val url = baseUri + "file/" + id + "/info" // TODO: 2-9-2020 Add string formater
        url.httpGet().responseString { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    val infoModel = InfoModel(id);
                    infoModel.info = ex.message!!;
                    if (_infoFiles.value.isNullOrEmpty()) {
                        _infoFiles.postValue(mutableListOf(infoModel))
                    } else {
                        _infoFiles.value?.add(infoModel);
                        _infoFiles.postValue(_infoFiles.value);
                    }
                }
                is Result.Success -> {
                    val data = result.get()
                    val infoModel = format.decodeFromString<InfoModel>(data);

                    if (_infoFiles.value.isNullOrEmpty()) {
                        _infoFiles.postValue(mutableListOf(infoModel))
                    } else {
                        _infoFiles.value?.add(infoModel);
                        _infoFiles.postValue(_infoFiles.value);
                    }

                }
            }
        }
    }
}