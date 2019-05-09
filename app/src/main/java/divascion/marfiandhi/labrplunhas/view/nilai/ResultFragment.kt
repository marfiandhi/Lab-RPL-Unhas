@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.nilai


import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.adapter.PagerAdapter
import kotlinx.android.synthetic.main.fragment_result.*
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.toast
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.model.User

class ResultFragment : Fragment(), ResultView {

    private var user: MutableList<User> = mutableListOf()
    private lateinit var dialog: ProgressDialog
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(false)
        dialog = indeterminateProgressDialog(getString(R.string.please_wait))
        dialog.setCancelable(false)
        dialog.dismiss()
        mDatabase = FirebaseDatabase.getInstance().reference
        loadData()
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun loadData() {
        showLoading()
        user.clear()
        val userResultListListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toast(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()) {
                    snapshot.children.mapNotNullTo(user) {
                        it.getValue<User>(User::class.java)
                    }
                    hideLoading("")
                    loadPager()
                }
            }
        }
        mDatabase.child("user").addListenerForSingleValueEvent(userResultListListener)
    }

    override fun showLoading() {
        dialog.show()
    }

    override fun hideLoading(message: String) {
        dialog.dismiss()
        if(!TextUtils.isEmpty(message)) {
            toast(message)
        }
    }

    private fun setupViewPager(pager: ViewPager?, firstTitle: String, secondTitle: String, firstFragment: Fragment, secondFragment: Fragment) {
        val adapter = PagerAdapter(fragmentManager!!)

        adapter.addFragment(firstFragment, firstTitle, "user", user)

        adapter.addFragment(secondFragment, secondTitle,"user", user)
        pager?.adapter = adapter
    }

    private fun loadPager() {
        if(user.isEmpty()) {
            return
        }
        setupViewPager(view_pager, getString(R.string.result_pbo), getString(R.string.result_pp), PboScoreFragment(), PpScoreFragment())
    }

}
