package divascion.marfiandhi.labrplunhas.utils

import android.annotation.SuppressLint
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
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.bumptech.glide.load.DataSource
import divascion.marfiandhi.labrplunhas.R

@SuppressLint("InflateParams")
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

    init {
        elevation = 5.0f
        this.view = contentView
        val closeButton = this.view.findViewById(R.id.ib_close) as ImageButton
        isOutsideTouchable = true

        isFocusable = true
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener {
            dismiss()
        }
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

                        loading.visibility = View.GONE
                        return false
                    }
                })

                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoView)

            showAtLocation(v, Gravity.CENTER, 0, 0)
        }
    }
}