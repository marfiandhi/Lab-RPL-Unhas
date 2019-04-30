@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.setting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import divascion.marfiandhi.labrplunhas.BuildConfig
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import divascion.marfiandhi.labrplunhas.view.login.LoginActivity
import divascion.marfiandhi.labrplunhas.view.profile.EditProfileActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mUser: FirebaseUser
    private var LOCALE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mUser = FirebaseAuth.getInstance().currentUser!!
        user = intent.getParcelableExtra("user")
        settings_account_edit.setOnClickListener {
            if(mUser.isEmailVerified || user.role=="admin") {
                startActivity(intentFor<EditProfileActivity>("user" to user))
            } else {
                toast(getString(R.string.verify_email_prompt))
            }
        }
        settings_account_email.setOnClickListener {
            startActivity(intentFor<ChangeEmailActivity>())
        }
        settings_account_password.setOnClickListener {
            startActivity(intentFor<ChangePasswordActivity>())
        }
        settings_language.setOnClickListener {
            val localizationIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivityForResult(localizationIntent, LOCALE)
        }
        settings_others_logout.setOnClickListener { _ ->
            alert(getString(R.string.logout_confirmation)) {
                yesButton{_ ->
                    indeterminateProgressDialog(getString(R.string.please_wait)).show()
                    doAsync {
                        FirebaseAuth.getInstance().signOut()
                        uiThread {
                            startActivity(intentFor<LoginActivity>())
                            finish()
                        }
                    }
                }
                noButton {
                    it.dismiss()
                }
            }.show()
        }
        settings_others_app_version.setOnClickListener { _ ->
            val pInfo = packageManager.getPackageInfo(packageName,0)
            val version = pInfo.versionName
            alert("${getString(R.string.info_app)}\n\n\n${getString(R.string.my_email)}\n\n${BuildConfig.WHAT_CHANGE}") {
                title = version
                positiveButton(getString(R.string.got_it)) {
                    it.dismiss()
                }
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LOCALE) {
            restartActivity()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
}
