package divascion.marfiandhi.labrplunhas

import divascion.marfiandhi.labrplunhas.model.User

interface MainView {
    fun getData(user: User)
    fun showLoading()
    fun hideLoading(i: Int, t: String)
}