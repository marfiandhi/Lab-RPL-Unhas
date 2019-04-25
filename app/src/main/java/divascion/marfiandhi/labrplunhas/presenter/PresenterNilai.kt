package divascion.marfiandhi.labrplunhas.presenter

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import divascion.marfiandhi.labrplunhas.view.exam.NilaiView
import divascion.marfiandhi.labrplunhas.model.Nilai

class PresenterNilai(private val mDatabase: DatabaseReference, private val nilai: Nilai, private val view: NilaiView) {

    fun getSingleNilai(uid: String, course: String, year: String) {
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
                            mDatabase.child(course).child(year).child(uid).setValue(nilai)
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
                        postSnapshot.key=="attempt1" -> nilai.attempt1 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt2" -> nilai.attempt2 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt3" -> nilai.attempt3 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt4" -> nilai.attempt4 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt5" -> nilai.attempt5 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt6" -> nilai.attempt6 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt7" -> nilai.attempt7 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="attempt8" -> nilai.attempt8 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai1" -> nilai.nilai1 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai2" -> nilai.nilai2 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai3" -> nilai.nilai3 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai4" -> nilai.nilai4 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai5" -> nilai.nilai5 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai6" -> nilai.nilai6 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai7" -> nilai.nilai7 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nilai8" -> nilai.nilai8 = postSnapshot.getValue(Int::class.java)!!
                        postSnapshot.key=="nim" -> nilai.nim = postSnapshot.getValue(String::class.java)
                    }
                }
                view.getData(nilai)
                view.hideLoading(0, "")
            }

        })
    }

    fun getAllMemberNilai(course: String, year: String) {

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
                    mDatabase.child(course).child(year).child(uid).setValue(nilai)
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