package divascion.marfiandhi.labrplunhas.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.view.ViewGroup
import android.view.Gravity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import android.widget.ProgressBar
import com.github.chrisbanes.photoview.PhotoView
import android.widget.ImageButton
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import divascion.marfiandhi.labrplunhas.BuildConfig
import divascion.marfiandhi.labrplunhas.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

@SuppressLint( "InflateParams")
class PhotoFullPopupWindow(internal var mContext: Context, v: View, imageUrl: String, bitmap: Bitmap?) :
    PopupWindow(
        (mContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.popup_photo_full,
            null
        ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    ) {

    internal var view: View
    internal var photoView: PhotoView
    internal var loading: ProgressBar
    internal var parent: ViewGroup
    internal var image: Bitmap? = null

    init {
        elevation = 5.0f
        this.view = contentView
        val saveButton = this.view.findViewById(R.id.ib_save) as ImageButton
        isOutsideTouchable = true

        isFocusable = true
        //---------Begin customising this popup--------------------

        photoView = view.findViewById(R.id.image_full)
        loading = view.findViewById(R.id.loading_image)
        photoView.maximumScale = 6f
        parent = photoView.parent as ViewGroup

        if (bitmap != null) {
            loading.visibility = View.GONE
                parent.background =
                    BitmapDrawable(
                        mContext.resources,
                        Constants.fastBlur(Bitmap.createScaledBitmap(bitmap, 50, 50, true))
                    )// ));
            photoView.setImageBitmap(bitmap)
        } else {
            loading.isIndeterminate = true
            loading.visibility = View.VISIBLE
            GlideApp.with(mContext).asBitmap()
                .load(imageUrl)

                .error(R.drawable.ic_exit_to_app_black_24dp)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.isIndeterminate = false
                        loading.setBackgroundColor(Color.LTGRAY)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Bitmap>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean): Boolean {
                            parent.background =
                                    BitmapDrawable(
                                        mContext.resources,
                                        Constants.fastBlur(Bitmap.createScaledBitmap(resource, 50, 50, true))
                                    )
                        photoView.setImageBitmap(resource)
                        image = resource

                        loading.visibility = View.GONE
                        return false
                    }
                })

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView)

            showAtLocation(v, Gravity.CENTER, 0, 0)
        }

        // saving image
        saveButton.setOnClickListener {
            if(image != null) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR).toString()
                val month = calendar.get(Calendar.MONTH).toString()
                val date = calendar.get(Calendar.DATE)
                val millis = calendar.timeInMillis

                var stream: OutputStream? = null
                try {
                    var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LabRPL")
                    if(!file.exists()) {
                        Log.e("Dir", "Making directory")
                        file.mkdirs()
                        Log.e("Dir", "Done create directory ${file.absolutePath}")
                    } else {
                        Log.e("Dir", "Not making any directory ${file.absolutePath}")
                    }
                    file = File(file, "Unhas_$year$month$date$millis.jpg")
                    stream = FileOutputStream(file)
                    image?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    try {
                        stream.flush()
                        stream.close()
                    } catch(e: Exception) {
                        Log.e("stream", e.message)
                    }
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val photo = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file)
                    mContext.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, photo))
                    Toast.makeText(mContext, mContext.getString(R.string.image_saved), Toast.LENGTH_LONG).show()
                    intent.setDataAndType(photo, "image/*")
                    val pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_ONE_SHOT)
                    val builder = NotificationCompat.Builder(mContext, "SAVED_IMAGE")
                        .setSmallIcon(R.drawable.ic_save_black_24dp)
                        .setContentTitle(mContext.getString(R.string.image_saved))
                        .setContentText(mContext.getString(R.string.tap_to_view))
                        .setLargeIcon(image)
                        .setStyle(NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    with(NotificationManagerCompat.from(mContext)) {
                        notify(19204213, builder.build())
                    }

                } catch(e: IOException) {
                    Toast.makeText(mContext, mContext.getString(R.string.failed_save), Toast.LENGTH_SHORT).show()
                    try {
                        stream?.flush()
                        stream?.close()
                    } catch(e: Exception) {
                        Log.e("stream", e.message)
                    }
                }
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.image_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}