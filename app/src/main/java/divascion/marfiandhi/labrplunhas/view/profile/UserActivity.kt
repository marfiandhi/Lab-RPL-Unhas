package divascion.marfiandhi.labrplunhas.view.profile

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_user.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class UserActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        user = intent.getParcelableExtra("user")
        mAuth.updateCurrentUser(mUser)
        user_display_name.text = mUser.displayName
        user_email.text = mUser.email
        user_nim.text = user.nim
        Picasso.get().load(mUser.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(user_pic)
        user_profile_group.setOnClickListener {
            startActivity(intentFor<DisplayProfileActivity>("user" to user))
        }
        user_edit.setOnClickListener {
            if(!mUser.isEmailVerified) {
                toast("Please verify your email first.")
            } else {
                startActivity(intentFor<EditProfileActivity>("user" to user))
            }
        }
        user_result_pbo.setOnClickListener {
            if(!mUser.isEmailVerified) {
                toast("Please verify your email first.")
            } else {
                toast("Result PBO on going...")
            }
        }
        user_result_pp.setOnClickListener {
            if(!mUser.isEmailVerified) {
                toast("Please verify your email first.")
            } else {
                toast("Result PP on going...")
            }
        }
        user_settings.setOnClickListener {
            toast("Settings on going...")
        }

        if(!mUser.isEmailVerified) {
            Snackbar.make(user_main_layout, "Email is not verified yet.", Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
