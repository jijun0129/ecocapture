package com.example.ecocapture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WebCrawler {

    // LiveData를 통해 articles를 관찰할 수 있게 설정
    private val _articlesLiveData = MutableLiveData<List<Pair<String, String>>>()
    val articlesLiveData: LiveData<List<Pair<String, String>>> get() = _articlesLiveData

    fun fetchArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 웹페이지 가져오기
                val doc: Document = Jsoup.connect("https://www.hkbs.co.kr/news/articleList.html?sc_section_code=S1N1&view_type=sm")
                    .get()
                    .apply { outputSettings().charset("UTF-8") }

                // 기사 리스트 선택
                val articlesList = doc.select("ul.type2 > li")

                val articles = mutableListOf<Pair<String, String>>()
                for (article in articlesList) {
                    val title = article.select("h4.titles a").text()
                    val relativeLink = article.select("h4.titles a").attr("href")
                    val link = "https://www.hkbs.co.kr$relativeLink"

                    // 기사 정보를 리스트에 추가
                    articles.add(Pair(title, link))
                }

                // UI 스레드에서 LiveData 업데이트
                _articlesLiveData.postValue(articles)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchArticleContent(url: String): String {
        return try {
            val doc: Document = Jsoup.connect(url).get()

            // 기사 본문 추출
            var articleContent = doc.select("article#article-view-content-div").html()

            // 이미지 태그에 스타일 적용
            articleContent = articleContent.replace(
                "<img", "<img style=\"max-width:100%;height:auto;\""
            )

            articleContent
        } catch (e: Exception) {
            e.printStackTrace()
            "본문을 불러오는 데 실패했습니다."
        }
    }
}