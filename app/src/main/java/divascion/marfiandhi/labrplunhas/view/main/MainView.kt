package divascion.marfiandhi.labrplunhas.view.main

import divascion.marfiandhi.labrplunhas.model.User

interface MainView {
    fun getData(user: User)
    fun showLoading()
    fun hideLoading(i: Int, t: String)
}