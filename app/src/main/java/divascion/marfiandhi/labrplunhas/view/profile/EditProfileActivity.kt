package divascion.marfiandhi.labrplunhas.view.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.jetbrains.anko.toast

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mUser: FirebaseUser
    private var gender = "Unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        user = intent.getParcelableExtra("user")
        loadView()
        edit_btn_profile_pic.setOnClickListener {
            toast("Set profile pic still progress")
        }
        edit_btn_email.setOnClickListener {
            toast("Change email still progress")
        }
        edit_btn_password.setOnClickListener {
            toast("Change password still progress")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.toolbar_save -> save()
            else -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save() {
        loadView()
    }

    private fun loadView(){
        Picasso.get().load(mUser.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(edit_profile_pic)
        edit_full_name.setText(user.name)
        edit_nickname.setText(mUser.displayName)
        gender = if(user.male!!) {
            "Male"
        } else {
            "Female"
        }
        edit_gender.setText(gender)
        edit_nim.setText(user.nim)
    }
}
