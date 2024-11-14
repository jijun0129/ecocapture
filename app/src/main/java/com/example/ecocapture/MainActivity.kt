package com.example.ecocapture

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // 사용자 입력을 위한 EditText, 생성 버튼, 응답을 표시할 TextView UI 컴포넌트 선언
    private lateinit var editTextPrompt: EditText
    private lateinit var buttonGenerate: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 컴포넌트 초기화 및 레이아웃의 요소들과 연결
        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonGenerate = findViewById(R.id.buttonGenerate)
        textViewResponse = findViewById(R.id.textViewResponse)

        // 버튼 클릭 리스너 설정
        buttonGenerate.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            // 사용자가 입력한 내용이 비어있지 않은지 확인
            if (prompt.isNotBlank()) {
                // 입력이 있으면 생성 모델을 사용해 콘텐츠 생성 함수 호출
                generateContentWithGenerativeModel(prompt)
            } else {
                // 입력이 없으면 텍스트뷰에 오류 메시지 표시
                textViewResponse.text = "질문을 입력해 주세요."
            }
        }
    }

    // 생성 모델을 사용해 콘텐츠 생성
    private fun generateContentWithGenerativeModel(prompt: String) {
        // 비동기 작업을 위해 IO 스레드에서 코루틴 실행
        CoroutineScope(Dispatchers.IO).launch {
            // GenerativeModel 객체 생성 및 API 키 설정
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "Your API Key" //본인의 제미나이 API 코드 입력
            )

            // 모델을 사용해 콘텐츠 생성
            val response = generativeModel.generateContent(prompt)

            // UI 스레드에서 응답을 텍스트뷰에 표시
            runOnUiThread {
                textViewResponse.text = response.text
            }
        }
    }
}
