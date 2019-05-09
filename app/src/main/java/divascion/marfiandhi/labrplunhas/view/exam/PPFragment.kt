@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.exam

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.presenter.PresenterScore
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
    private lateinit var presenterScore: PresenterScore
    private lateinit var score: Nilai
    private lateinit var user: User
    private lateinit var year: String
    private var register: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelable("user")!!
        return inflater.inflate(R.layout.fragment_pp, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mDatabase = FirebaseDatabase.getInstance().reference
        nDatabase = FirebaseDatabase.getInstance().getReference("peserta")
        setHasOptionsMenu(false)

        score = Nilai()
        score.nim = user.nim

        presenterScore = PresenterScore(nDatabase, score, this)

        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()

        checkRegistry()

        pp_register.setOnClickListener { _ ->
            alert(getString(R.string.class_confirmation_prompt)) {
                yesButton { but ->
                    user.pp = true
                    but.dismiss()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            presenterScore.registerCourse(mUser.uid, "pp", year)
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
                getUserKey(1)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_2.setOnClickListener {
            if(register) {
                getUserKey(2)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_3.setOnClickListener {
            if(register) {
                getUserKey(3)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_4.setOnClickListener {
            if(register) {
                getUserKey(4)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_5.setOnClickListener {
            if(register) {
                getUserKey(5)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_6.setOnClickListener {
            if(register) {
                getUserKey(6)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_7.setOnClickListener {
            if(register) {
                getUserKey(7)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
        chp_8.setOnClickListener {
            if(register) {
                getUserKey(8)
            } else {
                toast(getString(R.string.class_error_enter_message))
            }
        }
    }

    private fun exam(subject: String, chapter: String, attempt: Int) {
        alert(getString(R.string.alert_message_confirmation_exam)) {
            title = getString(R.string.confirm)
            yesButton {
                it.dismiss()
                startActivity(intentFor<ExamActivity>(
                    "subject" to subject,
                    "chapter" to chapter,
                    "user" to user.name,
                    "attempt" to attempt,
                    "nim" to user.nim,
                    "score" to score))
            }
            noButton {
                it.dismiss()
            }
        }.show()
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

            presenterScore.getSingleScore(mUser.uid, "pp", this.year)
            register = true
        } else {
            pp_register.visibility = View.VISIBLE
            register = false
        }
    }

    override fun getData(nilai: Nilai) {
        this.score = nilai
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

    private fun getUserKey(chapter: Int) {
        when(chapter) {
            1 -> {
                if(score.attempt1>0) {
                    exam("pp", "bab1", score.attempt1)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab1", score.attempt8)
                        }
                    }.show()
                }
            }
            2 -> {
                if(score.attempt2>0) {
                    exam("pp", "bab2", score.attempt2)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab2", score.attempt8)
                        }
                    }.show()
                }
            }
            3 -> {
                if(score.attempt3>0) {
                    exam("pp", "bab3", score.attempt3)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab3", score.attempt8)
                        }
                    }.show()
                }
            }
            4 -> {
                if(score.attempt4>0) {
                    exam("pp" +
                            "", "bab4", score.attempt4)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab4", score.attempt8)
                        }
                    }.show()
                }
            }
            5 -> {
                if(score.attempt5>0) {
                    exam("pp", "bab5", score.attempt5)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab5", score.attempt8)
                        }
                    }.show()
                }
            }
            6 -> {
                if(score.attempt6>0) {
                    exam("pp", "bab6", score.attempt6)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab6", score.attempt8)
                        }
                    }.show()
                }
            }
            7 -> {
                if(score.attempt7>0) {
                    exam("pp", "bab7", score.attempt7)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab7", score.attempt8)
                        }
                    }.show()
                }
            }
            8 -> {
                if(score.attempt8>0) {
                    exam("p", "bab8", score.attempt8)
                } else {
                    var editText: EditText? = null
                    alert {
                        title = getString(R.string.enter_key_to_continue)
                        customView {
                            relativeLayout {
                                editText = editText {
                                    maxLines = 1
                                    ems  = 7
                                    hint = getString(R.string.chapter_key_hint)
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }.lparams{
                                    margin = dip(20)
                                    gravity = Gravity.CENTER_VERTICAL
                                    width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }
                        }
                        onCancelled {
                            toast(getString(R.string.cant_enter_without_key))
                        }
                        positiveButton(getString(R.string.enter)) {
                            validateKey(editText!!.text.toString(), "bab8" , score.attempt8)
                        }
                    }.show()
                }
            }
        }
    }

    private fun validateKey(key: String, chapter: String, attempt: Int) {
        showLoading()
        val sUser = mDatabase.child("respon").child("key").child("pp").child(chapter)
        sUser.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                hideLoading(0, p0.message)
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                val realKey: String? = p0.getValue(String::class.java)
                hideLoading(0, "")
                if(key==realKey) {
                    exam("pp", chapter, attempt)
                } else {
                    toast(getString(R.string.wrong_key))
                }
            }
        })
    }

}
