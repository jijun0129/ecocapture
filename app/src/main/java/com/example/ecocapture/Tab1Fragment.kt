package com.example.ecocapture

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.ecocapture.databinding.FragmentTab1Binding

class Tab1Fragment : Fragment() {

    private lateinit var webCrawler: WebCrawler
    private var _binding: FragmentTab1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab1Binding.inflate(inflater, container, false)
        initEvents()
        initNews()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initEvents() {
        binding.btnImage.setOnClickListener {
            val intent = Intent(requireContext(), ImageActivity::class.java)
            startActivity(intent)
        }

        binding.btnText.setOnClickListener {
            val intent = Intent(requireContext(), TextActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initNews() {
        webCrawler = WebCrawler()

        // WebCrawler의 LiveData를 관찰하여 데이터가 변경될 때 UI를 갱신
        webCrawler.articlesLiveData.observe(viewLifecycleOwner, Observer { articles ->

            // 랜덤한 기사 선택
            if (articles.isNotEmpty()) {
                val randomArticle = articles.random() // 랜덤으로 하나 선택
                binding.randomTitleTextView.text = randomArticle.first // 기사 제목 표시
                binding.randomTitleTextView.setOnClickListener {
                    val intent = Intent(requireContext(), ArticleDetailActivity::class.java).apply {
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
