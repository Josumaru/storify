package id.overlogic.storify.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import id.overlogic.storify.R
import id.overlogic.storify.databinding.ActivityRegisterBinding
import id.overlogic.storify.ui.auth.login.LoginActivity
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.Result

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: RegisterViewModel? = null
    private val viewModel get() = _viewModel!!
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private var isAlertShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        _viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnLogin.isEnabled = false
        setUpEditText()

        viewModel.loading.observe(this) { loading ->
            binding.btnLogin.text = if (loading) "${getString(R.string.loading)}..." else getString(R.string.register)
        }
        viewModel.registerResult.observe(this) { result ->
            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            binding.btnLogin.isEnabled = true
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        viewModel.error.observe(this) { error ->
            if (!isAlertShown && error != "") {
                isAlertShown = true
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("OK") { dialog, _ ->
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.register)
                        isAlertShown = false
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

    }

    private fun setUpEditText() {
        binding.btnLogin.setOnClickListener {
            Toast.makeText(this, "Registerring", Toast.LENGTH_SHORT).show()
            viewModel.register(name, email, password)
            binding.btnLogin.isEnabled = false
        }
        binding.edRegisterName.addTextChangedListener {
            binding.btnLogin.isEnabled =
                binding.edRegisterName.text!!.isNotEmpty() && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null
            name = binding.edRegisterName.text.toString()
        }
        binding.edRegisterEmail.addTextChangedListener {
            binding.btnLogin.isEnabled =
                binding.edRegisterName.text!!.isNotEmpty() && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null
            email = binding.edRegisterEmail.text.toString()
        }
        binding.edRegisterPassword.addTextChangedListener {
            binding.btnLogin.isEnabled =
                binding.edRegisterName.text!!.isNotEmpty() && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null
            password = binding.edRegisterPassword.text.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
