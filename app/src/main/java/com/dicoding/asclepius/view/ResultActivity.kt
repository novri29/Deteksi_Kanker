package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageStringUri = intent.getStringExtra(IMAGE_URI)
        if (imageStringUri != null) {
            val imageUri = Uri.parse(imageStringUri)
            displayImg(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener{
                    override fun onError(error: String) {
                        Log.e(TAG, "ERROR : $error")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        if (results != null) {
                            showResults(results)
                        } else {
                            Log.e(TAG, "No Results")
                        }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(imageUri)
        } else {
            Log.e(TAG, "No Image")
            finish()
        }
    }

    private fun displayImg(uri: Uri?) {
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.formatToString()}"
    }

    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "ImagePicker"
    }
}