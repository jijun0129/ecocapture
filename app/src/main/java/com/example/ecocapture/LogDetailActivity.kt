package com.example.ecocapture

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocapture.databinding.ActivityLogDetailBinding

class LogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent로 전달된 데이터 받기
        val imageBytes = intent.getByteArrayExtra("image")
        val searchText = intent.getStringExtra("searchText")
        val resultText = intent.getStringExtra("resultText")

        // 데이터 설정
        if (imageBytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.imageViewDetail.setImageBitmap(bitmap)
            binding.imageViewDetail.visibility = View.VISIBLE
        } else {
            binding.imageViewDetail.visibility = View.GONE
        }

        binding.textSearchDetail.text = searchText ?: "입력 텍스트가 없습니다."
        binding.textResultDetail.text = resultText
    }
}
