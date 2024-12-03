package com.example.ecocapture

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocapture.databinding.ActivityTextResultBinding

class TextResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTextResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Intent에서 전달된 데이터 수신
        val responseText = intent.getStringExtra("responseText")

        // 결과를 TextView에 표시
        binding.textViewResult.text = responseText ?: "결과가 없습니다."

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // 뒤로가기 버튼 클릭 시 현재 액티비티 종료
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
