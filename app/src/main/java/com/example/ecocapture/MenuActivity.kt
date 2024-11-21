package com.example.ecocapture

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.ecocapture.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity()
{
    private lateinit var webCrawler: WebCrawler
    lateinit var binding: ActivityMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initEvents()
        initNews()
    }

    private fun initEvents()
    {
        binding.btnImage.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            startActivity(intent)
        }

        binding.btnText.setOnClickListener {
            val intent = Intent(this, TextActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initNews() {
        webCrawler = WebCrawler()

        // WebCrawler의 LiveData를 관찰하여 데이터가 변경될 때 UI를 갱신
        webCrawler.articlesLiveData.observe(this, Observer { articles ->

            // 랜덤한 기사 선택
            if (articles.isNotEmpty()) {
                val randomArticle = articles.random() // 랜덤으로 하나 선택
                binding.randomTitleTextView.text = randomArticle.first // 기사 제목 표시
                binding.randomTitleTextView.setOnClickListener {
                    val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                        putExtra("article_title", randomArticle.first)
                        putExtra("article_link", randomArticle.second)
                    }
                    startActivity(intent)
                }
            }
        })
        // 기사 데이터 가져오기
        webCrawler.fetchArticles()
    }
}