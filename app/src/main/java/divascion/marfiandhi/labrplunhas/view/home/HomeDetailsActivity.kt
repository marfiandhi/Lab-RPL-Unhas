package divascion.marfiandhi.labrplunhas.view.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import divascion.marfiandhi.labrplunhas.R
import divascion.marfiandhi.labrplunhas.model.News
import divascion.marfiandhi.labrplunhas.utils.PhotoFullPopupWindow
import kotlinx.android.synthetic.main.activity_home_details.*

class HomeDetailsActivity : AppCompatActivity() {

    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        news = intent.getParcelableExtra("news")
        supportActionBar?.title = news.title.toString()
        loadView()
    }

    private fun loadView() {
        if(news.image != null) {
            val transformation = object: Transformation {
                override fun key(): String {
                    return "transformation desiredWidth"
                }

                override fun transform(source: Bitmap?): Bitmap {
                    val targetWidth = news_details_image.width
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
                .into(news_details_image)
            news_details_image.setOnClickListener {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    PhotoFullPopupWindow(applicationContext, it, news.image.toString(), null)
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                }
            }
        } else {
            news_details_image.setImageDrawable(null)
        }
        news_details_title.text = news.title
        news_details_message.text = news.message
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PhotoFullPopupWindow(applicationContext, news_details_image, news.image.toString(), null)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
