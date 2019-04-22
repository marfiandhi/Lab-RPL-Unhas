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
import divascion.marfiandhi.labrplunhas.model.ModelNilai
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.fragment_pbo.*
import kotlinx.android.synthetic.main.fragment_pbo.view.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.*
import org.jetbrains.anko.yesButton
import java.util.*

class PBOFragment : Fragment(), NilaiView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user: User
    private lateinit var nilai: Nilai
    private lateinit var nDatabase: DatabaseReference
    private lateinit var modelNilai: ModelNilai
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

        nilai = Nilai(0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, user.nim)

        modelNilai = ModelNilai(nDatabase, nilai, this)

        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()

        checkRegistry()

        pbo_register.setOnClickListener { _ ->
            alert("Are you sure you want to register to this class?") {
                yesButton { but ->
                    user.pbo = true
                    but.dismiss()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            modelNilai.registerCourse(mUser.uid, "pbo", year)
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
        pbo_1.setOnClickListener {
            if(register) {
                exam("pbo", "bab1", nilai.attempt1)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_2.setOnClickListener {
            if(register) {
                exam("pbo", "bab2", nilai.attempt2)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_3.setOnClickListener {
            if(register) {
                exam("pbo", "bab3", nilai.attempt3)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_4.setOnClickListener {
            if(register) {
                exam("pbo", "bab4", nilai.attempt4)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_5.setOnClickListener {
            if(register) {
                exam("pbo", "bab5", nilai.attempt5)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_6.setOnClickListener {
            if(register) {
                exam("pbo", "bab6", nilai.attempt6)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_7.setOnClickListener {
            if(register) {
                exam("pbo", "bab7", nilai.attempt7)
            } else {
                toast("You have to register on this class first before entering the exam.")
            }
        }
        pbo_8.setOnClickListener {
            if(register) {
                exam("pbo", "bab8", nilai.attempt8)
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
            "nim" to user.nim))
    }

    private fun checkRegistry() {
        if(user.pbo!!) {
            pbo_1.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_2.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_3.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_4.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_5.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_6.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_7.setTextColor(Color.parseColor("#FFFFFF"))
            pbo_8.setTextColor(Color.parseColor("#FFFFFF"))

            pbo_1.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_2.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_3.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_4.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_5.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_6.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_7.setBackgroundResource(R.drawable.rounded_color_another_again)
            pbo_8.setBackgroundResource(R.drawable.rounded_color_another_again)

            pbo_register.visibility = View.GONE

            modelNilai.getSingleNilai(mUser.uid, "pbo", this.year)
            register = true
        } else {
            pbo_register.visibility = View.VISIBLE
            register = false
        }
    }

    override fun getData(nilai: Nilai) {
        this.nilai = nilai
        if(nilai.attempt1>0) {
            pbo_1.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt2>0) {
            pbo_2.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt3>0) {
            pbo_3.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt4>0) {
            pbo_4.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt5>0) {
            pbo_5.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt6>0) {
            pbo_6.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt7>0) {
            pbo_7.setBackgroundResource(R.drawable.rounded_color_another_selected)
        }
        if(nilai.attempt8>0) {
            pbo_8.setBackgroundResource(R.drawable.rounded_color_another_selected)
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

}
