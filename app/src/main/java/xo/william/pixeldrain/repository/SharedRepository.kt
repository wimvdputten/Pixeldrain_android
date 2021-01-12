package xo.william.pixeldrain.repository

import android.app.Activity
import android.content.Context

class SharedRepository(activity: Activity) {
    val sharedPreferences = activity.getSharedPreferences("default", Context.MODE_PRIVATE);
    private var tokenPath = "token_path";

    fun saveToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(tokenPath, token);
            apply();
        }
    }

    fun isUserLogedIn(): Boolean {
        val authKey = sharedPreferences.getString(tokenPath, "");
        return !authKey.isNullOrEmpty()
    }

    fun getAuthKey(): String {
        val authKey = sharedPreferences.getString(tokenPath, "");

        if (authKey === null) {
            return ""
        }

        return authKey;
    }

    fun deleteToken() {
        // TODO(Clear token correctly);
        with(sharedPreferences.edit()) {
            putString(tokenPath, "")
            apply();
        }
    }
}