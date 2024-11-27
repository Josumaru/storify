package id.overlogic.storify.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.overlogic.storify.R
import id.overlogic.storify.databinding.ActivityDetailBinding
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.DateFormatter

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private var _viewModel: DetailViewModel? = null
    private val viewModel get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id = intent.getStringExtra(EXTRA_ID)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        _viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        viewModel.getDetail(id ?: "")
        viewModel.loading.observe(this) { loading ->
            binding.pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(this) { error ->
            if (error != null) {
                if (error.isEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(error)
                        .setPositiveButton("OK") { _, _ ->
                            finish()
                        }
                        .create()
                        .show()
                }
            }
        }


        viewModel.detail.observe(this) { detail ->
            binding.tvDetailName.text = detail.story?.name ?: "Unknown"
            binding.tvDetailDescription.text = detail.story?.description ?: "Unknown"
            binding.tvSubTitle.text = DateFormatter(detail.story?.createdAt ?: "").format()


            Glide.with(this)
                .load(detail.story?.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_error)
                )
                .into(binding.ivDetailPhoto)
        }

        binding.cvBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}