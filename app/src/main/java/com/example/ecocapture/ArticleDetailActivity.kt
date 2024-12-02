package com.example.ecocapture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ecocapture.databinding.ActivityArticleDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleDetailBinding
    private val webCrawler = WebCrawler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val title = intent.getStringExtra("article_title") ?: "제목 없음"
        val link = intent.getStringExtra("article_link") ?: "링크 없음"

        binding.titleTextView.text = title
        binding.linkTextView.text = link

        // 링크 클릭 시 브라우저로 이동
        binding.linkTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }

        // 기사 본문 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val content = webCrawler.fetchArticleContent(link)
            withContext(Dispatchers.Main) {
                binding.webView.settings.javaScriptEnabled = true // JavaScript 활성화
                binding.webView.loadData(content, "text/html", "UTF-8") // HTML 본문 로드
            }
        }
    }
}