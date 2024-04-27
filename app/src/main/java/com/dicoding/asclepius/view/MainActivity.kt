package com.dicoding.asclepius.view


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.databinding.ActivityMainBinding
import java.io.File
import com.yalantis.ucrop.UCrop


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage()
                moveToResult()
            }
        }
        binding.previewImageView.setOnClickListener {
            currentImageUri?.let { uri ->
                startUcrop(uri)
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                currentImageUri = uri
                showImage()
                //Ucrop
                startUcrop(uri)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                currentImageUri = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(data!!)
            error?.let {
                Log.e(TAG, "UCrop Error: $it")
                showToast("Failed Crop Image")
            }
        }
    }

    private fun startUcrop(uri: Uri) {
        val destinationFileName = "cropped_image"
        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "$destinationFileName.jpg")))
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(1000, 1000)
        uCrop.start(this)
    }


    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        // Clear Cache Image untuk Mengambil Crop Baru
        binding.previewImageView.setImageURI(null)

        currentImageUri?.let { uri ->
            Log.e(TAG, "Display Image: $uri")
            binding.previewImageView.setImageURI(uri)
        }
    }


    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        val intent = Intent(this, ResultActivity::class.java)
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ImagePicker"
    }
}
