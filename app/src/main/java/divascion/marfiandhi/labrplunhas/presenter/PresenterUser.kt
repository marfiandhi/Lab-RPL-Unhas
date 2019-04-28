package divascion.marfiandhi.labrplunhas.presenter

import com.google.firebase.database.*
import divascion.marfiandhi.labrplunhas.view.main.MainView
import divascion.marfiandhi.labrplunhas.model.User

class PresenterUser(private val mDatabase: DatabaseReference, private val user: User, private val view: MainView) {

    fun getUser(username: String) {
        view.showLoading()
        val sUser = mDatabase.child(username)
        sUser.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                view.hideLoading(3, "${p0.message}. ${p0.details}")
                return
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(postSnapshot: DataSnapshot in p0.children) {
                    when {
                        postSnapshot.key=="email" -> user.email = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="name" -> user.name = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="nim" -> user.nim = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="pic" -> user.pic = postSnapshot.getValue(String::class.java)
                        postSnapshot.key=="pbo" -> user.pbo = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="pp" -> user.pp = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="male" -> user.male = postSnapshot.getValue(Boolean::class.java)
                        postSnapshot.key=="role" -> user.role = postSnapshot.getValue(String::class.java)
                    }
                }
                view.getData(user)
                view.hideLoading(0, "")
            }

        })
    }
}