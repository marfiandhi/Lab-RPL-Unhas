@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.nilai

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.adapter.ScoreAdapter
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.fragment_pbo_score.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.onRefresh
import java.util.*

class PboScoreFragment : Fragment(), ResultView, SearchView.OnQueryTextListener {

    private var result: MutableList<Nilai> = mutableListOf()
    private var searchResult: MutableList<Nilai> = ArrayList()
    private lateinit var user: ArrayList<User>
    private var searchUser: MutableList<User> = ArrayList()
    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter : ScoreAdapter
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var year: String
    private lateinit var spinner: Spinner
    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        user = arguments?.getParcelableArrayList("user")!!
        searchUser.clear()
        searchUser.addAll(user)
        return inflater.inflate(R.layout.fragment_pbo_score, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = getString(R.string.search_hint)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        swipe = pbo_swipe_layout
        swipe.setColorSchemeColors(
            resources.getColor(R.color.colorMaroon),
            resources.getColor(R.color.colorMidBlue),
            resources.getColor(android.R.color.holo_green_light),
            resources.getColor(R.color.colorAccent))
        textView = score_doesnt_exist_pbo_txt
        mDatabase = FirebaseDatabase.getInstance().reference
        spinnerAdapter()
        this.year = Calendar.getInstance().get(Calendar.YEAR).toString()
        pbo_recycler_result.layoutManager = LinearLayoutManager(activity)
        adapter = ScoreAdapter(activity!!, result, user) {score, user ->
            startActivity(intentFor<DisplayScoreActivity>(
                "score" to score,
                "user" to user,
                "admin" to true,
                "subject" to "PBO"))
        }
        pbo_recycler_result.adapter = adapter
        swipe.onRefresh {
            this.year = spinner.selectedItem.toString()
            loadData()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                year = spinner.selectedItem.toString()
                loadData()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                Log.e("Spinner nothing", arg0.toString())
            }

        }

    }

    private fun spinnerAdapter() {
        spinner = pbo_year_spinner
        val years = ArrayList<String>()
        val thisYear = Calendar.getInstance().get(Calendar.YEAR)
        var num = 0
        for(number in 2014..thisYear) {
            years.add(number.toString())
            if(number!=thisYear) {
                num++
            }
        }
        val spinnerAdapter = ArrayAdapter<String>(context!!, R.layout.spinner_item, years)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(num)
    }

    override fun loadData() {
        showLoading()
        val timer = object : CountDownTimer(15000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                Log.e("Connecting", "${(millisUntilFinished/1000)%60}")
            }

            override fun onFinish() {
                hideLoading("Error")
                return
            }
        }
        timer.start()
        result.clear()
        searchResult.clear()
        val pboResultListListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                snackbar(swipe, p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChildren()) {
                    snapshot.children.mapNotNullTo(result) {
                        it.getValue<Nilai>(Nilai::class.java)
                    }
                    timer.cancel()
                    searchResult.addAll(result)
                    hideLoading("")
                }
            }
        }
        mDatabase.child("peserta/pbo/${this.year}").addListenerForSingleValueEvent(pboResultListListener)
    }

    override fun showLoading() {
        swipe.isRefreshing = true
    }

    override fun hideLoading(message: String) {
        swipe.isRefreshing = false
        if(TextUtils.isEmpty(message)) {
            onDone()
        } else {
            textView.visibility = View.VISIBLE
            swipe.visibility = View.VISIBLE
            onDone()
        }
        if(!result.isEmpty()) {
            textView.visibility = View.GONE
            swipe.visibility = View.VISIBLE
            onDone()
        }
    }

    private fun onDone() {
        adapter.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText!!.isNotEmpty()) {
            this.user.clear()
            this.result.clear()
            val search = newText.toLowerCase()
            this.searchUser.forEach{
                if(it.name?.toLowerCase()!!.contains(search)|| it.nim?.toLowerCase()!!.contains(search)) {
                    this.user.add(it)
                    this.searchResult.forEach {score ->
                        if(score.nim==it.nim) {
                            this.result.add(score)
                        }
                    }
                }
            }
        } else {
            this.user.clear()
            this.result.clear()
            this.user.addAll(this.searchUser)
            this.result.addAll(this.searchResult)
        }
        adapter.notifyDataSetChanged()
        return true
    }
}
