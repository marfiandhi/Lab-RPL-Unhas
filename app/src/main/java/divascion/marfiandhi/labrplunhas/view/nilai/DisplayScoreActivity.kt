@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.nilai

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import divascion.marfiandhi.labrplunhas.presenter.PresenterNilai
import divascion.marfiandhi.labrplunhas.utils.PhotoFullPopupWindow
import divascion.marfiandhi.labrplunhas.view.exam.NilaiView
import kotlinx.android.synthetic.main.activity_display_score.*
import kotlinx.android.synthetic.main.display_score_content.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.util.*

class DisplayScoreActivity : AppCompatActivity(), NilaiView{

    private var admin = false
    private lateinit var user: User
    private lateinit var nilai: Nilai
    private lateinit var subject: String
    private lateinit var mUser: FirebaseUser
    private lateinit var presenter: PresenterNilai
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_score)
        setSupportActionBar(score_display_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.dismiss()
        subject = intent.getStringExtra("subject")
        admin = intent.getBooleanExtra("admin", false)
        user = intent.getParcelableExtra("user")
        if(admin) {
            nilai = intent.getParcelableExtra("score")
            loadData()
        } else {
            mUser = FirebaseAuth.getInstance().currentUser!!
            mDatabase = FirebaseDatabase.getInstance().getReference("peserta")
            val year = Calendar.getInstance().get(Calendar.YEAR).toString()
            this.nilai = Nilai()
            supportActionBar?.title = "${user.nim} $subject ${getString(R.string.score_prompt)}"
            presenter = PresenterNilai(mDatabase, nilai, this)
            presenter.getSingleNilai(mUser.uid, subject.toLowerCase(), year)
        }
        display_score_pic.setOnClickListener {
            if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PhotoFullPopupWindow(applicationContext, display_score_pic, user.pic.toString(), null)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PhotoFullPopupWindow(applicationContext, display_score_pic, user.pic.toString(), null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        supportActionBar?.title = "${user.nim} $subject ${getString(R.string.score_prompt)}"
        Picasso.get().load(user.pic.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(display_score_pic)
        display_score_name.text = user.name
        display_score_nim.text = user.nim
        display_score_email.text = user.email
        if(subject=="PBO") {
            loadPBO()
        } else {
            loadPP()
        }
        score_attempt1.text = nilai.attempt1.toString()
        score_attempt2.text = nilai.attempt2.toString()
        score_attempt3.text = nilai.attempt3.toString()
        score_attempt4.text = nilai.attempt4.toString()
        score_attempt5.text = nilai.attempt5.toString()
        score_attempt6.text = nilai.attempt6.toString()
        score_attempt7.text = nilai.attempt7.toString()
        score_attempt8.text = nilai.attempt8.toString()

        score_value1.text = nilai.nilai1.toString()
        score_value2.text = nilai.nilai2.toString()
        score_value3.text = nilai.nilai3.toString()
        score_value4.text = nilai.nilai4.toString()
        score_value5.text = nilai.nilai5.toString()
        score_value6.text = nilai.nilai6.toString()
        score_value7.text = nilai.nilai7.toString()
        score_value8.text = nilai.nilai8.toString()
    }

    private fun loadPBO() {
        score_chapter1.text = getString(R.string.pbo_chapter_one)
        score_chapter2.text = getString(R.string.pbo_chapter_two)
        score_chapter3.text = getString(R.string.pbo_chapter_three)
        score_chapter4.text = getString(R.string.pbo_chapter_four)
        score_chapter5.text = getString(R.string.pbo_chapter_five)
        score_chapter6.text = getString(R.string.pbo_chapter_six)
        score_chapter7.text = getString(R.string.pbo_chapter_seven)
        score_chapter8.text = getString(R.string.pbo_chapter_eight)
    }

    private fun loadPP() {
        score_chapter1.text = getString(R.string.pp_chapter_one)
        score_chapter2.text = getString(R.string.pp_chapter_two)
        score_chapter3.text = getString(R.string.pp_chapter_three)
        score_chapter4.text = getString(R.string.pp_chapter_four)
        score_chapter5.text = getString(R.string.pp_chapter_five)
        score_chapter6.text = getString(R.string.pp_chapter_six)
        score_chapter7.text = getString(R.string.pp_chapter_seven)
        score_chapter8.text = getString(R.string.pp_chapter_eight)
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
        }
        dialog.dismiss()
        loadData()
    }
}
