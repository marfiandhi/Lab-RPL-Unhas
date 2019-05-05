package divascion.marfiandhi.labrplunhas.presenter

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import divascion.marfiandhi.labrplunhas.view.exam.NilaiView
import divascion.marfiandhi.labrplunhas.model.Nilai

class PresenterScore(private val mDatabase: DatabaseReference, private val score: Nilai, private val view: NilaiView) {

    fun getSingleScore(uid: String, course: String, year: String) {
        view.showLoading()
        val sUser = mDatabase.child(course).child(year).child(uid)
        sUser.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        view.hideLoading(3, "${p0.message}. ${p0.details}")
                        return
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child(course).child(year).hasChild(uid)) {
                            view.hideLoading(2, "")
                            return
                        }else {
                            mDatabase.child(course).child(year).child(uid).setValue(score)
                                .addOnSuccessListener {
                                    view.hideLoading(0, "")
                                }
                                .addOnFailureListener {
                                    view.hideLoading(1, it.stackTrace.toString())
                                }
                        }
                    }
                })
                view.hideLoading(0, "")
                return
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(postSnapshot: DataSnapshot in p0.children) {
                    when {
                        postSnapshot.key=="attempt1" -> score.attempt1 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt2" -> score.attempt2 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt3" -> score.attempt3 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt4" -> score.attempt4 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt5" -> score.attempt5 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt6" -> score.attempt6 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt7" -> score.attempt7 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt8" -> score.attempt8 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai1" -> score.nilai1 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai2" -> score.nilai2 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai3" -> score.nilai3 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai4" -> score.nilai4 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai5" -> score.nilai5 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai6" -> score.nilai6 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai7" -> score.nilai7 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai8" -> score.nilai8 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nim" -> score.nim = postSnapshot.getValue(String::class.java)
                    }
                }
                view.getData(score)
                view.hideLoading(0, "")
            }

        })
    }

    fun registerCourse(uid: String, course: String, year: String) {
        view.showLoading()
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                view.hideLoading(3, "${p0.message}. ${p0.details}")
                return
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(course).child(year).hasChild(uid)) {
                    view.hideLoading(2, "")
                    return
                }else {
                    mDatabase.child(course).child(year).child(uid).setValue(score)
                        .addOnSuccessListener {
                            view.hideLoading(0, "")
                        }
                        .addOnFailureListener {
                            view.hideLoading(1, it.stackTrace.toString())
                        }
                }
            }
        })
        view.hideLoading(0, "")
    }
}