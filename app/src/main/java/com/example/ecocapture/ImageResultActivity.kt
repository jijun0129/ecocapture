package com.example.ecocapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ecocapture.databinding.ActivityImageResultBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ImageResultActivity : AppCompatActivity()
{
    lateinit var binding: ActivityImageResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        initEvents()

        val base64Image = intent.getStringExtra("imageBase64")
        val imageUriString = intent.getStringExtra("imageUri")

        if (base64Image != null)
        {
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val image: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            setImage(image)
            generateContentWithGenerativeModel(image) // GenerativeModel을 사용하여 컨텐츠 생성
        }
        else if (imageUriString != null)
        {
            val imageUri = Uri.parse(imageUriString)

            lifecycleScope.launch {
                try {
                    val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                    else
                    {
                        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    }

                    setImage(image)
                    generateContentWithGenerativeModel(image) // GenerativeModel을 사용하여 컨텐츠 생성

                }
                catch (e: Exception)
                {
                    Toast.makeText(this@ImageResultActivity, "이미지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun saveResultToDatabase(image: Bitmap, resultText: String) {
        val dbHelper = MyDatabaseHelper(this)

        // Bitmap -> ByteArray 변환
        val imageByteArray = getBitmapAsByteArray(image)

        // 입력 텍스트는 null로 저장
        val searchText = ""

        // 데이터베이스에 저장
        dbHelper.insertResult(imageByteArray, searchText, resultText)
    }

    private fun generateContentWithGenerativeModel(image: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = ApiKey.API_KEY
                )

                val inputContent = content {
                    image(image)
                    text("이 이미지에 나온 물건 어떻게 분리수거 하는 지 알려줘.")
                }

                val response = generativeModel.generateContent(inputContent)

                // 생성된 내용을 UI에 반영하기 위해 UI 스레드로 전환
                runOnUiThread {
                    binding.textRecycle.setText(response.text) // 생성된 텍스트를 TextView에 설정

                    // 데이터 저장
                    saveResultToDatabase(image, response.text ?: "")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@ImageResultActivity, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    /*
    private fun initEvents()
    {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

     */

    private fun setImage(bitmapImage : Bitmap)
    {
        binding.image.setImageBitmap(bitmapImage)
    }
}