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
import kotlinx.android.synthetic.main.fragment_pp.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton


class PPFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelable("user")!!
        return inflater.inflate(R.layout.fragment_pp, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance().reference

        checkRegistry()

        pp_register.setOnClickListener { _ ->
            alert("Are you sure you want to register to this class?") {
                yesButton { but ->
                    user.pp = true
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
                            user.pp = false
                            checkRegistry()
                        }
                }
                noButton {
                    it.dismiss()
                    checkRegistry()
                }
            }.show()
        }
        pp_1.setOnClickListener {
            toast("1")
        }
        pp_2.setOnClickListener {
            toast("2")
        }
        pp_3.setOnClickListener {
            toast("3")
        }
        pp_4.setOnClickListener {
            toast("4")
        }
        pp_5.setOnClickListener {
            toast("5")
        }
        pp_6.setOnClickListener {
            toast("6")
        }
        pp_7.setOnClickListener {
            toast("7")
        }
        pp_8.setOnClickListener {
            toast("8")
        }
    }

    private fun checkRegistry() {
        if(user.pp!!) {
            pp_1.isEnabled = true
            pp_2.isEnabled = true
            pp_3.isEnabled = true
            pp_4.isEnabled = true
            pp_5.isEnabled = true
            pp_6.isEnabled = true
            pp_7.isEnabled = true
            pp_8.isEnabled = true

            pp_1.setTextColor(Color.parseColor("#FFFFFF"))
            pp_2.setTextColor(Color.parseColor("#FFFFFF"))
            pp_3.setTextColor(Color.parseColor("#FFFFFF"))
            pp_4.setTextColor(Color.parseColor("#FFFFFF"))
            pp_5.setTextColor(Color.parseColor("#FFFFFF"))
            pp_6.setTextColor(Color.parseColor("#FFFFFF"))
            pp_7.setTextColor(Color.parseColor("#FFFFFF"))
            pp_8.setTextColor(Color.parseColor("#FFFFFF"))

            pp_register.visibility = View.GONE
        } else {
            pp_1.isEnabled = false
            pp_2.isEnabled = false
            pp_3.isEnabled = false
            pp_4.isEnabled = false
            pp_5.isEnabled = false
            pp_6.isEnabled = false
            pp_7.isEnabled = false
            pp_8.isEnabled = false

            pp_register.visibility = View.VISIBLE
        }
    }
}
