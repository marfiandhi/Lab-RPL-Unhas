@file:Suppress("DEPRECATION")

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
import divascion.marfiandhi.labrplunhas.presenter.PresenterScore
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.fragment_pbo.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.*
import java.lang.IllegalStateException
import java.util.*

class PBOFragment : Fragment(), NilaiView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user: User
    private lateinit var nilai: Nilai
    private lateinit var nDatabase: DatabaseReference
    private lateinit var presenterScore: PresenterScore
    private lateinit var year: String
    private var register: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelable("user")!!
        return inflater.inflate(R.layout.fragment_pbo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance().reference
        nDatabase = FirebaseDatabase.getInstance().getReference("peserta")

        nilai = Nilai()
        nilai.nim = user.nim

        presenterScore = PresenterScore(nDatabase, nilai, this)

        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()

        checkRegistry()

        pbo_register.setOnClickListener { _ ->
            alert(getString(R.string.class_confirmation_prompt)) {
                yesButton { but ->
                    user.pbo = true
                    but.dismiss()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            presenterScore.registerCourse(mUser.uid, "pbo", year)
                            checkRegistry()
                        }
                        .addOnFailureListener {
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
        ch_1.setOnClickListener {
            if(register) {
                //TODO getUserKey()
                exam("pbo", "bab1", nilai.attempt1)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_2.setOnClickListener {
            if(register) {
                exam("pbo", "bab2", nilai.attempt2)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_3.setOnClickListener {
            if(register) {
                exam("pbo", "bab3", nilai.attempt3)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_4.setOnClickListener {
            if(register) {
                exam("pbo", "bab4", nilai.attempt4)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_5.setOnClickListener {
            if(register) {
                exam("pbo", "bab5", nilai.attempt5)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_6.setOnClickListener {
            if(register) {
                exam("pbo", "bab6", nilai.attempt6)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_7.setOnClickListener {
            if(register) {
                exam("pbo", "bab7", nilai.attempt7)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        ch_8.setOnClickListener {
            if(register) {
                exam("pbo", "bab8", nilai.attempt8)
            } else {
                toast(getString(R.string.class_error_enter_message))
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
        if(user.pbo!!) {
            pbo_1.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_2.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_3.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_4.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_5.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_6.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_7.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )
            pbo_8.backgroundTintList = ContextCompat.getColorStateList(context!!,
                R.color.colorMidBlue
            )

            pbo_register.visibility = View.GONE

            presenterScore.getSingleScore(mUser.uid, "pbo", this.year)
            register = true
        } else {
            pbo_register.visibility = View.VISIBLE
            register = false
        }
    }

    override fun getData(nilai: Nilai) {
        this.nilai = nilai
        try {
            if(nilai.attempt1>0) {
                pbo_1.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt2>0) {
                pbo_2.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt3>0) {
                pbo_3.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt4>0) {
                pbo_4.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt5>0) {
                pbo_5.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt6>0) {
                pbo_6.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt7>0) {
                pbo_7.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
            if(nilai.attempt8>0) {
                pbo_8.backgroundTintList = ContextCompat.getColorStateList(context!!,
                    R.color.colorDarkGreen
                )
            }
        } catch(e: IllegalStateException) {
            Log.e("PBO Button", e.stackTrace.toString())
        }

    }

    override fun showLoading() {
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun hideLoading(i: Int, t: String) {
        if(i!=0) {
            toast(t)
            dialog.dismiss()
        }
        dialog.dismiss()
    }

    private fun getUserKey() {
        var editText: EditText? = null
        alert {
            title = getString(R.string.enter_key_to_continue)
            customView {
                relativeLayout {
                    editText = editText {
                        maxLines = 1
                        ems  = 7
                        hint = getString(R.string.chapter_key_hint)
                    }.lparams{
                        margin = dip(5)
                        gravity = Gravity.CENTER
                    }
                }
            }
            onCancelled {
                toast(getString(R.string.cant_enter_without_key))
            }
            positiveButton(getString(R.string.cant_enter_without_key)) {
                validateKey(editText!!.text.toString())
            }
        }.show()
    }

    private fun validateKey(key: String) {
        //TODO request to database and make sure the key is same
    }

}
