package divascion.marfiandhi.labrplunhas.adapter

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User

class PagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {

    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

    fun addFragment(fragment: Fragment, title: String, uKey: String, user: List<User>) {
        fragments.add(fragment)
        val bundle =  Bundle()
        bundle.putParcelableArrayList(uKey, ArrayList<Parcelable>(user))
        fragment.arguments = bundle
        titles.add(title)
    }
}