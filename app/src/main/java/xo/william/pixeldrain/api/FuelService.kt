package xo.william.pixeldrain.api

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.requests.UploadRequest
import com.github.kittinunf.fuel.core.requests.upload
import java.io.InputStream

class FuelService() {
    private val baseUri = "https://pixeldrain.com/api/"
    private val authKeyCookie = "pd_auth_key";

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