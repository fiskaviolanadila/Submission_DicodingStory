package com.dicoding.picodiploma.loginwithanimation.view.story

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.data.utils.uriToFile
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddStoryBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private var isUploading = false

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
            if (isSuccess) {
                Glide.with(this).load(currentImageUri).into(binding.ivStoryImage)
            } else {
                currentImageUri = null
                Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                Glide.with(this).load(uri).into(binding.ivStoryImage)
            } else {
                Toast.makeText(this, "Gagal mengambil gambar dari galeri", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.buttonAdd.setOnClickListener {
            if (!isUploading) {
                uploadStory()
            }
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 101)
            }
        }
    }

    private fun openCamera() {
        currentImageUri = com.dicoding.picodiploma.loginwithanimation.data.utils.createImageUri(this)
        cameraLauncher.launch(currentImageUri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString()
        if (currentImageUri != null && description.isNotEmpty()) {
            isUploading = true
            binding.buttonAdd.isEnabled = false

            lifecycleScope.launch {
                val userPreference = UserPreference.getInstance(applicationContext.dataStore)
                val user = userPreference.getSession().first()

                val file = uriToFile(currentImageUri!!, this@AddStoryActivity).reduceFileImage()
                val imagePart = MultipartBody.Part.createFormData(
                    "photo", file.name, file.asRequestBody("image/jpeg".toMediaType())
                )
                val descriptionPart = description.toRequestBody("text/plain".toMediaType())

                try {
                    val response = ApiConfig.getInstance().uploadStory("Bearer ${user.token}", descriptionPart, imagePart)
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddStoryActivity, "Berhasil mengunggah cerita", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddStoryActivity, "Gagal mengunggah cerita", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@AddStoryActivity, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    isUploading = false
                    binding.buttonAdd.isEnabled = true
                }
            }
        } else {
            Toast.makeText(this, "Harap isi deskripsi dan pilih gambar", Toast.LENGTH_SHORT).show()
        }
    }
}
