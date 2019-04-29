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
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.change_password)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.dismiss()
        change_password_button.setOnClickListener {
            changePassword()
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
        val oldPw = old_password.text.toString()
        if(TextUtils.isEmpty(oldPw)) {
            valid = false
            old_password.error = getString(R.string.required)
        } else {
            old_password.error = null
        }

        val newPw = new_password.text.toString()
        if(TextUtils.isEmpty(newPw)) {
            valid = false
            new_password.error = getString(R.string.required)
        } else {
            new_password.error = null
        }

        val newConPw = confirm_password_change.text.toString()
        if(TextUtils.isEmpty(newConPw)) {
            valid = false
            confirm_password_change.error = getString(R.string.required)
        } else {
            confirm_password_change.error = null
        }

        if(newPw!=newConPw) {
            valid = false
            confirm_password_change.error = getString(R.string.not_same)
            new_password.error = getString(R.string.not_same)
        } else {
            confirm_password_change.error = null
            new_password.error = null
        }
        return valid
    }

    private fun changePassword() {
        if(!validation()) {
            return
        }
        alert(getString(R.string.change_password_alert_message)) {
            title = getString(R.string.change_password_confirmation)
            positiveButton(getString(R.string.continue_text)) {
                doChangePassword()
            }
            cancelButton {
                it.dismiss()
            }
        }.show()
    }

    private fun doChangePassword() {
        val pw = new_password.text.toString()
        val oldPw = old_password.text.toString()
        mUser = FirebaseAuth.getInstance().currentUser!!
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
            .getCredential(mUser.email.toString(), oldPw)
        mUser.reauthenticate(credential)
            ?.addOnSuccessListener {
                mUser.updatePassword(pw)
                    .addOnSuccessListener { _ ->
                        mAuth = FirebaseAuth.getInstance()
                        mUser = mAuth.currentUser!!
                        timer.cancel()
                        mAuth.signInWithEmailAndPassword(mUser.email.toString(), pw)
                            .addOnSuccessListener {it ->
                                mUser = it.user
                                hideLoading()
                                toast(getString(R.string.password_change))
                                finish()
                            }
                    }
                    .addOnFailureListener { _ ->
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
