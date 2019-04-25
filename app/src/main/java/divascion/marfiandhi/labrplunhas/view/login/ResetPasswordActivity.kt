package divascion.marfiandhi.labrplunhas.view.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import divascion.marfiandhi.labrplunhas.R
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.jetbrains.anko.*

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        reset_password_button.setOnClickListener {
            reset()
        }
    }

    private fun reset() {
        if(!validateForm()) {
            return
        }

        alert("Are you sure you want to reset your password?") {
            yesButton { _ ->
                val dialog = indeterminateProgressDialog("Please wait...")
                dialog.setCancelable(false)
                dialog.show()
                try {
                    auth = FirebaseAuth.getInstance()

                    val email = email_reset_form.text.toString()

                    auth.sendPasswordResetEmail(email).addOnCompleteListener { it ->
                        if(it.isSuccessful) {
                            dialog.dismiss()
                            longToast("Reset password confirmation sent. Please check your inbox: $email")
                            finish()
                        } else {
                            dialog.dismiss()
                            toast("Failed, there is no user with email: $email")
                        }
                    }
                } catch(e: Exception) {
                    dialog.dismiss()
                    toast("Failed, caused by ${e.stackTrace}")
                }
            }
            noButton {
                it.dismiss()
            }
        }.show().setCancelable(false)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val username = email_reset_form.text.toString()
        if (TextUtils.isEmpty(username)) {
            email_reset_form.error = "Required."
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
                email_reset_form.error = null
            } else {
                email_reset_form.error = "It's not a valid email"
                valid = false
            }
        }

        return valid
    }
}
