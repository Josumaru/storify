package id.overlogic.storify.ui.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import id.overlogic.storify.MainActivity
import id.overlogic.storify.R
import id.overlogic.storify.databinding.FragmentCreateBinding
import id.overlogic.storify.ui.common.factory.ViewModelFactory
import id.overlogic.storify.util.UriConverter
import id.overlogic.storify.util.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class CreateFragment : Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: CreateViewModel? = null
    private val viewModel get() = _viewModel!!
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        _viewModel = ViewModelProvider(this, factory)[CreateViewModel::class.java]
        viewModel.uri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                currentImageUri = uri
                binding.ivCover.setImageURI(uri)
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setChecked(isChecked)
            if (isChecked) {
                getMyLastLocation()
            }
        }
        binding.btnCamera.setOnClickListener {
            createImageFile()?.let { file ->
                currentImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    file
                )
                launcherIntentCamera.launch(currentImageUri)
            } ?: Toast.makeText(
                context,
                getString(R.string.error_creating_image_file), Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.buttonAdd.isEnabled = !loading
            binding.pbLoading.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonAdd.text =
                if (loading) getString(R.string.uploading) else getString(R.string.upload)
            binding.btnGallery.isEnabled = !loading
            binding.btnCamera.isEnabled = !loading
        }
        viewModel.isChecked.observe(viewLifecycleOwner) {
            binding.checkBox.isChecked = it
        }
        viewModel.upload.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                if (!result.error!!) {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            AlertDialog.Builder(requireActivity())
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()

        }
        binding.buttonAdd.setOnClickListener {
            if (binding.edAddDescription.text.toString() == "") {
                Toast.makeText(
                    context,
                    getString(R.string.please_insert_a_description),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (currentImageUri != null) {
                binding.buttonAdd.isEnabled = false
                binding.pbLoading.visibility = View.VISIBLE
                binding.buttonAdd.text = getString(R.string.converting)
                binding.btnGallery.isEnabled = false
                binding.btnCamera.isEnabled = false
                lifecycleScope.launch {
                    try {
                        val converter = UriConverter()
                        val imageFile = converter.uriToFile(currentImageUri!!, requireContext())
                            .reduceFileImage()

                        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                        val multipartBody = MultipartBody.Part.createFormData(
                            "photo",
                            imageFile.name,
                            requestImageFile
                        )
                        val requestBody =
                            binding.edAddDescription.text.toString()
                                .toRequestBody("text/plain".toMediaType())
                        viewModel.uploadStory(multipartBody, requestBody)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            getString(R.string.failed_to_process_image), Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.no_image_selected), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                viewModel.setUri(it)
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.failed_to_capture_image), Toast.LENGTH_SHORT
            ).show()
            currentImageUri = null
        }
    }

    private fun createImageFile(): File? {
        return try {
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "IMG_${System.currentTimeMillis()}_",
                ".jpg",
                storageDir
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setUri(uri)
        } else {
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.permission_not_granted),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.setLat(location.latitude)
                    viewModel.setLon(location.longitude)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.location_is_not_found_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.setChecked(false)
                    fusedLocationClient.requestLocationUpdates(
                        LocationRequest.create().apply {
                            interval = 10000
                            fastestInterval = 5000
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        },
                        object : LocationCallback() {
                            override fun onLocationResult(p0: LocationResult) {
                                super.onLocationResult(p0)
                                p0.locations.firstOrNull()?.let {
                                    viewModel.setLat(it.latitude)
                                    viewModel.setLon(it.longitude)
                                }
                                viewModel.setChecked(false)
                            }
                        },
                        null
                    )
                }
            }
        } else {
            viewModel.setChecked(false)
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _viewModel = null
    }
}
