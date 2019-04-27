package divascion.marfiandhi.labrplunhas.view.profile

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
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
    private lateinit var mDatabase: DatabaseReference
    private var pause: Boolean = false
    private var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        user = intent.getParcelableExtra("user")
        mAuth.updateCurrentUser(mUser)

        loadView()

        user_profile_group.setOnClickListener {
            startActivity(intentFor<DisplayProfileActivity>("user" to user))
        }
        user_edit.setOnClickListener {
            if(mUser.isEmailVerified || user.role=="admin") {
                startActivity(intentFor<EditProfileActivity>("user" to user))
                edit = true
            } else {
                toast("Please verify your email first.")
            }
        }
        user_result_pbo.setOnClickListener {
            if(mUser.isEmailVerified || user.role=="admin") {
                toast("Result PBO on going...")
            } else {
                toast("Please verify your email first.")
            }
        }
        user_result_pp.setOnClickListener {
            if(mUser.isEmailVerified || user.role=="admin") {
                toast("Result PP on going...")
            } else {
                toast("Please verify your email first.")
            }
        }
        user_settings.setOnClickListener {
            toast("Settings on going...")
        }

        if(!mUser.isEmailVerified && user.role!="admin") {
            Snackbar.make(user_main_layout, "Email is not verified yet.", Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    private fun loadView() {
        getUser(mUser.uid)
        user_display_name.text = mUser.displayName
        user_email.text = mUser.email
        user_nim.text = user.nim
        Picasso.get().load(mUser.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(user_pic)
    }

    private fun getUser(username: String) {
        mDatabase = FirebaseDatabase.getInstance().reference
        val sUser = mDatabase.child("user").child(username)
        sUser.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(postSnapshot: DataSnapshot in p0.children) {
                    when {
                        postSnapshot.key=="email" -> user.email = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="name" -> user.name = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="nim" -> user.nim = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="pbo" -> user.pbo = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="pp" -> user.pp = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="male" -> user.male = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="role" -> user.role = postSnapshot.getValue(String::class.java)
                    }
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if(pause) {
            if(edit) {
                mAuth = FirebaseAuth.getInstance()
                mUser = mAuth.currentUser!!
                loadView()
                edit = false
            } else
                pause = false
        }
    }

    override fun onPause() {
        pause = true
        super.onPause()
    }
}
