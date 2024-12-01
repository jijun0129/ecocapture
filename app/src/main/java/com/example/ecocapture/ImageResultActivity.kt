package com.example.ecocapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecocapture.databinding.ActivityImageResultBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        if (base64Image != null)
        {
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val image: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            setImage(image)
            generateContentWithGenerativeModel(image) // GenerativeModel을 사용하여 컨텐츠 생성
        }
    }

    private fun generateContentWithGenerativeModel(image: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
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