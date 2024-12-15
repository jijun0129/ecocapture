package com.example.ecocapture

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecocapture.databinding.ActivityTextBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 초기화
        binding = ActivityTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 버튼 클릭 리스너 설정
        binding.buttonGenerate.setOnClickListener {
            val prompt = binding.editTextPrompt.text.toString().trim()

            if (prompt.isNotBlank()) {
                // 입력값이 단순한 제품명이라면 질문 형식으로 변환
                val formattedPrompt = if (!prompt.contains("?") && !prompt.contains("어떻게")) {
                    """
                        "$prompt" 에 대한 분리배출 방법을 아래의 형식을 따르며 친근한 존댓말로, 구체적으로 설명하라.
                        텍스트 꾸밈(예: **, -, 등)을 사용하지 않는다.
                        2024년 4분기 대한민국의 일반적인 정책을 따른다.
                        
                        재활용: (가능, 불가능을 문장으로. 네, 아니요는 하지 않음)
                        분리배출 방법: (번호.를 매겨서 설명)
                        주의 사항:
                    """.trimIndent()
                } else {
                    prompt
                }

                // 로딩 메시지 표시
                binding.textViewResponse.text = "결과를 불러오고 있습니다..."
                binding.buttonGenerate.isEnabled = false // 버튼 비활성화
                generateContentWithGenerativeModel(prompt, formattedPrompt)
            } else {
                binding.textViewResponse.text = "질문을 입력해 주세요."
            }
        }
    }

    private fun generateContentWithGenerativeModel(prompt: String, formattedPrompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // GenerativeModel 생성 및 API 호출
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = ApiKey.API_KEY
                )
                val response = generativeModel.generateContent(formattedPrompt)

                // 결과를 UI 스레드에서 처리
                runOnUiThread {
                    val responseText = response.text

                    // 데이터베이스에 저장
                    val dbHelper = MyDatabaseHelper(this@TextActivity)
                    dbHelper.insertResult(
                        image = null, // 현재 이미지는 null로 처리 (추가 가능)
                        searchText = prompt,
                        resultText = responseText ?: ""
                    )

                    // 결과를 TextResultActivity로 전달
                    val intent = Intent(this@TextActivity, TextResultActivity::class.java)
                    intent.putExtra("responseText", responseText)

                    // 버튼 활성화 및 액티비티 이동
                    binding.buttonGenerate.isEnabled = true
                    startActivity(intent)
                }
            } catch (e: Exception) {
                // 오류 처리
                runOnUiThread {
                    binding.textViewResponse.text = "오류가 발생했습니다: ${e.message}"
                    binding.buttonGenerate.isEnabled = true // 버튼 다시 활성화
                }
            }
        }
    }

}
