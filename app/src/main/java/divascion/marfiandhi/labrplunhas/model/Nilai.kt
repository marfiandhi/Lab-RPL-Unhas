package divascion.marfiandhi.labrplunhas.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Nilai (
    var attempt1: Int = 0,

    var attempt2: Int = 0,

    var attempt3: Int = 0,

    var attempt4: Int = 0,

    var attempt5: Int = 0,

    var attempt6: Int = 0,

    var attempt7: Int = 0,

    var attempt8: Int = 0,

    var nilai1: Int = 0,

    var nilai2: Int = 0,

    var nilai3: Int = 0,

    var nilai4: Int = 0,

    var nilai5: Int = 0,

    var nilai6: Int = 0,

    var nilai7: Int = 0,

    var nilai8: Int = 0,

    var nim: String? = null
): Parcelable