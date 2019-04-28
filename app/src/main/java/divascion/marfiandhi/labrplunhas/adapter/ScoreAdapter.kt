package divascion.marfiandhi.labrplunhas.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.Nilai
import divascion.marfiandhi.labrplunhas.model.User
import kotlinx.android.synthetic.main.score_result_list.view.*

class ScoreAdapter (private val context: Context, private val score: List<Nilai>, private val user: List<User>, private val listener: (Nilai, User) -> Unit)
    : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ScoreViewHolder(LayoutInflater.from(context).inflate(R.layout.score_result_list, parent, false))

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bindItem(score[position], user, listener)
    }

    override fun getItemCount(): Int = score.size

    class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view){

        @SuppressLint("SimpleDateFormat")
        fun bindItem(score: Nilai, user: List<User>, listener: (Nilai, User) -> Unit) {
            var name = ""
            var profilePic = ""
            var uIndex = -1
            for(index in user.indices) {
                if(user[index].nim == score.nim) {
                    uIndex = index
                    name = user[index].name.toString()
                    profilePic = user[index].pic.toString()
                }
            }
            Picasso.get().load(profilePic)
                .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
                .into(itemView.score_pic)
            itemView.score_name_txt.text = name
            itemView.score_nim_txt.text = score.nim
            itemView.result_content.setOnClickListener { listener(score, user[uIndex]) }
        }
    }

}