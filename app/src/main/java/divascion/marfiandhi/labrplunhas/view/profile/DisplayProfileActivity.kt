package divascion.marfiandhi.labrplunhas.view.profile

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_display_profile.*
import divascion.marfiandhi.labrplunhas.utils.PhotoFullPopupWindow


class DisplayProfileActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user = intent.getParcelableExtra("user")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        supportActionBar?.title = "${mUser.displayName}'s ${getString(R.string.prompt_profile)}"

        loadView()
        display_profile_pic.setOnClickListener{
            if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PhotoFullPopupWindow(applicationContext, display_profile_pic, mUser.photoUrl.toString(), null)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PhotoFullPopupWindow(applicationContext, display_profile_pic, user.pic.toString(), null)
        }

    }

    private fun loadView() {

        gender = if(user.male!!) {
            "Male"
        } else {
            "Female"
        }

        Picasso.get().load(mUser.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(display_profile_pic)
        display_txt_email.text = mUser.email
        display_txt_full_name.text = user.name
        display_txt_nickname.text = mUser.displayName
        display_txt_gender.text = gender
        display_txt_nim.text = user.nim
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
