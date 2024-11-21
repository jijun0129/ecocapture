package com.example.ecocapture

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextActivity : AppCompatActivity() {

    private lateinit var editTextPrompt: EditText
    private lateinit var buttonGenerate: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonGenerate = findViewById(R.id.buttonGenerate)
        textViewResponse = findViewById(R.id.textViewResponse)

        buttonGenerate.setOnClickListener {
            // 로컬 변수로 prompt 선언
            var prompt = editTextPrompt.text.toString().trim()

            if (prompt.isNotBlank()) {
                // 입력값이 단순한 제품명이라면 질문 형식으로 변환
                if (!prompt.contains("?") && !prompt.contains("어떻게")) {
                    prompt = "$prompt 의 분리수거 방법을 알려줘"
                }
                generateContentWithGenerativeModel(prompt) // 올바른 prompt 전달
            } else {
                textViewResponse.text = "질문을 입력해 주세요."
            }
        }
    }

    private fun generateContentWithGenerativeModel(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = ApiKey.API_KEY
            )

            val response = generativeModel.generateContent(prompt)

            runOnUiThread {
                val intent = Intent(this@TextActivity, TextResultActivity::class.java)
                intent.putExtra("responseText", response.text)
                startActivity(intent)
            }
        }
    }
}
