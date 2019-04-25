package divascion.marfiandhi.labrplunhas.view.login

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*

class LoginActivity : AppCompatActivity(), AuthView {

    private lateinit var auth: FirebaseAuth
    private lateinit var username: String
    private lateinit var password: String
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        dialog =  indeterminateProgressDialog("Please wait..")
        dialog?.dismiss()

        sign_in_button.setOnClickListener{
            signIn()
        }

        view_forgot_password.setOnClickListener{
            startActivity<ResetPasswordActivity>()
        }

        sign_up_button.setOnClickListener {
            signUp()
        }

        confirm_password_view.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                validatePassword()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        updateUI(currentUser, false)
    }

    override fun updateUI(currentUser: FirebaseUser?, new: Boolean) {
        if(currentUser != null && new) {
            currentUser.sendEmailVerification()
            startActivity(intentFor<RegisterActivity>("pw" to password))
            updateUI(null, false)
            finish()
        }
        else if (currentUser != null) {
            startActivity(intentFor<MainActivity>())
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val username = username_form.text.toString()
        if (TextUtils.isEmpty(username)) {
            username_form.error = "Required."
            valid = false
        } else {
            val email = username.toCharArray()
            var charAt = false
            var charDot = false
            for(index in email.indices) {
                if(email[index]=='@') {
                    charAt = when(charAt) {
                        true -> false
                        false -> true
                    }
                }
                if(email[index]=='.') {
                    charDot = true
                }
            }
            if(charAt && charDot) {
                username_form.error = null
            } else {
                username_form.error = "It's not a valid email"
                valid = false
            }
        }

        val password = password_form.text.toString()
        if (TextUtils.isEmpty(password)) {
            password_form.error = "Required."
            valid = false
        } else {
            password_form.error = null
        }

        return valid
    }

    private fun confirmPassword(): Boolean {
        var valid = true

        val password = confirm_password_form.text.toString()
        if (TextUtils.isEmpty(password)) {
            confirm_password_form.error = "Required."
            valid = false
        } else {
            confirm_password_form.error = null
        }
        return valid
    }

    private fun validatePassword():Boolean {
        var valid = true

        val password = password_form.text.toString()
        val conPassword = confirm_password_form.text.toString()
        if(password == conPassword) {
            confirm_password_form.error = null
        } else {
            password_form.error = "Not same."
            confirm_password_form.error = "Not same."
            valid = false
        }
        return valid
    }

    private fun signUp() {
        confirm_password_view.visibility = View.VISIBLE
        if (!validateForm()) {
            return
        }
        if(!confirmPassword()) {
            return
        }
        if(!validatePassword()) {
            return
        }
        showLoading()
        username = username_form.text.toString()
        password = password_form.text.toString()
        try {
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user, true)
                    } else {
                        Toast.makeText(this, "Failed create user, there is already user with this email: $username", Toast.LENGTH_SHORT).show()
                        updateUI(null, false)
                    }
                    hideLoading()
                }
        } catch(e: Exception) {
            Toast.makeText(this, e.stackTrace.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        confirm_password_view.visibility = View.GONE
        if (!validateForm()) {
            return
        }
        showLoading()
        username = username_form.text.toString()
        password = password_form.text.toString()
        doAsync {
            uiThread {

            }
        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user, false)
                } else {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    updateUI(null, false)
                }
                hideLoading()
            }
    }

    override fun showLoading() {
        dialog?.setCancelable(false)
        dialog?.show()
    }

    override fun hideLoading() {
        dialog?.dismiss()
    }

    override fun onDone(user: FirebaseUser) {
        Log.e("haha","err")
    }
}
