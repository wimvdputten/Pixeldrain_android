package xo.william.pixeldrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var username = "";
    var password = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userInput.doOnTextChanged { text, start, before, count ->
            username = text.toString();
            loginButton.isEnabled = (username.isNotEmpty() && password.isNotEmpty());
        }

        passwordInput.doOnTextChanged{ text, start, before, count ->
            password = text.toString();
            loginButton.isEnabled =  (username.isNotEmpty() && password.isNotEmpty());
        }

        loginButton.setOnClickListener{
            Toast.makeText(this, "data: ${username} - ${password}", Toast.LENGTH_LONG).show()

        }
    }
}