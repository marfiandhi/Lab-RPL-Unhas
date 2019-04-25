package divascion.marfiandhi.labrplunhas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var email: String? = null,

    var name: String? = null,

    var nim: String? = null,

    var pbo: Boolean? = null,

    var pp: Boolean? = null,

    var male: Boolean? = null,

    var role: String? = null
): Parcelable