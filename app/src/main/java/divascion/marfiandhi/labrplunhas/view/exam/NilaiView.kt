package divascion.marfiandhi.labrplunhas.view.exam

import divascion.marfiandhi.labrplunhas.model.Nilai

interface NilaiView {
    fun getData(nilai: Nilai)
    fun showLoading()
    fun hideLoading(i: Int, t: String)
}