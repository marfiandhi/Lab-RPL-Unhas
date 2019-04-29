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

        alert(getString(R.string.reset_password_message)) {
            yesButton { _ ->
                val dialog = indeterminateProgressDialog(getString(R.string.please_wait))
                dialog.setCancelable(false)
                dialog.show()
                try {
                    auth = FirebaseAuth.getInstance()

                    val email = email_reset_form.text.toString()

                    auth.sendPasswordResetEmail(email).addOnCompleteListener { it ->
                        if(it.isSuccessful) {
                            dialog.dismiss()
                            longToast("${getString(R.string.reset_password_confirmation_prompt)} $email")
                            finish()
                        } else {
                            dialog.dismiss()
                            toast("${getString(R.string.failed_create_user_email)} $email")
                        }
                    }
                } catch(e: Exception) {
                    dialog.dismiss()
                    toast("${e.message}")
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
            email_reset_form.error = getString(R.string.required)
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
                email_reset_form.error = getString(R.string.its_not_valid_email)
                valid = false
            }
        }

        return valid
    }
}
