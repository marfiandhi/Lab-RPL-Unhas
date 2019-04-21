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
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab1", "user" to user.name, "attempt" to nilai.attempt1))
        }
        pbo_2.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab2", "user" to user.name, "attempt" to nilai.attempt2))
        }
        pbo_3.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab3", "user" to user.name, "attempt" to nilai.attempt3))
        }
        pbo_4.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab4", "user" to user.name, "attempt" to nilai.attempt4))
        }
        pbo_5.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab5", "user" to user.name, "attempt" to nilai.attempt5))
        }
        pbo_6.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab6", "user" to user.name, "attempt" to nilai.attempt6))
        }
        pbo_7.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab7", "user" to user.name, "attempt" to nilai.attempt7))
        }
        pbo_8.setOnClickListener {
            startActivity(intentFor<ExamActivity>("subject" to "pbo", "chapter" to "bab8", "user" to user.name, "attempt" to nilai.attempt8))
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

            modelNilai.getSingleNilai(mUser.uid, "pbo", this.year)

            if(nilai.attempt1!!>0) {
                toast("You did it")
            }
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
