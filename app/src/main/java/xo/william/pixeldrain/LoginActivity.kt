package xo.william.pixeldrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_login.*
import xo.william.pixeldrain.model.LoginResponse
import xo.william.pixeldrain.model.LoginViewModel
import xo.william.pixeldrain.repository.SharedRepository

class LoginActivity : AppCompatActivity() {

    private var username = "";
    private var password = "";

    private lateinit var loginViewModel: LoginViewModel;
    private lateinit var sharedRepository: SharedRepository;

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedRepository = SharedRepository(this);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        userInput.doOnTextChanged { text, start, before, count ->
            username = text.toString();
            loginButton.isEnabled = (username.isNotEmpty() && password.isNotEmpty());
        }

        passwordInput.doOnTextChanged { text, start, before, count ->
            password = text.toString();
            loginButton.isEnabled = (username.isNotEmpty() && password.isNotEmpty());
        }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false;
            loginProgress.visibility = View.VISIBLE

            loginViewModel.loginUser(username, password);
        }

        loginViewModel.loginResponse.observe(this,
            Observer { response -> handleLoginResponse(response) })
    }

    fun handleLoginResponse(response: LoginResponse) {
        loginProgress.visibility = View.GONE;
        if (response.auth_key.isNotEmpty()) {
            sharedRepository.saveToken(response.auth_key);
            setResult(200)
            finish();
        } else {
            loginButton.isEnabled = true;
            Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_LONG).show()
        }
    }

}