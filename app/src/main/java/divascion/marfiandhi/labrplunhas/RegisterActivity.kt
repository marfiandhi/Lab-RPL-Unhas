package divascion.marfiandhi.labrplunhas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mDatabase = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        create_account.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        if(!validateForm()) {
            return
        }
        val name = name_form.text.toString()
        val nim = nim_form.text.toString()
        val user = User(
            mUser.email,
            name,
            nim,
            false,
            false,
            "member"
        )
        try {
            val dialog = indeterminateProgressDialog("Please wait...")
            dialog.setCancelable(false)
            dialog.show()
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
                                startActivity<MainActivity>()
                            }
                            .addOnFailureListener {
                                dialog.dismiss()
                                toast("Connection Error. Try again.")
                            }
                    }
                    dialog.dismiss()
                }
            })
        }catch(e: Exception) {
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

        return valid
    }

    override fun onBackPressed() {
        auth = FirebaseAuth.getInstance()
        mUser = auth.currentUser!!
        alert("Cancelling this progress may affect to deleting your current account. Continue?") {
            yesButton {
                mUser.delete()
                auth.signOut()
                startActivity<LoginActivity>()
            }
            noButton {
                it.dismiss()
            }
        }.show()
    }
}
