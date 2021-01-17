package xo.william.pixeldrain.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.UploadRequest
import com.github.kittinunf.fuel.core.requests.upload
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result;
import xo.william.pixeldrain.fileList.InfoModel
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.InputStream

class FuelService() {
    private val baseUri = "https://pixeldrain.com/api/"

    private val format = Json { ignoreUnknownKeys = true }
    private val authKeyCookie = "pd_auth_key";

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

    fun getFileInfoById(id: String): Request {
        val url = "${baseUri}file/${id}/info";
        return Fuel.get(url)
    }

    fun uploadAnonFile(selectedFile: InputStream, fileName: String?): UploadRequest {
        val url = baseUri + "file";
        val setFileName = if (fileName !== null) fileName else "file";

        Log.d("response", "url: " + url + " " + fileName);
        return Fuel.upload(url, method = Method.POST, parameters = listOf("name" to setFileName))
            .add(BlobDataPart(selectedFile, name = "file", filename = setFileName));
    }


    fun uploadFile(selectedFile: InputStream, fileName: String?, authKey: String): UploadRequest {
        val url = baseUri + "file";
        val setFileName = if (fileName !== null) fileName else "file";
        val authKeyCookie = "${authKeyCookie}=${authKey}";

        return Fuel.upload(url, method = Method.POST, parameters = listOf("name" to setFileName))
            .header(Headers.COOKIE to authKeyCookie).upload()
            .add(BlobDataPart(selectedFile, name = "file", filename = setFileName))
    }

    fun getFiles(authKey: String): Request {
        val url = "${baseUri}/user/files"
        val authKeyCookie = "${authKeyCookie}=${authKey}";

        return Fuel.get(url, parameters = listOf("page" to 0, "limit" to 1000))
            .header(Headers.COOKIE to authKeyCookie)
    }

    fun loginUser(username: String, password: String): Request {
        val url ="${baseUri}/user/login"
        return Fuel.post(url, parameters = listOf("username" to username, "password" to password));
    }

    fun deleteFile(id: String, authKey: String): Request {
        val url ="${baseUri}/file/${id}"
        val authKeyCookie = "${authKeyCookie}=${authKey}";

        return Fuel.delete(url).header(Headers.COOKIE to authKeyCookie)
    }

    fun getFileText(fileUrl: String): Request {
        return Fuel.get(fileUrl);
    }
}