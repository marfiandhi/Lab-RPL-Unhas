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
import kotlinx.android.synthetic.main.fragment_pp.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.*
import org.jetbrains.anko.yesButton
import java.util.*

class PPFragment : Fragment(), NilaiView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var nDatabase: DatabaseReference
    private lateinit var modelNilai: ModelNilai
    private lateinit var nilai: Nilai
    private lateinit var user: User
    private lateinit var year: String

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

        nilai = Nilai(0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, user.nim)

        modelNilai = ModelNilai(nDatabase, nilai, this)

        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()

        checkRegistry()

        pp_register.setOnClickListener { _ ->
            alert("Are you sure you want to register to this class?") {
                yesButton { but ->
                    user.pp = true
                    but.dismiss()
                    mDatabase.child("user").child(mUser.uid).setValue(user)
                        .addOnSuccessListener {
                            modelNilai.registerCourse(mUser.uid, "pp", year)
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
        pp_1.setOnClickListener {
            exam("pp", "bab1", nilai.attempt1)
        }
        pp_2.setOnClickListener {
            exam("pp", "bab2", nilai.attempt2)
        }
        pp_3.setOnClickListener {
            exam("pp", "bab3", nilai.attempt3)
        }
        pp_4.setOnClickListener {
            exam("pp", "bab4", nilai.attempt4)
        }
        pp_5.setOnClickListener {
            exam("pp", "bab5", nilai.attempt5)
        }
        pp_6.setOnClickListener {
            exam("pp", "bab6", nilai.attempt6)
        }
        pp_7.setOnClickListener {
            exam("pp", "bab7", nilai.attempt7)
        }
        pp_8.setOnClickListener {
            exam("pp", "bab8", nilai.attempt8)
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

            modelNilai.getSingleNilai(mUser.uid, "pp", this.year)

            if(nilai.attempt1>0) {
                toast("You did it")
            }
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

    override fun getData(nilai: Nilai) {
        this.nilai = nilai
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
