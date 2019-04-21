package divascion.marfiandhi.labrplunhas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
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

                    auth.sendPasswordResetEmail(email).addOnCompleteListener {
                        if(it.isSuccessful) {
                            dialog.dismiss()
                            toast("Reset password sent. Please check your email inbox.").show()
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
            email_reset_form.error = null
        }

        return valid
    }
}
