package xo.william.pixeldrain.repository

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedRepository {
    private val sharedPreferences: SharedPreferences
    private var tokenPath = "token_path";

    constructor(activity: Activity) {
        sharedPreferences = activity.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    constructor(activity: Application) {
        sharedPreferences = activity.getSharedPreferences("default", Context.MODE_PRIVATE);
    }

    fun saveToken(token: String) {
        with(sharedPreferences.edit()) {
            putString(tokenPath, token);
            apply();
        }
    }

    fun isUserLoggedIn(): Boolean {
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
        with(sharedPreferences.edit()) {
            putString(tokenPath, "")
            apply();
        }
    }
}