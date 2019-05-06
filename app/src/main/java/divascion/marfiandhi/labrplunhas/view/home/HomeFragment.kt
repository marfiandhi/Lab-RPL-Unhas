@file:Suppress("DEPRECATION")

package divascion.marfiandhi.labrplunhas.view.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.adapter.NewsAdapter
import divascion.marfiandhi.labrplunhas.model.News
import divascion.marfiandhi.labrplunhas.presenter.PresenterNews
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.onRefresh
import java.util.*

class HomeFragment : Fragment(), HomeView, SearchView.OnQueryTextListener {

    private lateinit var adapter : NewsAdapter
    private var news: MutableList<News> = mutableListOf()
    private var searchNews: MutableList<News> = ArrayList()
    private lateinit var mDatabase: DatabaseReference
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var presenter: PresenterNews
    private lateinit var year: String
    private lateinit var spinner: Spinner
    private lateinit var choice: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(false)
        setHasOptionsMenu(true)
        year = Calendar.getInstance().get(Calendar.YEAR).toString()
        swipe = news_swipe_layout
        swipe.setColorSchemeColors(
            resources.getColor(R.color.colorMaroon),
            resources.getColor(R.color.colorMidBlue),
            resources.getColor(android.R.color.holo_green_light),
            resources.getColor(R.color.colorAccent))
        mDatabase = FirebaseDatabase.getInstance().reference
        presenter = PresenterNews(mDatabase, news, this)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        news_recycler.layoutManager = linearLayoutManager
        adapter = NewsAdapter(activity!!, news) {
            startActivity(intentFor<HomeDetailsActivity>(
                "news" to it
            ))
        }
        news_recycler.adapter = adapter
        spinnerAdapter()
        swipe.onRefresh {
            presenter.getNews(year, choice)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                choice = spinner.selectedItem.toString()
                presenter.getNews(year, choice)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                Log.e("Spinner nothing", arg0.toString())
            }

        }
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText!!.isNotEmpty()) {
            this.news.clear()
            val search = newText.toLowerCase()
            this.searchNews.forEach{
                try {
                    if(it.title?.toLowerCase()!!.contains(search)|| it.message?.toLowerCase()!!.contains(search)) {
                        this.news.add(it)
                    }
                } catch(e: Exception) {
                    Log.e("search", e.message.toString())
                }
            }
        } else {
            this.news.clear()
            this.news.addAll(this.searchNews)
        }
        adapter.notifyDataSetChanged()
        return true
    }

    private fun spinnerAdapter() {
        spinner = home_spinner
        val choice = ArrayList<String>()
        choice.add(getString(R.string.prompt_all))
        choice.add(getString(R.string.prompt_general))
        choice.add(getString(R.string.prompt_pp))
        choice.add(getString(R.string.prompt_pbo))
        val spinnerAdapter = ArrayAdapter<String>(context!!, R.layout.spinner_item, choice)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.setSelection(0)
    }

    override fun getData(news: List<News>, category: String) {
        if(!news.isEmpty()) {
            news_doesnt_exist_txt.visibility = View.GONE
            this.searchNews.clear()
            this.searchNews.addAll(news)
            val mCategory = category.toLowerCase()
            val mNews: MutableList<News> = mutableListOf()
            if(category!=getString(R.string.prompt_all)) {
                for(index in news.indices) {
                    if(news[index].type == mCategory) {
                        mNews.add(news[index])
                        Log.e(mCategory, "${news[index].type}")
                    }
                }
                Log.e("mNews", "${mNews.isEmpty()}")
                this.news.clear()
                this.searchNews.clear()
                this.news.addAll(mNews)
                this.searchNews.addAll(mNews)
            }
        } else {
            news_doesnt_exist_txt.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    override fun hideLoading() {
        swipe.isRefreshing = false
    }

    override fun showLoading() {
        swipe.isRefreshing = true
    }
}
