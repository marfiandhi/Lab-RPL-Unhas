package divascion.marfiandhi.labrplunhas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News (
    var title: String? = null,

    var message: String? = null,

    var type: String? = null,

    var image: String? = null
):Parcelable