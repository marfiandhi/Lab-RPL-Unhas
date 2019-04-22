package divascion.marfiandhi.labrplunhas

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import divascion.marfiandhi.labrplunhas.R.id.*
import divascion.marfiandhi.labrplunhas.model.ModelUser
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainView {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var user: User
    private lateinit var modelUser: ModelUser
    private var mSavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "Home"
        setSupportActionBar(toolbar)

        mDatabase = FirebaseDatabase.getInstance().getReference("user")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        val username = mUser.uid

        user = User()

        modelUser = ModelUser(mDatabase, user, this)
        modelUser.getUser(username)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(nav_home)
        changeFragment(savedInstanceState, HomeFragment(), null)
        mSavedInstanceState = savedInstanceState
    }

    private fun changeFragment(savedInstanceState: Bundle?, fragment: Fragment, user: User?) {
        if(savedInstanceState == null) {
            val bundle = Bundle()
            bundle.putParcelable("user", user)
            fragment.arguments = bundle
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit()
        }
        mSavedInstanceState = savedInstanceState
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_language -> {
                //TODO localization language yeah
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            nav_home -> {
                toolbar.title = "Home"
                changeFragment(mSavedInstanceState, HomeFragment(), null)
            }
            nav_pp -> {
                if(!mUser.isEmailVerified && user.role!="admin") {
                    alert("Please verify you email address first.") {
                        okButton {
                            it.dismiss()
                        }
                    }.show()
                    nav_view.setCheckedItem(nav_home)
                } else {
                    toolbar.title = "Pengantar Pemrograman"
                    changeFragment(mSavedInstanceState, PPFragment(), this.user)
                }
            }
            nav_pbo -> {
                if(!mUser.isEmailVerified && user.role!="admin") {
                    alert("Please verify you email address first.") {
                        okButton {
                            it.dismiss()
                        }
                    }.show()
                    nav_view.setCheckedItem(nav_home)
                } else {
                    toolbar.title = "Pemrograman Berorientasi Objek"
                    changeFragment(mSavedInstanceState, PBOFragment(), this.user)
                }
            }
            nav_profile -> {
                toast("Still in progress")
                //TODO profile
            }
            nav_logout -> {
                alert("Are you sure want to logout?") {
                    yesButton{
                        indeterminateProgressDialog("Please wait...").show()
                            FirebaseAuth.getInstance().signOut()
                            startActivity(intentFor<LoginActivity>())
                        finish()
                    }
                    noButton {
                        it.dismiss()
                    }
                }.show()
            }
            nav_nilai -> {
                toolbar.title = "Rekap Nilai"
                changeFragment(mSavedInstanceState, NilaiFragment(), null)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun showLoading() {
        dialog =  indeterminateProgressDialog("Please Wait..")
        dialog.show()
        dialog.setCancelable(false)
    }

    override fun hideLoading(i: Int, t: String) {
        when(i) {
            3 -> toast("Canceled. $t").show()
            2 -> toast("Failed to Connect, try again. $t\"").show()
            1 -> toast("There is no such data. $t\"").show()
        }
        dialog.dismiss()
    }

    override fun getData(user: User) {
        this.user = user

        nim_view.text = user.nim
        name_view.text = user.name
        email_view.text = user.email
        if(user.role=="admin") {
            val menu = nav_view.menu
            menu.findItem(nav_nilai).isVisible = true
        }
    }
}
