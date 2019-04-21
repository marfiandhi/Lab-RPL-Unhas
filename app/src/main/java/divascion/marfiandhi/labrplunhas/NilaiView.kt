package divascion.marfiandhi.labrplunhas

import divascion.marfiandhi.labrplunhas.model.Nilai

interface NilaiView {
    fun getData(nilai: Nilai)
    fun showLoading()
    fun hideLoading(i: Int, t: String)
}