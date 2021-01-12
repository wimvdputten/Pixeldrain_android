package xo.william.pixeldrain.repository

import android.app.Activity
import android.content.Context

class SharedRepository(activity: Activity) {
    val sharedPreferences = activity.getSharedPreferences("default", Context.MODE_PRIVATE);
    private var token_path = "token_path";

    fun saveToken(token: String) {
        with (sharedPreferences.edit()) {
            putString(token_path,token);
            apply();
        }
    }

}