@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.main

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
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
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.R.id.*
import divascion.marfiandhi.labrplunhas.presenter.PresenterUser
import divascion.marfiandhi.labrplunhas.model.User
import divascion.marfiandhi.labrplunhas.view.home.HomeFragment
import divascion.marfiandhi.labrplunhas.view.nilai.ResultFragment
import divascion.marfiandhi.labrplunhas.view.exam.PBOFragment
import divascion.marfiandhi.labrplunhas.view.exam.PPFragment
import divascion.marfiandhi.labrplunhas.view.login.LoginActivity
import divascion.marfiandhi.labrplunhas.view.profile.UserActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    MainView {

    private lateinit var dialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private var mUser: FirebaseUser? = null
    private lateinit var user: User
    private lateinit var presenterUser: PresenterUser
    private var mSavedInstanceState: Bundle? = null
    private var pause = false
    private var count = 0
    private var profile = false

    private val timer = object : CountDownTimer(2700, 1) {
        private var run = true
        override fun onTick(millisUntilFinished: Long) {
            run = true
        }

        override fun onFinish() {
            count = 0
            run = false
        }

        fun isRun(): Boolean {
            return run
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = getString(R.string.home)
        setSupportActionBar(toolbar)

        mDatabase = FirebaseDatabase.getInstance().getReference("user")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser
        val username = mUser?.uid

        user = User()

        presenterUser = PresenterUser(mDatabase, user, this)
        presenterUser.getUser(username!!)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(nav_home)
        changeFragment(savedInstanceState, HomeFragment(), null)
        mSavedInstanceState = savedInstanceState
    }

    override fun onResume() {
        super.onResume()
        if(pause) {
            if(profile) {
                mAuth = FirebaseAuth.getInstance()
                mUser = mAuth.currentUser
                loadView()
                profile = false
            } else
            pause = false
        }
    }

    override fun onPause() {
        pause = true
        super.onPause()
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
            if(count<1) {
                toast(getString(R.string.press_back_exit))
                timer.start()
                if(timer.isRun()) {
                    count++
                }
            } else {
                finishAffinity()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
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
                if(!mUser!!.isEmailVerified && user.role!="admin") {
                    alert(getString(R.string.verify_email_prompt)) {
                        okButton {
                            it.dismiss()
                        }
                    }.show()
                    nav_view.setCheckedItem(nav_home)
                } else {
                    toolbar.title = getString(R.string.basic_programming_prompt)
                    changeFragment(mSavedInstanceState, PPFragment(), this.user)
                }
            }
            nav_pbo -> {
                if(!mUser!!.isEmailVerified && user.role!="admin") {
                    alert(getString(R.string.verify_email_prompt)) {
                        okButton {
                            it.dismiss()
                        }
                    }.show()
                    nav_view.setCheckedItem(nav_home)
                } else {
                    toolbar.title = getString(R.string.object_oriented_prompt)
                    changeFragment(mSavedInstanceState, PBOFragment(), this.user)
                }
            }
            nav_profile -> {
                profile = true
                startActivity(intentFor<UserActivity>("user" to user))
            }
            nav_logout -> {
                alert(getString(R.string.logout_confirmation)) {
                    yesButton{_ ->
                        indeterminateProgressDialog(getString(R.string.please_wait)).show()
                        doAsync {
                            FirebaseAuth.getInstance().signOut()
                            uiThread {
                                startActivity(intentFor<LoginActivity>())
                                finish()
                            }
                        }
                    }
                    noButton {
                        it.dismiss()
                    }
                }.show()
            }
            nav_nilai -> {
                toolbar.title = "Rekap Nilai"
                changeFragment(mSavedInstanceState, ResultFragment(), null)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun showLoading() {
        dialog =  indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.show()
        dialog.setCancelable(false)
    }

    override fun hideLoading(i: Int, t: String) {
        if(i!=0) {
            toast(t)
            dialog.dismiss()
        }else {
            dialog.dismiss()
        }
    }

    override fun getData(user: User) {
        this.user = user

        loadView()

        if(user.role=="admin") {
            val menu = nav_view.menu
            menu.findItem(nav_nilai).isVisible = true
        }
    }

    private fun loadView() {
        nim_view.text = user.nim
        email_view.text = user.email
        name_view.text = mUser?.displayName.toString()

        Picasso.get().load(mUser?.photoUrl.toString())
            .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
            .into(profile_photo_view)
    }
}
