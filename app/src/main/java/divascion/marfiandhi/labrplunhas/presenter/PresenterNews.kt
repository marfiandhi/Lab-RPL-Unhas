package divascion.marfiandhi.labrplunhas.presenter

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import divascion.marfiandhi.labrplunhas.model.News
import divascion.marfiandhi.labrplunhas.view.home.HomeView

class PresenterNews(private val mDatabase: DatabaseReference, private val news: MutableList<News>, private val view: HomeView) {

    fun getNews(year: String, category: String) {
        view.showLoading()
        val userResultListListener = object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                view.hideLoading()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                news.clear()
                if(snapshot.hasChildren()) {
                    snapshot.children.mapNotNullTo(news) {
                        it.getValue<News>(News::class.java)
                    }
                    view.getData(news, category)
                    view.hideLoading()
                }
            }
        }
        mDatabase.child("news").child("general").child(year).addListenerForSingleValueEvent(userResultListListener)
    }
}