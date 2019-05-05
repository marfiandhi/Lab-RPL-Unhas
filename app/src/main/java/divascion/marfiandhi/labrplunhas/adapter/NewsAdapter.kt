package divascion.marfiandhi.labrplunhas.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.News
import divascion.marfiandhi.labrplunhas.utils.PhotoFullPopupWindow
import kotlinx.android.synthetic.main.news_list.view.*

class NewsAdapter (private val context: Context, private val news: List<News>, private val listener: (News) -> Unit)
    : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.news_list, parent, false))

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindItem(news[position], context, listener)
    }

    override fun getItemCount(): Int = news.size

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun bindItem(news: News, context: Context, listener: (News) -> Unit) {
            if(news.image!=null) {
                val transformation = object: Transformation {
                    override fun key(): String {
                        return "transformation desiredWidth"
                    }

                    override fun transform(source: Bitmap?): Bitmap {
                        val targetWidth = itemView.news_image.width
                        val aspectRatio= (source?.height?.toDouble())!! / (source.width.toDouble())
                        val targetHeight = (targetWidth*aspectRatio).toInt()
                        val result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false)
                        if(result!=source) {
                            source.recycle()
                        }
                        return result
                    }
                }
                Picasso.get().load(news.image)
                    .placeholder(R.color.colorBlack).error(R.drawable.ic_launcher_foreground)
                    .transform(transformation)
                    .into(itemView.news_image)
                itemView.news_image.setOnClickListener {
                        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            PhotoFullPopupWindow(context.applicationContext, it, news.image.toString(), null)
                        } else {
                            Toast.makeText(context, context.getString(R.string.please_enable_storage_permission), Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                itemView.news_image.setImageDrawable(null)
            }
            val textView = itemView.findViewById<TextView>(R.id.news_message)
            textView.text = news.message
//            setLabelAfterEllipsize(context, textView, context.getString(R.string.see_more), 3)
            itemView.news_title.text = news.title
            itemView.setOnClickListener { listener(news) }
        }



        private fun getTextWidth(text: String, textSize: Float):Int {
            val bounds = Rect()
            val paint = Paint()
            paint.textSize = textSize
            paint.getTextBounds(text, 0, text.length, bounds)
            return Math.ceil(bounds.width().toDouble()).toInt()
        }

        @SuppressLint("SetTextI18n")
        private fun setLabelAfterEllipsize(context:Context, textView: TextView, label: String, maxLines: Int) {
            if(textView.layout.getEllipsisCount(maxLines-1)==0) {
                return
            }
            val start = textView.layout.getLineStart(0)
            val end = textView.layout.getLineEnd(textView.lineCount-1)
            val displayed = textView.text.toString().substring(start, end)
            val displayedWidth = getTextWidth(displayed, textView.textSize)

            val ellipsize = context.getString(R.string.ellipsis_char)
            val suffix = "$ellipsize$label"

            var newText = displayed
            var textWidth = getTextWidth(newText+suffix, textView.textSize)

            while(textWidth>displayedWidth) {
                newText = newText.substring(0, newText.length-1).trim()
                textWidth = getTextWidth(newText+suffix, textView.textSize)
            }

            textView.text = newText+suffix
        }

    }

}