package com.sianpike.newszen

data class APIResult(val status: String, val totalResults: Int, val articles: List<NewsArticle>) {
}