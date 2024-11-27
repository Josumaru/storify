package id.overlogic.storify.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import id.overlogic.storify.MainActivity
import id.overlogic.storify.R
import id.overlogic.storify.databinding.ActivityLoginBinding
import id.overlogic.storify.ui.auth.register.RegisterActivity
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.Result

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: LoginViewModel? = null
    private val viewModel get() = _viewModel!!
    private var email: String = ""
    private var password: String = ""
    private var isAlertShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        _viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        email = binding.edLoginEmail.text.toString()
        password = binding.edLoginPassword.text.toString()

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.text = getString(R.string.loading)
            viewModel.login(email, password)
        }

        binding.edLoginEmail.addTextChangedListener {
            email = binding.edLoginEmail.text.toString()
            updateLoginButtonState()
        }
        binding.edLoginPassword.addTextChangedListener {
            password = binding.edLoginPassword.text.toString()
            updateLoginButtonState()

        }


        viewModel.error.observe(this) { error ->
            if (!isAlertShown && error != "") {
                isAlertShown = true
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("OK") { dialog, _ ->
                        isAlertShown = false
                        binding.btnLogin.text = getString(R.string.login)
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        viewModel.loading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
            binding.btnLogin.text = if (loading) "${getString(R.string.loading)}..." else getString(R.string.loading)
        }

        viewModel.loginResult.observe(this) { _ ->
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
    private fun updateLoginButtonState() {
        binding.btnLogin.isEnabled = validateInput()
    }

    private fun validateInput(): Boolean {
        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length >= 8
        return isEmailValid && isPasswordValid
    }

}