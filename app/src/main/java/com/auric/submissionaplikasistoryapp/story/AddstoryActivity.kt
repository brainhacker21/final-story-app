package com.auric.submissionaplikasistoryapp.story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.auric.submissionaplikasistoryapp.R
import com.auric.submissionaplikasistoryapp.databinding.ActivityAddstoryBinding
import com.auric.submissionaplikasistoryapp.viewmodel.StoryViewModel
import com.auric.submissionaplikasistoryapp.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.random.Random

@AndroidEntryPoint
class AddstoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddstoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userViewModel by viewModels<UserViewModel>()
    private val storyViewModel by viewModels<StoryViewModel>()
    private var getFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddstoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupPermission()
        setupAction()
        getToken()

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.location.setOnClickListener { getLocationNow() }
    }

    private fun setupPermission(){
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun getToken() {
        userViewModel.getUserPreferences().observe(this) {
            if (it.token.trim() != "") {
                EXTRA_TOKEN = it.token
            }
        }
    }

    private fun setupAction() {
        if (getFile == null) {
            Glide.with(this).load(getFile).placeholder(R.drawable.ic_place_holder)
                .fallback(R.drawable.ic_place_holder).into(binding.previewImageView)
        }

        binding.apply {
            cameraButton.setOnClickListener {
                launcherIntentCamera
            }
            galleryButton.setOnClickListener {
                launcherIntentGallery
            }
            uploadButton.setOnClickListener {
                uploadImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.denyper,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose))
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddstoryActivity,
                "com.auric.submissionaplikasistoryapp",
                it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)

        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddstoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getLocationNow()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getLocationNow()
            else -> {}
        }
    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLocationNow() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val addressName = getAddressName(LatLng(location.latitude, location.longitude))
                    binding.lokasi.setText(addressName)
                } else {
                    Toast.makeText(this@AddstoryActivity, R.string.empty_location, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getAddressName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val allAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (allAddress.isEmpty()) getString(R.string.empty_address) else allAddress[0].getAddressLine(
                0
            )
        } catch (e: Exception) {
            getString(R.string.empty_address)
        }
    }

    private fun addressToCoordinate(locationName: String): LatLng {
        return try {
            val randomLatitude = randomCoordinate()
            val randomLongitude = randomCoordinate()

            val geocoder = Geocoder(this)
            val allLocation = geocoder.getFromLocationName(locationName, 1)
            if (allLocation.isEmpty()) {
                LatLng(randomLatitude, randomLongitude)
            } else {
                LatLng(allLocation[0].latitude, allLocation[0].longitude)
            }
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    private fun randomCoordinate(): Double {
        return Random.nextDouble(15.0, 100.0)
    }

    private fun uploadImage() {
        val description = binding.descstory.text.toString()

        when {
            getFile == null -> {
                Toast.makeText(
                    this@AddstoryActivity,
                    R.string.insertfoto,
                    Toast.LENGTH_SHORT
                ).show()
            }
            description.trim().isEmpty() -> {
                Toast.makeText(
                    this@AddstoryActivity,
                    R.string.complete,
                    Toast.LENGTH_SHORT
                ).show()
                binding.descstory.error = getString(R.string.complete)
            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val address = binding.lokasi.text.toString()
                val location = addressToCoordinate(address)
                storyViewModel.uploadStory(EXTRA_TOKEN, description, file, location)

                storyViewModel.responseUpload.observe(this) { response ->
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    if (!response.error) {
                        val intent = Intent(this@AddstoryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingBar.visibility = View.VISIBLE
        } else {
            binding.loadingBar.visibility = View.GONE
        }

        binding.apply {
            cameraButton.isEnabled = !isLoading
            uploadButton.isEnabled = !isLoading
            galleryButton.isEnabled = !isLoading
            descstory.isEnabled = !isLoading
        }
    }

    companion object {
        private var EXTRA_TOKEN = "token"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}