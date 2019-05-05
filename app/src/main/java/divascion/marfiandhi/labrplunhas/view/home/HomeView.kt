package divascion.marfiandhi.labrplunhas.view.home

import divascion.marfiandhi.labrplunhas.model.News

interface HomeView {
    fun getData(news: List<News>, category: String)
    fun hideLoading()
    fun showLoading()
}