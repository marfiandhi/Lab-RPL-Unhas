@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.exam

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.presenter.PresenterNilai
import divascion.marfiandhi.labrplunhas.model.Nilai
import kotlinx.android.synthetic.main.activity_score.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.util.*

class ScoreActivity : AppCompatActivity(), NilaiView {

    private lateinit var nilai: Nilai
    private lateinit var name: String
    private lateinit var nim: String
    private lateinit var subject: String
    private lateinit var chapter: String
    private lateinit var dialog: ProgressDialog
    private var realScore = 0
    private var score = 0
    private var totalQuestion = 0
    private var attempt = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.dismiss()
        this.score = intent.getIntExtra("right", 0)
        this.totalQuestion = intent.getIntExtra("totalQuestion", 0)
        this.name = intent.getStringExtra("user")
        this.attempt = intent.getIntExtra("attempt", -1)
        this.attempt += 1
        this.chapter = intent.getStringExtra("chapter")
        this.subject = intent.getStringExtra("subject")
        this.nim = intent.getStringExtra("nim")

        realScore = ((score.toFloat()/totalQuestion.toFloat())*100f).toInt()

        val num = when (chapter) {
            "bab1" -> 1
            "bab2" -> 2
            "bab3" -> 3
            "bab4" -> 4
            "bab5" -> 5
            "bab6" -> 6
            "bab7" -> 7
            "bab8" -> 8
            else -> 0
        }

        txt_count_right.text = score.toString()
        txt_count_task.text = totalQuestion.toString()
        txt_score.text = realScore.toString()
        txt_your_score.text = "$name's Score"

        if(attempt<2) {
            saveData(num, realScore)
        } else {
            updateAttempt(num)
        }

        btn_home.setOnClickListener {
            realScore = 0
            finish()
        }
    }

    private fun updateAttempt(num: Int) {
        val mDatabase = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()
        val mUser = auth.currentUser!!
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()

        this.nilai = intent.getParcelableExtra("nilai")

        when (num) {
            1 -> {
                nilai.attempt1 = attempt
            }
            2 -> {
                nilai.attempt2 = attempt
            }
            3 -> {
                nilai.attempt3 = attempt
            }
            4 -> {
                nilai.attempt4 = attempt
            }
            5 -> {
                nilai.attempt5 = attempt
            }
            6 -> {
                nilai.attempt6 = attempt
            }
            7 -> {
                nilai.attempt7 = attempt
            }
            8 -> {
                nilai.attempt8 = attempt
            }
            else -> Log.e("score", num.toString())
        }

        showLoading()
        mDatabase.child("peserta").child(subject).child(year).child(mUser.uid).setValue(nilai)
            .addOnSuccessListener {
                hideLoading(0, "")
            }
            .addOnFailureListener{
                hideLoading(1, "${it.message}")
            }
    }

    private fun saveData(num: Int, realScore: Int) {
        val mDatabase = FirebaseDatabase.getInstance().reference
        val nDatabase = FirebaseDatabase.getInstance().getReference("peserta")
        val auth = FirebaseAuth.getInstance()
        val mUser = auth.currentUser!!
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()

        this.nilai = Nilai()
        this.nilai.nim = this.nim
        val modelNilai = PresenterNilai(nDatabase, nilai, this)
        modelNilai.getSingleNilai(mUser.uid, subject, year)

        when (num) {
            1 -> {
                nilai.attempt1 = attempt
                nilai.nilai1 = realScore
            }
            2 -> {
                nilai.attempt2 = attempt
                nilai.nilai2 = realScore
            }
            3 -> {
                nilai.attempt3 = attempt
                nilai.nilai3 = realScore
            }
            4 -> {
                nilai.attempt4 = attempt
                nilai.nilai4 = realScore
            }
            5 -> {
                nilai.attempt5 = attempt
                nilai.nilai5 = realScore
            }
            6 -> {
                nilai.attempt6 = attempt
                nilai.nilai6 = realScore
            }
            7 -> {
                nilai.attempt7 = attempt
                nilai.nilai7 = realScore
            }
            8 -> {
                nilai.attempt8 = attempt
                nilai.nilai8 = realScore
            }
            else -> Log.e("score", num.toString())
        }

        showLoading()
        mDatabase.child("peserta").child(subject).child(year).child(mUser.uid).setValue(nilai)
            .addOnSuccessListener {
                hideLoading(0, "")
            }
            .addOnFailureListener{
                hideLoading(1, "${it.message}")
            }
    }


    override fun getData(nilai: Nilai) {
        this.nilai = nilai
    }

    override fun showLoading() {
        dialog.show()
    }

    override fun hideLoading(i: Int, t: String) {
        if(i!=0) {
            toast(t)
            dialog.dismiss()
        }
        dialog.dismiss()
    }
}
