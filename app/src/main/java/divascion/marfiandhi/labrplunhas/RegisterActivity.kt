package divascion.marfiandhi.labrplunhas

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mDatabase: DatabaseReference
    private var delete = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mDatabase = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        create_account.setOnClickListener {
            createAccount()
        }
        longSnackbar(register_view, "Please verify your email. Check inbox in ${mUser.email}", "Click to go to email") {
            val i = Intent(Intent.ACTION_MAIN)
            i.addCategory(Intent.CATEGORY_APP_EMAIL)
            startActivity(Intent.createChooser(i, "Select Mail App"))
        }
        radio_male.isChecked = true
    }

    override fun onStop() {
        try {
            if(delete) {
                mUser.delete()
                auth.signOut()
            }
        } catch(e: Exception) {
            Log.e("Error deleting", e.stackTrace.toString())
        }
        super.onStop()
    }

    private fun createAccount() {
        if(!validateForm()) {
            return
        }
        val name = name_form.text.toString()
        val nim = nim_form.text.toString()
        val nick = display_name_form.text.toString()
        val male = radio_male.isChecked
        val user = User(
            mUser.email,
            name,
            nim,
            false,
            false,
            male,
            "member"
        )

        val key = intent.getStringExtra("pw")

        try {
            val dialog = indeterminateProgressDialog("Please wait...")
            dialog.setCancelable(false)
            dialog.show()
            val requestUpdateProfile = if(male) {
                UserProfileChangeRequest.Builder()
                    .setDisplayName(nick)
                    .setPhotoUri(Uri.parse("${BuildConfig.BASE_URL_PROFILE_PIC}${BuildConfig.URL_PROFILE_MAN}")).build()
            } else {
                UserProfileChangeRequest.Builder()
                    .setDisplayName(nick)
                    .setPhotoUri(Uri.parse("${BuildConfig.BASE_URL_PROFILE_PIC}${BuildConfig.URL_PROFILE_WOMAN}")).build()
            }
            mUser.updateProfile(requestUpdateProfile)
                .addOnSuccessListener {_ ->
                    val credentials = EmailAuthProvider.getCredential(user.email.toString(), key)
                    mUser.reauthenticateAndRetrieveData(credentials)
                        .addOnSuccessListener {

                        }.addOnFailureListener {
                            toast(it.stackTrace.toString())
                        }
                }.addOnFailureListener{
                    toast(it.stackTrace.toString())
                }

            mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dialog.dismiss()
                    toast(p0.message)
                    return
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("user").hasChild(mUser.uid)) {
                        dialog.dismiss()
                        toast("Error, there is another user using this UID. Contact Administrator")
                        return
                    }else {
                        mDatabase.child("user").child(mUser.uid).setValue(user)
                            .addOnSuccessListener {
                                dialog.dismiss()
                                FirebaseAuth.getInstance().signOut()
                                delete = false
                                startActivity<LoginActivity>()
                                longToast("Successful registered.")
                                finish()
                            }
                            .addOnFailureListener {
                                delete = true
                                dialog.dismiss()
                                toast("Connection Error. Try again.")
                            }
                    }
                    dialog.dismiss()
                }
            })
        } catch(e: Exception) {
            toast(e.stackTrace.toString())
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = name_form.text.toString()
        if (TextUtils.isEmpty(name)) {
            name_form.error = "Required."
            valid = false
        } else {
            name_form.error = null
        }

        val nim = nim_form.text.toString()
        if (TextUtils.isEmpty(nim)) {
            nim_form.error = "Required."
            valid = false
        } else {
            nim_form.error = null
        }

        val nick = display_name_form.text.toString()
        if(TextUtils.isEmpty(nick)) {
            display_name_form.error = "Required"
            valid = false
        } else {
            display_name_form.error = null
        }

        return valid
    }

    override fun onBackPressed() {
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        alert("Cancelling this progress may affect to deleting your current account. Continue?") {
            yesButton {
                delete = true
                mUser.delete()
                auth.signOut()
                longToast("Canceled register.")
                startActivity<LoginActivity>()
                finish()
            }
            noButton {
                delete = false
                it.dismiss()
            }
        }.show()
    }
}
