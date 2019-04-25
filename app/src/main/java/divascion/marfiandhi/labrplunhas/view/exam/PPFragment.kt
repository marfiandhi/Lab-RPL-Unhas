package divascion.marfiandhi.labrplunhas.view.exam

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.presenter.PresenterNilai
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.fragment_pp.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import java.lang.IllegalStateException
import java.util.*

class PPFragment : Fragment(), NilaiView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var nDatabase: DatabaseReference
    private lateinit var presenterNilai: PresenterNilai
    private lateinit var nilai: Nilai
    private lateinit var user: User
    private lateinit var year: String
    private var register: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelable("user")!!
        return inflater.inflate(R.layout.fragment_pp, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance().reference
        nDatabase = FirebaseDatabase.getInstance().getReference("peserta")

        nilai = Nilai()
        nilai.nim = user.nim

        presenterNilai = PresenterNilai(nDatabase, nilai, this)

        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()

        checkRegistry()

        pp_register.setOnClickListener { _ ->
            alert("Are you sure you want to register to this class?") {
                yesButton { but ->
                    user.pp = true
                    but.dismiss()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            presenterNilai.registerCourse(mUser.uid, "pp", year)
                            checkRegistry()
                        }
                        .addOnFailureListener {
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
        chp_1.setOnClickListener {
            if(register) {
                //TODO getUserKey()
                exam("pp", "bab1", nilai.attempt1)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_2.setOnClickListener {
            if(register) {
                exam("pp", "bab2", nilai.attempt2)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_3.setOnClickListener {
            if(register) {
                exam("pp", "bab3", nilai.attempt3)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_4.setOnClickListener {
            if(register) {
                exam("pp", "bab4", nilai.attempt4)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_5.setOnClickListener {
            if(register) {
                exam("pp", "bab5", nilai.attempt5)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_6.setOnClickListener {
            if(register) {
                exam("pp", "bab6", nilai.attempt6)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_7.setOnClickListener {
            if(register) {
                exam("pp", "bab7", nilai.attempt7)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        chp_8.setOnClickListener {
            if(register) {
                exam("pp", "bab8", nilai.attempt8)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
    }

    private fun exam(subject: String, chapter: String, attempt: Int) {
        startActivity(intentFor<ExamActivity>(
            "subject" to subject,
            "chapter" to chapter,
            "user" to user.name,
            "attempt" to attempt,
            "nim" to user.nim,
            "nilai" to nilai))
    }

    private fun checkRegistry() {
        if(user.pp!!) {
            pp_1.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_2.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_3.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_4.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_5.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_6.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_7.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pp_8.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )

            pp_register.visibility = View.GONE

            presenterNilai.getSingleNilai(mUser.uid, "pp", this.year)
            register = true
        } else {
            pp_register.visibility = View.VISIBLE
            register = false
        }
    }

    override fun getData(nilai: Nilai) {
        this.nilai = nilai
        try {
            if(nilai.attempt1>0) {
                pp_1.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt2>0) {
                pp_2.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt3>0) {
                pp_3.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt4>0) {
                pp_4.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt5>0) {
                pp_5.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt6>0) {
                pp_6.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt7>0) {
                pp_7.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt8>0) {
                pp_8.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
        } catch(e: IllegalStateException) {
            Log.e("PP Button", e.stackTrace.toString())
        }
    }

    override fun showLoading() {
        dialog = indeterminateProgressDialog("Please Wait..")
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun hideLoading(i: Int, t: String) {
        when(i) {
            3 -> toast("Canceled. $t").show()
            2 -> toast("Failed to Connect, try again. $t\"").show()
            1 -> toast("There is no such data. $t\"").show()
        }
        dialog.dismiss()
    }

    private fun getUserKey() {
        var editText: EditText? = null
        alert {
            title = "Enter key to continue"
            customView {
                relativeLayout {
                    editText = editText {
                        maxLines = 1
                        ems  = 7
                        hint = "Chapter key..."
                    }.lparams{
                        margin = dip(5)
                        gravity = Gravity.CENTER
                    }
                }
            }
            onCancelled {
                toast("Can't enter without key..")
            }
            positiveButton("Enter") {
                validateKey(editText!!.text.toString())
            }
        }.show()
    }

    private fun validateKey(key: String) {
        //TODO request to database and make sure the key is same
    }

}
