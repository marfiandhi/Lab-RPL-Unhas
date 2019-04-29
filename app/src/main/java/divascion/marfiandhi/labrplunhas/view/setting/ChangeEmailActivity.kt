@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.setting

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import divascion.marfiandhi.labrplunhas.R
import kotlinx.android.synthetic.main.activity_change_email.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.change_email)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.dismiss()
        change_email_button.setOnClickListener {
            changeEmail()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validation():Boolean {
        var valid = true
        val email = new_email_edit.text.toString()
        if(TextUtils.isEmpty(email)) {
           valid = false
            new_email_edit.error = getString(R.string.required)
        } else {
            new_email_edit.error = null
        }

        val pw = email_password_confirmation_edit.text.toString()
        if(TextUtils.isEmpty(pw)) {
            valid = false
            email_password_confirmation_edit.error = getString(R.string.required)
        } else {
            email_password_confirmation_edit.error = null
        }
        return valid
    }

    private fun changeEmail() {
        if(!validation()) {
            return
        }
        alert(getString(R.string.change_email_alert_message)) {
            title = getString(R.string.change_email_confirmation)
            positiveButton(getString(R.string.continue_text)) {
                doChangeEmail()
            }
            cancelButton {
                it.dismiss()
            }
        }.show()
    }

    private fun doChangeEmail() {
        val email = new_email_edit.text.toString()
        val pw = email_password_confirmation_edit.text.toString()
        showLoading()
        val timer = object : CountDownTimer(15000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                Log.e("Connecting", "${(millisUntilFinished/1000)%60}")
            }

            override fun onFinish() {
                toast(getString(R.string.try_again))
                hideLoading()
                return
            }
        }
        timer.start()
        val credential = EmailAuthProvider
            .getCredential(mUser.email.toString(), pw)
        mUser.reauthenticate(credential)
            ?.addOnSuccessListener {
                mUser.updateEmail(email)
                    .addOnSuccessListener { _->
                        timer.cancel()
                        mAuth.signInWithEmailAndPassword(email, pw)
                            .addOnSuccessListener {it ->
                                timer.cancel()
                                timer.start()
                                mUser = it.user
                                mUser.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        timer.cancel()
                                        if (task.isSuccessful) {
                                            hideLoading()
                                            toast(getString(R.string.email_changed))
                                            finish()
                                        }
                                    }
                                mUser = it.user
                                hideLoading()
                                toast(getString(R.string.email_changed))
                                finish()
                            }
                    }.addOnFailureListener { _ ->
                        toast("${getString(R.string.try_again)} ${getString(R.string.wrong_password)}")
                        hideLoading()
                    }
            }
            ?.addOnFailureListener{
                toast("${getString(R.string.try_again)} ${getString(R.string.wrong_password)}")
                hideLoading()
            }
    }

    private fun showLoading() {
        dialog.show()
    }

    private fun hideLoading() {
        dialog.dismiss()
    }
}
