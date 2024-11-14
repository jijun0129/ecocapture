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
    private lateinit var editTextPrompt: EditText
    private lateinit var buttonGenerate: Button
    private lateinit var textViewResponse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        editTextPrompt = findViewById(R.id.editTextPrompt)
        buttonGenerate = findViewById(R.id.buttonGenerate)
        textViewResponse = findViewById(R.id.textViewResponse)

        // Set click listener for the button
        buttonGenerate.setOnClickListener {
            val prompt = editTextPrompt.text.toString()
            if (prompt.isNotBlank()) {
                generateContentWithGenerativeModel(prompt)
            } else {
                textViewResponse.text = "Please enter a question."
            }
        }
    }

    private fun generateContentWithGenerativeModel(prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBXY45dfxtzWv86augDtxgt86X69p4h3nI"
            )

            val response = generativeModel.generateContent(prompt)

            // Update the response in the UI thread
            runOnUiThread {
                textViewResponse.text = response.text
            }
        }
    }
}
