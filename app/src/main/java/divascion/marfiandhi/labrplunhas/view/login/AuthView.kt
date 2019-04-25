package divascion.marfiandhi.labrplunhas.view.login

import com.google.firebase.auth.FirebaseUser

interface AuthView {
    fun showLoading()
    fun hideLoading()
    fun onDone(user: FirebaseUser)
    fun updateUI(currentUser: FirebaseUser?, new: Boolean)
}