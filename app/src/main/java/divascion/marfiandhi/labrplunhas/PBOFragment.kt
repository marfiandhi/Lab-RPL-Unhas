package divascion.marfiandhi.labrplunhas


import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.fragment_pbo.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton


class PBOFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelable("user")!!
        return inflater.inflate(R.layout.fragment_pbo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance().reference

        checkRegistry()

        pbo_register.setOnClickListener { _ ->
            alert("Are you sure you want to register to this class?") {
                yesButton { but ->
                    user.pbo = true
                    dialog = indeterminateProgressDialog("Please wait...")
                    dialog.setCancelable(false)
                    but.dismiss()
                    dialog.show()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            dialog.dismiss()
                            checkRegistry()
                        }
                        .addOnFailureListener {
                            dialog.dismiss()
                            alert("Failed. Try again.").show()
                            user.pbo = false
                            checkRegistry()
                        }
                }
                noButton {
                    it.dismiss()
                    checkRegistry()
                }
            }.show()
        }
        pbo_1.setOnClickListener {
            toast("1")
        }
        pbo_2.setOnClickListener {
            toast("2")
        }
        pbo_3.setOnClickListener {
            toast("3")
        }
        pbo_4.setOnClickListener {
            toast("4")
        }
        pbo_5.setOnClickListener {
            toast("5")
        }
        pbo_6.setOnClickListener {
            toast("6")
        }
        pbo_7.setOnClickListener {
            toast("7")
        }
        pbo_8.setOnClickListener {
            toast("8")
        }
    }

    private fun checkRegistry() {
        if(user.pbo!!) {
            pbo_1.isEnabled = true
            pbo_2.isEnabled = true
            pbo_3.isEnabled = true
            pbo_4.isEnabled = true
            pbo_5.isEnabled = true
            pbo_6.isEnabled = true
            pbo_7.isEnabled = true
            pbo_8.isEnabled = true

            pbo_1.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_2.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_3.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_4.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_5.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_6.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_7.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_8.setTextColor(Color.parseColor("#FFFFFF"))

            pbo_register.visibility = View.GONE
        } else {
            pbo_1.isEnabled = false
            pbo_2.isEnabled = false
            pbo_3.isEnabled = false
            pbo_4.isEnabled = false
            pbo_5.isEnabled = false
            pbo_6.isEnabled = false
            pbo_7.isEnabled = false
            pbo_8.isEnabled = false

            pbo_register.visibility = View.VISIBLE
        }
    }

}
