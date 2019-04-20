package divascion.marfiandhi.labrplunhas

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var username: String
    private lateinit var password: String
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

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
        val currentUser = auth.currentUser
        updateUI(currentUser, false)
    }

    private fun updateUI(currentUser: FirebaseUser?, new: Boolean) {
        hideProgressDialog()
        if(currentUser != null && new) {
            currentUser.sendEmailVerification()
            startActivity(intentFor<RegisterActivity>())
        }
        else if (currentUser != null) {
            startActivity(intentFor<MainActivity>())
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val username = username_form.text.toString()
        if (TextUtils.isEmpty(username)) {
            username_form.error = "Required."
            valid = false
        } else {
            username_form.error = null
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
        showProgressDialog()
        username = username_form.text.toString()
        password = password_form.text.toString()
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user, true)
                } else {
                    Toast.makeText(this, task.result.toString(), Toast.LENGTH_SHORT).show()
                    updateUI(null, false)
                }
                hideProgressDialog()
            }
    }

    private fun signIn() {
        confirm_password_view.visibility = View.GONE
        if (!validateForm()) {
            return
        }
        showProgressDialog()
        username = username_form.text.toString()
        password = password_form.text.toString()
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user, false)
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null, false)
                }
                hideProgressDialog()
            }
    }

    private fun showProgressDialog() {
        dialog =  indeterminateProgressDialog("Please wait..")
        dialog?.show()
        dialog?.setCancelable(false)
    }

    private fun hideProgressDialog() {
        dialog?.dismiss()
    }
}
