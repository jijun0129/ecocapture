package com.example.ecocapture

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TextResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.text_result)

        // UI 컴포넌트 초기화 및 레이아웃의 요소와 연결
        val textViewResult: TextView = findViewById(R.id.textViewResult)

        // Intent에서 전달된 데이터 수신
        val responseText = intent.getStringExtra("responseText")

        // 결과를 TextView에 표시
        textViewResult.text = responseText ?: "결과가 없습니다."
    }
}
