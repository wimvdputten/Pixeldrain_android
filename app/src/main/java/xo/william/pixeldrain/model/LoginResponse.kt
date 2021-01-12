package xo.william.pixeldrain.model


import kotlinx.serialization.*

@Serializable
data class LoginResponse( var authKey: String = "") {
    var auth_key: String = ""
    var message: String = ""
    var succes: Boolean = false;
}